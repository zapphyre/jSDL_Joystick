package org.asmus.model;

import java.util.Map;

public record AxisReading(Map<String, Integer> values, GamepadDevice device) {
}
