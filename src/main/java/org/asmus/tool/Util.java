package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.digitizer.Segmentize;

import static org.asmus.model.NamingConstants.MAX;

@UtilityClass
public class Util {

    public Segmentize pieces(int numSegments, long range, long min) {
        long segmentSize = range / numSegments;
        return value -> {
            int segmentIndex;
            if (value == MAX) {
                segmentIndex = numSegments - 1; // Place MAX in last segment
            } else {
                // Shift value to [0, range-1] and divide by segment size
                long shiftedValue = (long) value - min; // [0, 65534]
                segmentIndex = (int) (shiftedValue / segmentSize);
                // Ensure boundary edge cases don't overshoot
                if (segmentIndex >= numSegments) {
                    segmentIndex = numSegments - 1;
                }
            }

            return segmentIndex;
        };
    }
}