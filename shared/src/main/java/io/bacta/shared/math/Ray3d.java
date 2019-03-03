package io.bacta.shared.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public final class Ray3d {
    private final Vector point;
    private final Vector normal;

    public Ray3d(final Vector point, final Vector normal) {
        this.point = point;
        this.normal = normal;
    }

    public Line3d getLine() {
        return new Line3d(point, normal);
    }

    public Vector atParam(final float t) {
        return point.add(normal.multiply(t));
    }
}
