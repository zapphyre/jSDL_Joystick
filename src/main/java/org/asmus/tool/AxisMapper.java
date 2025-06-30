package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class AxisMapper {

    GamepadEvent.GamepadEventBuilder builder = GamepadEvent.builder().position(1);

    public static Function<Map.Entry<String, Integer>, GamepadEvent> mapVertical =
            q -> q.getValue() > 0 ?
                    builder
                            .type(EButtonAxisMapping.DOWN)
                            .build() : builder
                    .type(EButtonAxisMapping.UP)
                    .build();

    public static Function<Map.Entry<String, Integer>, GamepadEvent> mapHorizontal =
            q -> q.getValue() > 0 ?
                    builder
                            .type(EButtonAxisMapping.RIGHT)
                            .build() : builder
                    .type(EButtonAxisMapping.LEFT)
                    .build();

    public static Predicate<Map.Entry<String, Integer>> valueFor(String axisName) {
        return q -> q.getKey().equals(axisName);
    }
}
