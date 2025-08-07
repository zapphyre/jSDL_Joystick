package org.asmus.builder;

import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.model.NamingConstants;
import org.asmus.model.PolarCoords;
import org.asmus.tool.EventMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.stream.Stream;

import static org.asmus.builder.AxisEventFactory.shouldPass;

public class AxisEventProcessorFactory {
    Sinks.Many<PolarCoords> leftPolarStream = Sinks.many().multicast().directBestEffort();
    Sinks.Many<PolarCoords> rightPolarStream = Sinks.many().multicast().directBestEffort();

    public RawArrowSource leftStickStream() {
        return events -> Stream.of(events)
                .filter(shouldPass(NamingConstants.LEFT_STICK_X, NamingConstants.LEFT_STICK_Y))
                .map(EventMapper.translateAxis(NamingConstants.LEFT_STICK_X, NamingConstants.LEFT_STICK_Y))
                .forEach(leftPolarStream::tryEmitNext);
    }

    public RawArrowSource reightStickStream() {
        return events -> Stream.of(events)
                .filter(shouldPass(NamingConstants.RIGHT_STICK_X, NamingConstants.RIGHT_STICK_Y))
                .map(EventMapper.translateAxis(NamingConstants.RIGHT_STICK_X, NamingConstants.RIGHT_STICK_Y))
                .forEach(rightPolarStream::tryEmitNext);
    }

    public Flux<PolarCoords> leftPolarFlux() {
        return leftPolarStream.asFlux();
    }

    public Flux<PolarCoords> rightPolarFlux() {
        return rightPolarStream.asFlux();
    }
}
