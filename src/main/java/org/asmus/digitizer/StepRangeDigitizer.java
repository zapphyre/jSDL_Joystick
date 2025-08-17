package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.GamepadEvent;
import org.asmus.tool.Util;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

import static org.asmus.model.NamingConstants.MAX;
import static org.asmus.model.NamingConstants.MIN;

@RequiredArgsConstructor
public class StepRangeDigitizer {

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;
    private static final int numSegments = 7;
    private int lastSegmentIndex = -1; // Track previous segment
    long range = (long) MAX - MIN + 1; // Total values: 65535

    public Consumer<GamepadEvent> digitize(int segments) {
        Segmentize segmentizer = Util.pieces(segments, range, MIN);
        return q -> {
            int currentSegment = segmentizer.segmentize(q.getPosition());

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

}
