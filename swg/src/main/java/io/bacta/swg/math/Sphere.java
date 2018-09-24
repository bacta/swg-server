package io.bacta.swg.math;

import lombok.Getter;

/**
 * Created by crush on 4/23/2016.
 */
@Getter
public final class Sphere {
    public static final Sphere ZERO = new Sphere(0.f, 0.f, 0.f, 0.f);
    public static final Sphere UNIT = new Sphere(0.f, 0.f, 0.f, 1.f);

    private final Vector center;
    private final float radius;

    /**
     * Constructs a default sphere.
     * <p>
     * The sphere will be centered at (0, 0, 0) and have a radius of 0.
     */
    public Sphere() {
        center = Vector.ZERO;
        radius = 0;
    }

    /**
     * Constructs a sphere.
     *
     * @param center The center point for the sphere.
     * @param radius The radius of the sphere.
     */
    public Sphere(final Vector center, final float radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * Constructs a sphere.
     *
     * @param x      The X center point of the sphere.
     * @param y      The Y center point of the sphere.
     * @param z      The z center point of the sphere.
     * @param radius The radius of the sphere.
     */
    public Sphere(final float x, final float y, final float z, final float radius) {
        this.center = new Vector(x, y, z);
        this.radius = radius;
    }

    public Vector getAxisX() {
        return Vector.UNIT_X;
    }

    public Vector getAxisY() {
        return Vector.UNIT_Y;
    }

    public Vector getAxisZ() {
        return Vector.UNIT_Z;
    }

    public float getExtentX() {
        return getRadius();
    }

    public float getExtentY() {
        return getRadius();
    }

    public float getExtentZ() {
        return getRadius();
    }

    public Circle getCircle() {
        return new Circle(center, radius);
    }

    /**
     * Check if a point is inside the sphere.
     *
     * @param point The point to check.
     * @return True if the point is in the sphere, otherwise false.
     */
    public boolean contains(final Vector point) {
        return center.magnitudeBetweenSquared(point) <= (radius * radius);
    }

    /**
     * Check if the sphere contains another sphere.
     *
     * @param other The other sphere.
     * @return True if the second sphere is entirely contained within this sphere.
     */
    public boolean contains(final Sphere other) {
        return other.radius <= radius &&
                center.magnitudeBetweenSquared(other.center) <= ((radius - other.radius) * (radius - other.radius));
    }

    public boolean intersectsLine(final Vector startPoint, final Vector endPoint) {
        return contains(center.findClosestPointOnLine(startPoint, endPoint).point);
    }

    public boolean intersectionsLineSegment(final Vector startPoint, final Vector endPoint) {
        return contains(center.findClosestPointOnLineSegment(startPoint, endPoint).point);
    }

    /**
     * Dtermine if a ray intersects a sphere.
     *
     * @param startPoint          Where the ray starts.
     * @param normalizedDirection The direction of the sphere.
     * @return True if it intersects the sphere, otherwise false.
     */
    public boolean intersectsRay(final Vector startPoint, final Vector normalizedDirection) {
        if (contains(startPoint))
            return true;

        final Vector rayStartPointToCenter = center.subtract(startPoint);

        final float originRayDot = rayStartPointToCenter.dot(normalizedDirection);

        if (originRayDot < 0.f)
            return false;

        final float distanceSquared = rayStartPointToCenter.magnitudeSquared();
        final float distanceToRaySquared = (radius * radius) - (distanceSquared - originRayDot * originRayDot);

        return distanceToRaySquared > 0.0f;
    }

    public boolean intersectsSphere(final Sphere other) {
        final float d = center.magnitudeBetweenSquared(other.getCenter());
        final float r = (radius + other.getRadius()) * (radius + other.getRadius());
        return d < r;
    }

    public boolean intersectsCone(final Vector coneBase, final Vector coneNormal, final float coneAngleRadians) {
        final float angleSine = (float) Math.sin(coneAngleRadians);
        final float angleCosine = (float) Math.cos(coneAngleRadians);
        final float angleInverseSine = angleSine > Float.MIN_VALUE ? 1.0f / angleSine : 0.f;
        final float angleCosineSquared = angleCosine * angleCosine;

        final Vector vectorToSphere = center.subtract(coneBase);
        final Vector intersectPosition = vectorToSphere.add(coneNormal.multiply(radius * angleInverseSine));

        float magnitudeOfIntersectionSquared = intersectPosition.magnitudeSquared();
        float angleBetweenSourceAndIntersection = intersectPosition.dot(coneNormal);

        boolean inCone = false;

        if (angleBetweenSourceAndIntersection > Float.MIN_VALUE &&
                (angleBetweenSourceAndIntersection * angleBetweenSourceAndIntersection) >= (magnitudeOfIntersectionSquared * angleCosineSquared)) {

            final float angleSinSquared = angleSine * angleSine;
            magnitudeOfIntersectionSquared = vectorToSphere.magnitudeSquared();
            angleBetweenSourceAndIntersection = -vectorToSphere.dot(coneNormal);

            if (angleBetweenSourceAndIntersection > Float.MIN_VALUE &&
                    (angleBetweenSourceAndIntersection * angleBetweenSourceAndIntersection) >= (magnitudeOfIntersectionSquared * angleSinSquared)) {
                final float rangeSquared = radius * radius;
                inCone = magnitudeOfIntersectionSquared <= rangeSquared;
            } else {
                inCone = true;
            }
        }

        return inCone;
    }

    public Vector closestPointOnSphere(final Vector point) {
        final Vector normalToPoint = point.subtract(center);
        final Vector normalToPointNormalized = normalToPoint.normalize();
        return normalToPointNormalized != null ? center.add(normalToPoint.multiply(radius)) : center;
    }

    public Vector approximateClosestPointOnSphere(final Vector point) {
        final Vector normalToPoint = point.subtract(center);
        final Vector normalToPointNormalized = normalToPoint.approximateNormalize();
        return normalToPointNormalized != null ? center.add(normalToPoint.multiply(radius)) : center;
    }

    public Range getRangeX() {
        return new Range(center.x - radius, center.x + radius);
    }

    public Range getRangeY() {
        return new Range(center.y - radius, center.y + radius);
    }

    public Range getRangeZ() {
        return new Range(center.z - radius, center.z + radius);
    }
}
