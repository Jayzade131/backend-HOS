package com.org.hosply360.util.Others;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for scaling BigDecimal values to two decimal places consistently.
 */
public final class ScaleUtil {

    private ScaleUtil() {
        // Prevent instantiation
    }

    /**
     * Scales the given BigDecimal to two decimal places (HALF_UP rounding).
     *
     * @param value the input BigDecimal value
     * @return a new BigDecimal scaled to 2 decimal places
     */
    public static BigDecimal scaleToTwo(BigDecimal value) {
        return value != null
                ? value.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
