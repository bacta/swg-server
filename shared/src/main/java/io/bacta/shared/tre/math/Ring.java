package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 * <p>
 * Simple class to represent a 2D ring in the X-Z plane
 * <p>
 * This class is only semantically different from a circle - Circles are
 * 2-dimensional entities (i.e. a circle cut from paper) whereas a Ring
 * is a 1-dimensional entity (a piece of wire bent into a circle)
 */
@Getter
public final class Ring {
    private final Vector center;
    private final float radius;

    public Ring(final Vector center, final float radius) {
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
        return new Plane3d(center, new Vector(0.f, 1.f, 0.f));
    }

}
