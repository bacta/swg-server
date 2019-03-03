package io.bacta.shared.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public class OrientedCylinder {
    protected Vector base;
    protected Vector axis;
    protected float radius;
    protected float height;

    public OrientedCylinder() {
        this.base = Vector.ZERO;
        this.axis = new Vector(0.f, 1.f, 0.f);
        this.radius = 1.f;
        this.height = 1.f;
    }

    public OrientedCylinder(final Vector base, final Vector axis, final float radius, final float height) {
        this.base = base;
        this.axis = axis.normalize();
        this.radius = radius;
        this.height = height;
    }

    public OrientedCylinder(final Cylinder cylinder) {
        this.base = cylinder.getBase();
        this.axis = Vector.UNIT_Y;
        this.radius = cylinder.getRadius();
        this.height = cylinder.getHeight();
    }

    public OrientedCylinder(final Cylinder cylinder, final Transform transform) {
        this.base = transform.rotateTranslateLocalToParent(cylinder.getBase());
        this.axis = transform.rotateLocalToParent(cylinder.getAxisY());
        this.radius = cylinder.getRadius();
        this.height = cylinder.getHeight();
    }

    public Segment3d getAxisSegment() {
        return new Segment3d(base, base.add(axis.multiply(height)));
    }

    public OrientedCircle getBaseCircle() {
        return new OrientedCircle(base, axis, radius);
    }

    public OrientedCircle getTopCircle() {
        return new OrientedCircle(base.add(axis.multiply(height)), axis, radius);
    }

    public Vector getCenter() {
        return base.add(axis.multiply(height / 2.f));
    }

    public Vector getAxisX() {
        final Vector projected = new Vector(-axis.z, 0.f, axis.x).normalize();

        return projected != null ? projected : Vector.UNIT_X;
    }

    public Vector getAxisY() {
        return axis;
    }

    public Vector getAxisZ() {
        final Vector temp = getAxisX().cross(axis).normalize();
        return temp;
    }

    public float getExtentX() {
        return radius;
    }

    public float getExtentY() {
        return height / 2.f;
    }

    public float getExtentZ() {
        return radius;
    }

    public Transform getTransformLocalToParent() {
        final Transform temp = new Transform();
        temp.setLocalFrameIJKInParentSpace(getAxisX(), getAxisY(), getAxisZ());
        temp.moveInParentSpace(getCenter());
        return temp;
    }

    public Transform getTransformParentToLocal() {
        final Transform temp = new Transform();
        temp.invert(getTransformLocalToParent());
        return temp;
    }

    public Vector transformToLocal(final Vector vector) {
        return rotateToLocal(vector.subtract(getCenter()));
    }

    public Vector transformToParent(final Vector vector) {
        return rotateToWorld(vector).add(getCenter());
    }

    public Vector rotateToLocal(final Vector vector) {
        return new Vector(
                vector.dot(getAxisX()),
                vector.dot(getAxisY()),
                vector.dot(getAxisZ()));
    }

    public Vector rotateToWorld(final Vector vector) {
        //This creates a bunch of vectors - might want to optimize at some point to only create a single vec.
        return Vector.ZERO
                .add(getAxisX().multiply(vector.x))
                .add(getAxisY().multiply(vector.y))
                .add(getAxisZ().multiply(vector.z));
    }

    public Cylinder getLocalShape() {
        final float extentY = getExtentY();
        return new Cylinder(new Vector(0.f, -extentY, 0.f), radius, extentY * 2.f);
    }
}
