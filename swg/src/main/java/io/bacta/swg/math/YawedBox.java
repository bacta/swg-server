package io.bacta.swg.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public class YawedBox {
    protected Vector base;

    protected Vector axisX;
    protected Vector axisZ;

    protected float extentX;
    protected float extentZ;

    protected float height;

    public YawedBox() {

    }

    public YawedBox(final AxialBox box, final float yaw) {
        this.base = box.getBase();
        this.extentX = box.getExtentX();
        this.extentZ = box.getExtentZ();
        this.height = box.getHeight();

        final float cos = (float) Math.cos(yaw);
        final float sin = (float) Math.sin(yaw);

        this.axisX = new Vector(cos, 0.0f, -sin);
        this.axisZ = new Vector(sin, 0.0f, cos);
    }

    public YawedBox(final Vector base, final Vector axisX, final Vector axisZ, final float extentX, final float extentZ, final float height) {
        this.base = base;
        this.axisX = axisX;
        this.axisZ = axisZ;
        this.extentX = extentX;
        this.extentZ = extentZ;
        this.height = height;
    }

    public Vector getCorner(final int whichCorner) {
        final Vector X = axisX.multiply(extentX);
        final Vector Y = Vector.UNIT_Y.multiply(height);
        final Vector Z = axisZ.multiply(extentZ);

        switch (whichCorner) {
            case 0:
                return base.subtract(X).subtract(Z);
            case 1:
                return base.add(X).subtract(Z);
            case 2:
                return base.subtract(X).add(Z);
            case 3:
                return base.add(X).add(Z);
            case 4:
                return base.subtract(X).subtract(Z).add(Y);
            case 5:
                return base.add(X).subtract(Z).add(Y);
            case 6:
                return base.subtract(X).add(Z).add(Y);
            case 7:
                return base.add(X).add(Z).add(Y);

            default:
                return new Vector(0, 0, 0);
        }
    }

    public Vector getCenter() {
        return getBase().add(new Vector(0.f, getExtentY(), 0.f));
    }

    public float getExtentY() {
        return getHeight() / 2.f;
    }

    public Vector getAxisY() {
        return Vector.UNIT_Y;
    }

    public Range getRangeY() {
        final float min = getBase().y;
        final float max = min + getHeight();

        return new Range(min, max);
    }

    public AxialBox getLocalShape() {
        return new AxialBox(
                new Vector(-getExtentX(), -getExtentY(), -getExtentZ()),
                new Vector(getExtentX(), getExtentY(), getExtentZ()));
    }

    public Transform getTransformLocalToParent() {
        final Transform temp = new Transform();
        temp.setLocalFrameIJKInParentSpace(axisX, Vector.UNIT_Y, axisZ);
        temp.moveInParentSpace(base);

        return temp;
    }

    public Transform getTransformParentToLocal() {
        final Transform temp = new Transform();
        temp.invert(getTransformLocalToParent());
        return temp;
    }

    public Vector transformToLocal(final Vector vector) {
        //	return rotateToLocal(V - getCenter());
        return rotateToLocal(vector.subtract(getCenter()));
    }

    public Vector transformToWorld(final Vector vector) {
        //	return rotateToWorld(V) + getCenter();
        return rotateToWorld(vector).add(getCenter());
    }

    public Vector rotateToLocal(final Vector vector) {
        return new Vector(
                vector.dot(getAxisX()),
                vector.dot(getAxisY()),
                vector.dot(getAxisZ()));
    }

    public Vector rotateToWorld(final Vector vector) {
        return Vector.ZERO
                .add(getAxisX().multiply(vector.x))
                .add(getAxisY().multiply(vector.y))
                .add(getAxisZ().multiply(vector.z));
    }
}
