package io.bacta.shared.tre.math;

import lombok.Data;

/**
 * Created by crush on 5/13/2016.
 */
@Data
public class Circle {
    protected Vector center;
    protected float radius;

    public Circle(final Vector center, final float radius) {
        this.center = center;
        this.radius = radius;
    }

    public float getRadiusSquared() {
        return radius * radius;
    }

    public Range getRangeX() {
        return new Range(center.x - radius, center.x + radius);
    }

    public Range getRangeZ() {
        return new Range(center.z - radius, center.z + radius);
    }

    public Range getLocalRangeX() {
        return new Range(-radius, radius);
    }

    public Plane3d getPlane() {
        return new Plane3d(center, Vector.UNIT_Y);
    }
}
