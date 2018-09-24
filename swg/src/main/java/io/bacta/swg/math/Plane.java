package io.bacta.swg.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 5/14/2016.
 * <p>
 * Class to contain data about a plane
 * <p>
 * The plane is described as a normal {A,B,C} and a D plane coefficient
 * making the following equation true: Ax + By + Cz + D = 0.
 * <p>
 * For now, I'm not making this immutable. If we see heavy usage or weird behavior, we may want to make it
 * immutable.
 */
@Getter
public final class Plane {
    /**
     * The normal will be a unit vector point orthogonal to the plane.
     */
    private Vector normal;
    /**
     * The D coefficient is the value that makes the plane equation true, given XYZ as the plane's normal:
     * X * x + Y * y + Z * z + D = 0. This value also represents the minimum distance from the origin to the plane.
     */
    private float d; //D coefficient

    /**
     * Constructs a default plane which will be pointed down the position Z axis and located at the origin.
     */
    public Plane() {
        this.normal = Vector.UNIT_Z;
    }

    /**
     * Constructs a plane with the specified normal and D-plane coefficient.
     *
     * @param normal The normal for the plane.
     * @param d      The D-plane coefficient.
     */
    public Plane(final Vector normal, final float d) {
        set(normal, d);
    }

    /**
     * Constructs a plane given three non-collinear points. The front half of the plane is the side from which
     * the vertices would be specified in a clockwise order.
     *
     * @param point0 First point on the plane.
     * @param point1 Second point on the plane.
     * @param point2 Third point on the plane.
     */
    public Plane(final Vector point0, final Vector point1, final Vector point2) {
        set(point0, point1, point2);
    }

    /**
     * Constructs a plane with the specified normal. The point on the plane is used to calculate the D-plane
     * coefficient.
     *
     * @param normal The normal for the plane.
     * @param point  The point on the plane.
     */
    public Plane(final Vector normal, final Vector point) {
        set(normal, point);
    }

    /**
     * Sets the plane to have the specified normal and D-plane coefficient.
     *
     * @param normal Normal for the plane.
     * @param d      The D-plane coefficient for the plane.
     */
    public void set(final Vector normal, final float d) {
        this.normal = normal;
        this.d = d;
    }

    /**
     * Sets the plane to have the specified normal and D-plane coefficient calculated from the point.
     *
     * @param normal Normal for the plane.
     * @param point  Point on the plane.
     */
    public void set(final Vector normal, final Vector point) {
        this.normal = normal;
        this.d = computeD(normal, point);
    }

    /**
     * Sets a plane given three non-collinear points. The front half of the plane is the side from which
     * the vertices would be specified in a clockwise order.
     *
     * @param point0 First point on the plane.
     * @param point1 Second point on the plane.
     * @param point2 Third point on the plane.
     */
    public void set(final Vector point0, final Vector point1, final Vector point2) {
        //Calculate the new normal direction
        //normal = (point0 - point2).cross(point1 - point0);
        this.normal = point0.subtract(point2).cross(point1.subtract(point0));

        //normalize the normal
        final Vector normalized = this.normal.normalize();

        if (normalized == null) {
            this.normal = Vector.UNIT_Z;
        } else {
            this.normal = normalized;
        }

        //Compute the D coefficient.
        this.d = computeD(normalized, point0);
    }

    /**
     * Sets the value of the plane to be that of the other plane with the specified transformation applied.
     *
     * @param other     The other plane.
     * @param transform The transformation to be applied.
     */
    public void set(final Plane other, final Transform transform) {
        this.normal = other.normal;
        this.d = other.d;

        transformLocalToParent(transform);
    }

    /**
     * Transform the plane by the specified transformation.
     *
     * @param transform The transformation to apply.
     */
    public void transformLocalToParent(final Transform transform) {
        final Vector point = transform.rotateTranslateLocalToParent(normal.multiply(-d));

        this.normal = transform.rotateLocalToParent(normal);
        this.d = computeD(normal, point);
    }

