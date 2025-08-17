package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.GamepadEvent;
import org.asmus.model.PolarCoords;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class AxisDigitizer {

    private static final int THRESHOLD = 2_000;
    private final Sinks.Many<GamepadEvent> qualifiedEventStream;

    public Consumer<PolarCoords> digitize(EButtonAxisMapping x) {
        return q -> qualifiedEventStream.tryEmitNext(GamepadEvent.builder()
                .type(x)
                .logicalEventType(translateAxisMove(q))
                .build());
    }

    public static ELogicalEventType translateAxisMove(PolarCoords coords) {
        double theta = coords.getTheta();
        double r = coords.getRadius();

        if (theta == 0 || r < THRESHOLD) {
            return ELogicalEventType.CENTER;
        } else if (theta >= -0.785 && theta < 0.785) {
            return ELogicalEventType.RIGHT;
        } else if (theta >= 0.785 && theta < 2.356) {
            return ELogicalEventType.DOWN;
        } else if (theta >= -2.356 && theta < -0.785) {
            return ELogicalEventType.UP;
        }

        return ELogicalEventType.LEFT;
    };
}
