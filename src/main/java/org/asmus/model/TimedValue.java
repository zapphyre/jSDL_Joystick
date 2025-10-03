package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@RequiredArgsConstructor
@EqualsAndHashCode
public class TimedValue {
    @EqualsAndHashCode.Exclude
    long time = System.currentTimeMillis();

    String name;
    boolean value;
    GamepadDevice device;

    public TimedValue(InputValue<Boolean> iv) {
        this.name = iv.name();
        this.value = iv.value();
        this.device = iv.device();
    }

}
