package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public class Plane3d {
    protected Vector point;
    protected Vector normal;

    public Plane3d() {
        point = Vector.ZERO;
        normal = Vector.ZERO;
    }

    public Plane3d(final Plane plane) {
        this.point = plane.getNormal().multiply(-plane.getD());
        this.normal = plane.getNormal();
    }

    public Plane3d(final Vector point, final Vector normal) {
        this.point = point;
        this.normal = normal;
    }

    public Plane3d(final Vector a, final Vector b, final Vector c) {
        this.point = a;
        this.normal = b.subtract(a).cross(c.subtract(a)); //(b-a).cross(c-a)
        this.normal = normal.normalize();
    }

    public boolean isNormalized() {
        return true;
    }

    /**
     * Builds a plane but doesn't worry about normalizing the normal.
     *
     * @param point  The point for the plane.
     * @param normal The normal for the plane.
     * @return
     */
    public static Plane3d NoNorm(final Vector point, final Vector normal) {
        final Plane3d temp = new Plane3d();
        temp.point = point;
        temp.normal = normal;

        return temp;
    }
}
