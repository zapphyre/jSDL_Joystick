package org.asmus.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@With
@Value
@SuperBuilder
@NonFinal
@Jacksonized
@RequiredArgsConstructor
public class PolarCoords {
    double radius;
    double theta;

    GamepadDevice device;

    public boolean isZero() {
        return radius == 0 && theta == 0;
    }
}
