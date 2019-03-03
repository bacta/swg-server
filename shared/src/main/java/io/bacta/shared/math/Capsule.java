package io.bacta.shared.math;

import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 */
@Getter
public class Capsule {
    protected Vector pointA;
    protected Vector pointB;
    protected float radius;

    //Help accelerate sphere tree queries
    protected Vector normal;
    protected float segmentLength;

    public Capsule(final Vector a, final Vector b, final float radius) {
        this.pointA = a;
        this.pointB = b;
        this.radius = radius;

        final Vector delta = b.subtract(a);

        this.segmentLength = delta.magnitude();

        this.normal = segmentLength > 0.f ? delta.divide(segmentLength) : Vector.ZERO;
    }

    public Capsule(final Sphere sphere, final Vector delta) {
        this.pointA = sphere.getCenter();
        this.pointB = sphere.getCenter().add(delta);
        this.radius = sphere.getRadius();

        segmentLength = delta.magnitude();

        this.normal = segmentLength > 0.f ? delta.divide(segmentLength) : Vector.ZERO;
    }

    public Vector getCenter() {
        return pointA.add(pointB).divide(2.f);
    }

    public float getTotalRadius() {
        return radius + (segmentLength / 2.f);
    }

    public Sphere getBoundingSphere() {
        return new Sphere(getCenter(), getTotalRadius());
    }

    public Sphere getSphereA() {
        return new Sphere(pointA, radius);
    }

    public Sphere getSphereB() {
        return new Sphere(pointB, radius);
    }

    public Vector getDelta() {
        return pointB.subtract(pointA);
    }

    public boolean contains(final Sphere sphere) {
        if (radius < sphere.getRadius())
            return false;

        final Vector delta = sphere.getCenter().subtract(pointA);
        final float time = MathUtil.clamp(0.f, delta.dot(normal), segmentLength);

        final float diff2 = (radius - sphere.getRadius()) * (radius - sphere.getRadius());
        final float dist2 = delta.magnitudeBetweenSquared(normal.multiply(time));

        return dist2 < diff2;
    }

    public boolean intersectsSphere(final Sphere sphere) {
        final Vector delta = sphere.getCenter().subtract(pointA);
        final float time = MathUtil.clamp(0.f, delta.dot(normal), segmentLength);
        final float sum2 = (radius + sphere.getRadius()) * (radius + sphere.getRadius());
        final float dist2 = delta.magnitudeBetweenSquared(normal.multiply(time));

        return dist2 < sum2;
    }
}
