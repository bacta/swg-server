package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 */
@Getter
public class OrientedBox {
    protected Vector center;
    protected Vector axisX;
    protected Vector axisY;
    protected Vector axisZ;
    protected float extentX;
    protected float extentY;
    protected float extentZ;

    public OrientedBox(final Vector center,
                       final Vector axisX,
                       final Vector axisY,
                       final Vector axisZ,
                       final float extentX,
                       final float extentY,
                       final float extentZ) {
        this.center = center;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
        this.extentX = extentX;
        this.extentY = extentY;
        this.extentZ = extentZ;
    }

    public OrientedBox(final AxialBox box, final Transform transform) {
        this.center = transform.rotateTranslateLocalToParent(box.getCenter());
        this.axisX = transform.rotateLocalToParent(box.getAxisX());
        this.axisY = transform.rotateLocalToParent(box.getAxisY());
        this.axisZ = transform.rotateLocalToParent(box.getAxisZ());
        this.extentX = box.getExtentX();
        this.extentY = box.getExtentY();
        this.extentZ = box.getExtentZ();
    }

    public OrientedBox(final AxialBox box) {
        this.center = box.getCenter();
        this.axisX = box.getAxisX();
        this.axisY = box.getAxisY();
        this.axisZ = box.getAxisZ();
        this.extentX = box.getExtentX();
        this.extentY = box.getExtentY();
        this.extentZ = box.getExtentZ();
    }

    public OrientedBox(final YawedBox box) {
        this.center = box.getCenter();
        this.axisX = box.getAxisX();
        this.axisY = box.getAxisY();
        this.axisZ = box.getAxisZ();
        this.extentX = box.getExtentX();
        this.extentY = box.getExtentY();
        this.extentZ = box.getExtentZ();
    }

    public Vector[] getAxes() {
        return new Vector[]{axisX, axisY, axisZ};
    }

    public float[] getExtents() {
        return new float[]{extentX, extentY, extentZ};
    }

    public Vector getBase() {
        return getCenter().subtract(getAxisY().multiply(getExtentY()));
    }

    public Plane3d getFacePlane(final int whichPlane) {
        switch (whichPlane) {
            case 0:
                return new Plane3d(center.add(axisX.multiply(extentX)), axisX);
            case 1:
                return new Plane3d(center.add(axisY.multiply(extentY)), axisY);
            case 2:
                return new Plane3d(center.add(axisZ.multiply(extentZ)), axisZ);
            case 3:
                return new Plane3d(center.subtract(axisX.multiply(extentX)), axisX.inverse());
            case 4:
                return new Plane3d(center.subtract(axisY.multiply(extentY)), axisY.inverse());
            case 5:
                return new Plane3d(center.subtract(axisZ.multiply(extentZ)), axisZ.inverse());
            default:
                return new Plane3d(Vector.ZERO, Vector.ZERO);
        }
    }

    public Vector getCorner(final int whichCorner) {
        final Vector x = axisX.multiply(extentX);
        final Vector y = axisY.multiply(extentY);
        final Vector z = axisZ.multiply(extentZ);

        switch (whichCorner) {
            case 0:
                return center.subtract(x).subtract(y).subtract(z);
            case 1:
                return center.add(x).subtract(y).subtract(z);
            case 2:
                return center.subtract(x).subtract(y).add(z);
            case 3:
                return center.add(x).subtract(y).add(z);
            case 4:
                return center.subtract(x).add(y).subtract(z);
            case 5:
                return center.add(x).add(y).subtract(z);
            case 6:
                return center.subtract(x).add(y).add(z);
            case 7:
                return center.add(x).add(y).add(z);
            default:
                return Vector.ZERO;
        }
    }

    public AxialBox getLocalShape() {
        return new AxialBox(
                new Vector(-extentX, -extentY, -extentZ),
                new Vector(extentX, extentY, extentZ));
    }

    public Transform getTransformLocalToParent() {
        final Transform temp = new Transform();
        temp.setLocalFrameIJKInParentSpace(getAxisX(), getAxisY(), getAxisZ());
        temp.moveInParentSpace(center);
        return temp;
    }

    public Transform getTransformParentToLocal() {
        final Transform temp = new Transform();
        temp.invert(getTransformLocalToParent());
        return temp;
    }

    public Vector transformToLocal(final Vector vector) {
        return rotateToLocal(vector.subtract(center));
    }

    public Vector transformToWorld(final Vector vector) {
        return rotateToWorld(vector).add(center);
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
