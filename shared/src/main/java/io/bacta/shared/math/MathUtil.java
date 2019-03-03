package io.bacta.shared.math;

/**
 * Created by crush on 5/13/2016.
 */
public class MathUtil {
    public static final float PI_TIMES_2 = (float) (Math.PI * 2.f);
    public static final float PI_OVER_2 = (float) (Math.PI / 2.f);
    public static final float PI_OVER_3 = (float) (Math.PI / 3.f);
    public static final float PI_OVER_4 = (float) (Math.PI / 4.f);
    public static final float PI_OVER_6 = (float) (Math.PI / 6.f);
    public static final float PI_OVER_8 = (float) (Math.PI / 8.f);
    public static final float PI_OVER_12 = (float) (Math.PI / 12.f);
    public static final float PI_OVER_16 = (float) (Math.PI / 16.f);
    public static final float PI_OVER_180 = (float) (Math.PI / 180.f);

    public static final float E = 2.7182818284590452f;

    /**
     * Make sure a value is between two values.
     *
     * @param min   The minimum value.
     * @param value The value.
     * @param max   The maximum value.
     * @return If the value is less than min, return min. If the value is greater than max, return max. Otherwise, return value.
     */
    public static byte clamp(final byte min, final byte value, final byte max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static short clamp(final short min, final short value, final short max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static int clamp(final int min, final int value, final int max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static long clamp(final long min, final long value, final long max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    public static float clamp(final float min, final float value, final float max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }


    /**
     * Linear interpolate from start to end along t, where t is from 0..a.
     * <p>
     * t must be from 0..1
     *
     * @param start
     * @param end
     * @param t
     * @return A value from start to end.
     */
    public static byte linearInterpolate(final byte start, final byte end, final float t) {
        return (byte) ((end - start) * t + start);
    }

    public static short linearInterpolate(final short start, final short end, final float t) {
        return (short) ((end - start) * t + start);
    }

    public static int linearInterpolate(final int start, final int end, final float t) {
        return (int) ((end - start) * t + start);
    }

    public static long linearInterpolate(final long start, final long end, final float t) {
        return (long) ((end - start) * t + start);
    }

    public static float linearInterpolate(final float start, final float end, final float t) {
        return (end - start) * t + start;
    }


    /**
     * A non-linear, smooth interpolation from start to end along t, where t is from 0..1.
     * <p>
     * (-2 * (end - start) * t^3) + (3 * (end - start) * t^2) + start
     * <p>
     * t must be from 0..1
     *
     * @param start
     * @param end
     * @param interpolation
     * @return A value from start to end
     */
    public static int cubicInterpolate(final byte start, final byte end, final float t) {
        final float tSquared = t * t;
        final float tCubed = tSquared * t;
        final byte diff = (byte) (end - start);

        return (byte) ((diff * (tCubed * -2.f)) + diff * (tSquared * 3.f) + start);
    }

    public static short cubicInterpolate(final short start, final short end, final float t) {
        final float tSquared = t * t;
        final float tCubed = tSquared * t;
        final short diff = (short) (end - start);

        return (short) ((diff * (tCubed * -2.f)) + diff * (tSquared * 3.f) + start);
    }

    public static int cubicInterpolate(final int start, final int end, final float t) {
        final float tSquared = t * t;
        final float tCubed = tSquared * t;
        final int diff = end - start;

        return (int) ((diff * (tCubed * -2.f)) + diff * (tSquared * 3.f) + start);
    }

    public static long cubicInterpolate(final long start, final long end, final float t) {
        final float tSquared = t * t;
        final float tCubed = tSquared * t;
        final long diff = end - start;

        return (long) ((diff * (tCubed * -2.f)) + diff * (tSquared * 3.f) + start);
    }

    public static float cubicInterpolate(final float start, final float end, final float t) {
        final float tSquared = t * t;
        final float tCubed = tSquared * t;
        final float diff = end - start;

        return (diff * (tCubed * -2.f)) + diff * (tSquared * 3.f) + start;
    }


    public static boolean withinRangeInclusiveInclusive(final long rangeMin, final long value, final long rangeMax) {
        return (value >= rangeMin) && (value <= rangeMax);
    }

    public static boolean withinRangeInclusiveInclusive(final float rangeMin, final float value, final float rangeMax) {
        return (value >= rangeMin) && (value <= rangeMax);
    }

    public static boolean withinRangeExclusiveExclusive(final long rangeMin, final long value, final long rangeMax) {
        return (value > rangeMin) && (value < rangeMax);
    }

    public static boolean withinRangeExclusiveExclusive(final long rangeMin, final float value, final float rangeMax) {
        return (value > rangeMin) && (value < rangeMax);
    }

    public static boolean withinEpsilonInclusive(final long base, final long value, final long epsilon) {
        return (value >= base - epsilon) && (value <= base + epsilon);
    }

    public static boolean withinEpsilonInclusive(final float base, final float value, final float epsilon) {
        return (value >= base - epsilon) && (value <= base + epsilon);
    }

    public static boolean withinEpsilonExclusive(final long base, final long value, final long epsilon) {
        return (value > base - epsilon) && (value < base + epsilon);
    }

    public static boolean withinEpsilonExclusive(final float base, final float value, final float epsilon) {
        return (value > base - epsilon) && (value < base + epsilon);
    }

    /**
     * Equation from http://mathworld.wolfram.com/NormalDistribution.html
     *
     * @param variate
     * @param standardDeviation
     * @param mean
     * @return
     */
    public float gaussianDistribution(final float variate, final float standardDeviation, final float mean) {
        final float variateMinusMean = (variate - mean);
        return (float) ((1.f / (standardDeviation * Math.sqrt(2.f * Math.PI))) * Math.pow(E, -(variateMinusMean * variateMinusMean) / (2.f * standardDeviation * standardDeviation)));
    }

    public boolean withinEpsilon(final float rhs, final float lhs) {
        return withinEpsilon(rhs, lhs, 1.0e-3f);
    }

    public boolean withinEpsilon(final float rhs, final float lhs, final float epsilon) {
        return ((lhs - epsilon) <= rhs) && (rhs <= (lhs + epsilon));
    }
}
