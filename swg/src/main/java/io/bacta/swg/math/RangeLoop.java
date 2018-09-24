package io.bacta.swg.math;

import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 * <p>
 * This class requires a bit of epxlanation - it represents a 1d range of numbers defined over the numerical
 * ring [0,1]. Because the ring is closed, certain operations aren't well-defined - there's no way to express
 * the notions of greater than or less than nor is there a unique way to express the distance between two values.
 * <p>
 * Nevertheless, this class is very useful for representing ranges of things that are logically loops, like angles
 * and such.
 * <p>
 * Definitions -
 * [0, 0.2] - All numbers between 0 and 0.2, inclusive
 * [0.4, 0.2] - The union of [0.4, 1] and [0, 0.2]
 * <p>
 * This class really out to be called RingRange
 */
@Getter
public final class RangeLoop {
    public static final RangeLoop EMPTY = new RangeLoop();
    public static final RangeLoop FULL = new RangeLoop(0.f, 1.f);

    private final float min;
    private final float max;

    public RangeLoop() {
        this.min = -1.f;
        this.max = -1.f;
    }

    public RangeLoop(float min, float max) {
        if (Math.abs(max - min) > 1.f) {
            this.min = 0.f;
            this.max = 1.f;
        } else {
            this.min = clip(min);
            this.max = clip(max);
        }
    }

    public boolean isEmpty() {
        return (min == -1.f) && (max == -1.f);
    }

    public boolean isFull() {
        return (min == 0.f) && (max == 1.f);
    }

    public float atParam(final float time) {
        return min <= max
                ? min + (max - min) * time
                : clip(min + (max - min + 1.f) * time);
    }

    public float getSize() {
        return min <= max ? max - min : max - min + 1.f;
    }

    public boolean overlapPositive(final RangeLoop rangeLoop) {
        if (isEmpty() || rangeLoop.isEmpty()) return false;
        if (isFull() || rangeLoop.isFull()) return true;

        if (!containsExclusive(rangeLoop.min)) return false;
        if (containsExclusive(rangeLoop.max)) return false;

        if (!rangeLoop.containsExclusive(max)) return false;
        if (rangeLoop.containsExclusive(min)) return false;

        return true;
    }

    public boolean overlapNegative(final RangeLoop rangeLoop) {
        if (isEmpty() || rangeLoop.isEmpty()) return false;
        if (isFull() || rangeLoop.isFull()) return true;

        if (!containsExclusive(rangeLoop.max)) return false;
        if (containsExclusive(rangeLoop.min)) return false;

        if (!rangeLoop.containsExclusive(min)) return false;
        if (rangeLoop.containsExclusive(max)) return false;

        return true;
    }

    public boolean disjointInclusive(final RangeLoop rangeLoop) {
        if (isEmpty() || rangeLoop.isEmpty()) return false;
        if (isFull() || rangeLoop.isFull()) return true;

        if (containsInclusive(rangeLoop.min)) return false;
        if (containsInclusive(rangeLoop.max)) return false;

        if (rangeLoop.containsInclusive(min)) return false;
        if (rangeLoop.containsInclusive(max)) return false;

        return true;
    }

    public boolean disjointExclusive(final RangeLoop rangeLoop) {
        if (isEmpty() || rangeLoop.isEmpty()) return true;
        if (isFull() || rangeLoop.isFull()) return false;

        if (containsExclusive(rangeLoop.min)) return false;
        if (containsExclusive(rangeLoop.max)) return false;

        if (rangeLoop.containsExclusive(min)) return false;
        if (rangeLoop.containsExclusive(max)) return false;

        return true;
    }

    public boolean containsInclusive(float value) {
        if (isFull()) return true;
        if (isEmpty()) return false;

        value = clip(value);

        if (max == min)
            return value == min;
        else if (max > min)
            return (value >= min) && (value <= max);
        else
            return (value >= min) || (value <= max);
    }

