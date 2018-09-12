package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public final class Segment3d {
    private final Vector begin;
    private final Vector end;

    public Segment3d(final Vector begin, final Vector end) {
        this.begin = begin;
        this.end = end;
    }

    public Vector getDelta() {
        return end.subtract(begin);
    }

    public Line3d getLine() {
        return new Line3d(begin, end.subtract(begin));
    }

    public Line3d getReverseLine() {
        return new Line3d(end, begin.subtract(end));
    }

    public float getLength() {
        return getDelta().magnitude();
    }

    public float getLengthSquared() {
        return getDelta().magnitudeSquared();
    }

    public Range getRangeX() {
        return new Range(begin.x, end.x);
    }

    public Range getRangeY() {
        return new Range(begin.y, end.y);
    }

    public Range getRangeZ() {
        return new Range(begin.z, end.z);
    }

    public Vector atParam(final float t) {
        //begin + (end - begin) * t;
        return begin.add(end.subtract(begin).multiply(t));
    }
}
