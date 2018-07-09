package io.bacta.shared.tre.math;

import lombok.Data;

import java.util.Random;

/**
 * Created by crush on 5/13/2016.
 * <p>
 * Represents a 1D range (essentially a 1D bounding box)
 */
@Data
public class Range {
    public static final Range EMPTY = new Range(Float.MAX_VALUE, -Float.MAX_VALUE);
    public static final Range INF = new Range(-Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Range PLUS_INF = new Range(0.f, -Float.MAX_VALUE);
    public static final Range NEG_INF = new Range(-Float.MAX_VALUE, 0.f);
    public static final Range UNIT = new Range(0.f, 1.f);

    private static final Random random = new Random();

    protected float min;
    protected float max;

    public Range() {
        min = Float.MIN_VALUE;
        max = -Float.MAX_VALUE;
    }

    public Range(float newMin, float newMax) {
        this.min = newMin;
        this.max = newMax;
    }

    public void set(final float newMin, final float newMax) {
        setMin(newMin);
        setMax(newMax);
    }

    public boolean isBelow(final Range range) {
        return max < range.min;
    }

    public boolean isAbove(final Range range) {
        return min > range.max;
    }

    public boolean isTouchingBelow(final Range range) {
        return max == range.min;
    }

    public boolean isTouchingAbove(final Range range) {
        return min == range.max;
    }

    public boolean isEmpty() {
        return max < min;
    }

    public boolean contains(final float value) {
        return value >= min && value <= max;

    }

    public Range add(final float offset) {
        return new Range(min + offset, max + offset);
    }

    public boolean isLessThan(final Range range) {
        return min < range.min || min == range.min && max < range.max;
    }

    public boolean isEqualTo(final Range range) {
        return min == range.min && max == range.max;
    }

    public boolean isNotEqualTo(final Range range) {
        return min != range.min || max != range.max;
    }

    public float clamp(final float value) {
        if (isEmpty())
            return value;

        return MathUtil.clamp(min, value, max);
    }

    public float linearInterpolate(final float t) {
        return MathUtil.linearInterpolate(min, max, t);
    }

    public float cubicInterpolate(final float t) {
        return MathUtil.cubicInterpolate(min, max, t);
    }

    public float random() {
        return (min > max)
                ? random.nextFloat() * max + min
                : random.nextFloat() * min + max;
    }

    public static Range enclose(final Range a, final float value) {
        if (a.isEmpty())
            return new Range(value, value);
        else
            return new Range(Math.min(a.getMin(), value), Math.max(a.getMax(), value));
    }

    public static Range enclose(final Range a, final Range b) {
        if (a.isEmpty()) {
            if (b.isEmpty())
                return Range.EMPTY;
            else
                return b;
        } else {
            if (b.isEmpty())
                return a;
            else
                return new Range(Math.min(a.getMin(), b.getMin()), Math.max(a.getMax(), b.getMax()));
        }
    }

    public static Range enclose(final Range a, final Range b, final Range c) {
        return enclose(enclose(a, b), c);
    }

    public static Range enclose(final Range a, final Range b, final Range c, final Range d) {
        return enclose(enclose(a, b), enclose(c, d));
    }
}
