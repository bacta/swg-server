package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public class Line3d {
    protected Vector point;
    protected Vector normal;

    public Line3d(final Vector point, final Vector normal) {
        this.point = point;
        this.normal = normal;
    }

    public Vector atParam(final float t) {
        return point.add(normal.multiply(t));
    }

    public void flip() {
        this.normal = this.normal.inverse();
    }

    public Line3d flipped() {
        return new Line3d(this.point, this.normal.inverse());
    }
}
