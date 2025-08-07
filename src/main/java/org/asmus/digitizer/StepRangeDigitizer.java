package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.GamepadEvent;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

import static org.asmus.model.NamingConstants.MAX;
import static org.asmus.model.NamingConstants.MIN;

@RequiredArgsConstructor
public class StepRangeDigitizer {

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;
    private final int numSegments = 7;
    private int lastSegmentIndex = -1; // Track previous segment
    long range = (long) MAX - MIN + 1; // Total values: 65535

    public Consumer<GamepadEvent> digitize() {
        return q -> {

            int currentSegment = processInput(q.getPosition());

            ELogicalEventType type = currentSegment > lastSegmentIndex ?
                    ELogicalEventType.STEP_POSITIVE : ELogicalEventType.STEP_NEGATIVE;

            if (lastSegmentIndex != -1 && lastSegmentIndex != currentSegment)
                qualifiedEventStream.tryEmitNext(q
                        .withLogicalEventType(type)
                        .withPosition(currentSegment)
                );

            lastSegmentIndex = currentSegment;
        };
    }

    private final long segmentSize = range / numSegments;

    int processInput(int value) {
        int segmentIndex;
        if (value == MAX) {
            segmentIndex = numSegments - 1; // Place MAX in last segment
        } else {
            // Shift value to [0, range-1] and divide by segment size
            long shiftedValue = (long) value - MIN; // [0, 65534]
            segmentIndex = (int) (shiftedValue / segmentSize);
            // Ensure boundary edge cases don't overshoot
            if (segmentIndex >= numSegments) {
                segmentIndex = numSegments - 1;
            }
        }

        return segmentIndex;
    }
}
