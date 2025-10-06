package org.asmus.builder.closure.button;

import org.asmus.model.AxisReading;

@FunctionalInterface
public interface RawArrowSource {

    void processArrowEvents(AxisReading axisStates);
}
