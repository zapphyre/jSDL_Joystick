package org.asmus.builder.closure.axis;

import org.asmus.model.PolarCoords;
import reactor.core.publisher.Flux;

import java.util.Map;

@FunctionalInterface
public interface AxisProcessor {

    void process(Map<String, Integer> events);
}
