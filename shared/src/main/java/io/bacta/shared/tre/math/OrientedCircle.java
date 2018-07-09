package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 */
@Getter
public class OrientedCircle {
    protected Vector center;
    protected Vector axis;
    protected float radius;

    public OrientedCircle(final Vector center, final Vector axis, final float radius) {
        this.center = center;
        this.axis = axis;
        this.radius = radius;
    }

    public OrientedCircle(final Circle circle) {
        this.center = circle.getCenter();
        this.axis = new Vector(0.f, 1.f, 0.f);
        this.radius = circle.getRadius();
    }
}
