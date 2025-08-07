package org.asmus.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.*;
import org.bbi.linuxjoy.LinuxJoystick;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.*;
import static java.util.stream.Collectors.toMap;
import static org.asmus.tool.SdlStringMapper.translate;

@Slf4j
public class JoyWorker {

    private final Sinks.Many<List<TimedValue>> buttonStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<Map<String, Integer>> axisStream = Sinks.many().multicast().directBestEffort();
    private final Sinks.Many<SourceState> sourceStateStream = Sinks.many().multicast().directBestEffort();
    private ScheduledFuture<?> pollerCloseable;
    private SourceState.SourceStateBuilder sourceStateBuilder = SourceState.builder()
            .connected(true);

    public JoyWorker() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (pollerCloseable != null)
                pollerCloseable.cancel(true);

            axisStream.tryEmitComplete();
            buttonStream.tryEmitComplete();
        }));
    }

    Predicate<ButtonNamePosition> isAxis = ButtonNamePosition::isAxis;
    Predicate<ButtonNamePosition> isHat = ButtonNamePosition::isHat;
    Predicate<ButtonNamePosition> axisOrHat = isAxis.or(isHat);

    @SneakyThrows
    public Function<Controller, Runnable> watchingDevice(String devicePath) {
        return controller -> {
            LinuxJoystick j = new LinuxJoystick(controller.device(), controller.buttons(), controller.axes());

            sourceStateBuilder = sourceStateBuilder.devicePath(devicePath);

            List<ButtonNamePosition> mappings = translate(controller.mapping());

            List<ButtonNamePosition> axisMappings = mappings.stream()
                    .filter(axisOrHat)
                    .toList();

            List<ButtonNamePosition> buttonMappings = mappings.stream()
                    .filter(not(axisOrHat))
                    .toList();

            ControllerDevice device = new ControllerDevice(axisMappings, buttonMappings, j);

            processEvents(device);
            sourceStateStream.tryEmitNext(sourceStateBuilder.connected(true).build());

            return () -> {
                j.close();
                pollerCloseable.cancel(true);
                sourceStateStream.tryEmitNext(sourceStateBuilder.connected(false).build());
            };
        };
    }

    Function<List<ButtonNamePosition>, List<ButtonNamePosition>> mapToPosition(Predicate<ButtonNamePosition> predicate) {
        return q -> q.stream()
                .filter(predicate)
                .toList();
    }

    @SneakyThrows
    public void processEvents(ControllerDevice dev) {
        Thread.sleep(100);

        LinuxJoystick j = dev.joystick();
        j.open();

        JoyStateMapper jMapper = new JoyStateMapper(j);

        pollerCloseable = Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            while (true) {
                if (!j.isDeviceOpen()) return;

                j.poll();

                if (!j.isChanged()) return;

                Map<String, Integer> axisVals = dev.axisMappings().stream()
                        .map(jMapper.toIV(LinuxJoystick::getAxisState))
                        .collect(toMap(InputValue::name, InputValue::value));

                axisStream.tryEmitNext(axisVals);

                List<TimedValue> buttonVals = dev.buttonMappings().stream()
                        .map(jMapper.toIV(LinuxJoystick::getButtonState))
                        .map(TimedValue::new)
                        .toList();

                sourceStateStream.tryEmitNext(sourceStateBuilder.connected(true).build());
                buttonStream.tryEmitNext(buttonVals);
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    public Flux<List<TimedValue>> getButtonStream() {
        return buttonStream.asFlux();
    }

    public Flux<Map<String, Integer>> getAxisStream() {
        return axisStream.asFlux();
    }

    public Flux<SourceState> getSourceStateStream() {
        return sourceStateStream.asFlux();
    }

    record JoyStateMapper(LinuxJoystick joystick) {
        <T> Function<ButtonNamePosition, InputValue<T>> toIV(BiFunction<LinuxJoystick, Integer, T> getter) {
            return q -> new InputValue<>(getter.apply(joystick, q.getPosition()), q.getButtonName());
        }
    }
}