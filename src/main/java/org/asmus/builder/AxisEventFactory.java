package org.asmus.builder;

import lombok.experimental.UtilityClass;
import org.asmus.builder.closure.axis.PolarCoordsProducer;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.NamingConstants;
import org.asmus.model.TriggerPosition;
import org.asmus.service.JoyWorker;
import org.asmus.tool.EventMapper;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.asmus.builder.IntrospectedEventFactory.actionFor;

@UtilityClass
public class AxisEventFactory {

    public PolarCoordsProducer leftStickStream() {
        return q -> q.getAxisStream()
                .filter(shouldPass(NamingConstants.LEFT_STICK_X, NamingConstants.LEFT_STICK_Y, new AtomicBoolean()))
                .map(EventMapper.translateAxis(NamingConstants.LEFT_STICK_X, NamingConstants.LEFT_STICK_Y));
    }

    public PolarCoordsProducer rightStickStream() {
        return q -> q.getAxisStream()
                .filter(shouldPass(NamingConstants.RIGHT_STICK_X, NamingConstants.RIGHT_STICK_Y, new AtomicBoolean()))
                .map(EventMapper.translateAxis(NamingConstants.RIGHT_STICK_X, NamingConstants.RIGHT_STICK_Y));
    }

    static Predicate<Map<String, Integer>> shouldPass(String xAxis, String yAxis, AtomicBoolean zeroState) {
        return coords -> {
            int x = coords.getOrDefault(xAxis, 0);
            int y = coords.getOrDefault(yAxis, 0);

            boolean isZero = x == 0 && y == 0;

            if (!isZero) {
                zeroState.set(false);
                return true;
            }

            return !zeroState.getAndSet(true);
        };
    }

}