    public boolean containsExclusive(float value) {
        if (isFull()) return true;
        if (isEmpty()) return false;

        value = clip(value);

        if (max == min)
            return value == min;
        else if (max > min)
            return (value > min) && (value < max);
        else
            return (value > min) || (value < max);
    }

    public boolean contains(final RangeLoop rangeLoop) {
        if (isEmpty() || rangeLoop.isEmpty()) return false;
        if (isFull()) return true;
        if (rangeLoop.isFull()) return false;

        if (!containsInclusive(rangeLoop.min)) return false;
        if (!containsInclusive(rangeLoop.max)) return false;

        return true;
    }

    public static float clip(final float value) {
        return value - (float) Math.floor(value);
    }

    public static float distancePositive(float a, float b) {
        a = clip(a);
        b = clip(b);

        return (b > a) ? -a + b : 1 - a + b;
    }

    public static float distanceNegative(float a, float b) {
        a = clip(a);
        b = clip(b);

        return (b > a) ? 1 + a - b : a - b;
    }

    public static boolean containsInclusive(float min, float max, float value) {
        min = clip(min);
        max = clip(max);
        value = clip(value);

        return max > min
                ? (value >= min) && (value <= max)
                : (value >= min) || (value <= max);
    }

    public static RangeLoop enclose(final float a, final float b) {
        return distancePositive(a, b) < 0.5f
                ? new RangeLoop(a, b)
                : new RangeLoop(b, a);
    }

    public static RangeLoop enclose(final RangeLoop a, final float b) {
        if (a.isEmpty())
            return new RangeLoop(b, b);
        else if (a.isFull())
            return FULL;
        else if (a.containsInclusive(b))
            return a;
        else {
            final float distA = distancePositive(a.getMax(), b);
            final float distB = distanceNegative(a.getMin(), b);

            return distA < distB
                    ? new RangeLoop(a.getMin(), b)
                    : new RangeLoop(b, a.getMax());
        }
    }

    public static RangeLoop enclose(final RangeLoop a, final RangeLoop b) {
        final boolean fullA = a.isFull();
        final boolean fullB = b.isFull();

        if (fullA || fullB)
            return FULL;

        final boolean emptyA = a.isEmpty();
        final boolean emptyB = b.isEmpty();

        if (emptyA && emptyB)
            return EMPTY;
        else if (emptyA)
            return b;
        else if (emptyB)
            return a;

        if (a.contains(b))
            return a;
        else if (b.contains(a))
            return b;
        else if (a.disjointExclusive(b)) {
            final float distA = distancePositive(a.getMin(), b.getMax());
            final float distB = distanceNegative(a.getMax(), b.getMin());

            return distA < distB
                    ? new RangeLoop(a.getMin(), b.getMax())
                    : new RangeLoop(b.getMin(), a.getMax());
        }

        final boolean overlapAB = a.overlapPositive(b);
        final boolean overlapBA = b.overlapPositive(a);

        if (overlapAB && overlapBA)
            return new RangeLoop(0.f, 1.f);
        else if (overlapAB)
            return new RangeLoop(a.getMin(), b.getMax());
        else if (overlapBA)
            return new RangeLoop(b.getMin(), a.getMax());

        final boolean touchAB = (a.getMax() == b.getMin());
        final boolean touchBA = (b.getMax() == a.getMin());

        if (touchAB && touchBA)
            return new RangeLoop(0.f, 1.f);
        else if (touchAB)
            return new RangeLoop(a.getMin(), b.getMax());
        else if (touchBA)
            return new RangeLoop(b.getMin(), a.getMax());

        //Something went wrong - enclose is broken.
        return new RangeLoop(0.f, 1.f);
    }

    private static float distance(float a, float b) {
        a = clip(a);
        b = clip(b);

        final float positive = distancePositive(a, b);

        return positive <= 0.5f ? positive : positive - 1;
    }
}