    /**
     * Transform the plane by the specified transformation.
     *
     * @param transform The transformation to apply.
     */
    public void transformParentToLocal(final Transform transform) {
        final Vector point = transform.rotateTranslateParentToLocal(normal.multiply(-d));

        this.normal = transform.rotateParentToLocal(normal);
        this.d = computeD(normal, point);
    }

    /**
     * Compute the signed distance from the point to the plane.
     * <p>
     * If the result is 0, the point is on the plane. If the result is position, the point is on the front half-space
     * of the plane. If the result is negative, the point is on the back half-space of the plane.
     *
     * @param point Point to test against the plane.
     * @return Signed distance from the point to the plane.
     */
    public float computeDistanceTo(final Vector point) {
        return normal.dot(point) + d;
    }

    /**
     * Find the intersection between a line segment and the plane.
     * <p>
     * If the line segment does intersect the plane, and the intersection pointer is non-NULL, then
     * intersection will be set to the point on the line segment that crosses the plane.
     *
     * @param point0 Start of the line segment.
     * @param point1 END of the line segment.
     * @return True if the line segment intersects the plane. Otherwise, false.
     */
    public IntersectionResult findIntersection(final Vector point0, final Vector point1) {
        final float t0 = computeDistanceTo(point0);
        final float t1 = computeDistanceTo(point1);

        //Check to make sure the endpoints span the plane.
        if ((t0 * t1) > 0.f)
            return new IntersectionResult(false);

        if (t0 == t1)
            return new IntersectionResult(point0, 0.f, true); //both zero.

        final float abst = t0 / (t0 - t1); //safe since sige of t0 is always opposite t1

        return new IntersectionResult(Vector.linearInterpolate(point0, point1, abst), abst, true);
    }

    /**
     * Find the directed intersection between a line segment and the plane. Will only detect the intersection if point0
     * is on the front side of the plane, and point1 is on the back side of the plane.
     * <p>
     * If the line segment does intersect the plane, and the intersection pointer is non-NULL, then intersection will
     * be set on the line segment that crosses the plane.
     *
     * @param point0 Start of the line segment.
     * @param point1 END of the line segment.
     * @return
     */
    public IntersectionResult findDirectedIntersection(final Vector point0, final Vector point1) {
        final float t0 = computeDistanceTo(point0);
        final float t1 = computeDistanceTo(point1);

        //check to make t0 is on the front side of the plane and t1 is on the back side of the plane
        if (t0 < 0.f || t1 > 0.f)
            return new IntersectionResult(false);

        if (t0 == t1)
            return new IntersectionResult(point0, 0.f, true); //both zero

        //solve parametric equation to find the intersection point
        final float abst = t0 / (t0 - t1); //save since sign of t0 is always opposite t1

        return new IntersectionResult(Vector.linearInterpolate(point0, point1, abst), abst, true);
    }

    /**
     * Find point projected onto the plane.
     *
     * @param point The point to project onto the plane.
     * @return The projected point onto the plane.
     */
    public Vector project(final Vector point) {
        return point.subtract(normal.multiply(computeDistanceTo(point)));
    }

    /**
     * Compute the d coefficient. Uses the equation d = 1(ax + by + cz) = -(normal dot point)
     *
     * @param normal The normal vector from which to compute D.
     * @param point  The point from which to compute D.
     * @return Plane D coefficient.
     */
    public static float computeD(final Vector normal, final Vector point) {
        return -normal.dot(point);
    }

    /**
     * Encapsulates the result of a findIntersection or findDirectedIntersection method.
     */
    @AllArgsConstructor
    public static final class IntersectionResult {
        /**
         * Intersection of the point and the plane (may be null)
         */
        public final Vector intersection;
        /**
         * Parameterized time from 0.f -> 1.f
         */
        public final float t;
        /**
         * True if the line segment intersects the plane from front-to-rear. Otherwise, false.
         */
        public final boolean intersects;

        public IntersectionResult(final boolean intersects) {
            this.intersects = intersects;
            this.intersection = null;
            this.t = 0;
        }
    }
}
