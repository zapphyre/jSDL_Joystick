package org.asmus.tool;

import lombok.experimental.UtilityClass;
import org.asmus.model.AxisReading;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.EPolarDirection;
import org.asmus.model.PolarCoords;

import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class EventMapper {

    public static Function<AxisReading, PolarCoords> translateAxis(String x,
                                                                   String y) {
        return q -> {
            Map<String, Integer> coords = q.values();

            int yAxisLeft = coords.get(y);
            int xAxisLeft = coords.get(x);

            double theta = getTheta(xAxisLeft, yAxisLeft);
            double r = getR(xAxisLeft, yAxisLeft);

            return PolarCoords.builder()
                    .radius(r)
                    .theta(theta)
                    .build();
        };
    }

    static double getTheta(double x, double y) {
        return Math.atan2(y, x);
    }

    static double getR(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }
}
