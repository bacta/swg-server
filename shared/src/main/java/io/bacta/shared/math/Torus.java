package io.bacta.shared.math;

import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 */
public final class Torus {
    @Getter
    private final Vector center;
    @Getter
    private final Vector axis;
    private final Vector tangent;
    private final Vector binormal;
    @Getter
    private final float majorRadius;
    @Getter
    private final float minorRadius;

    public Torus(final Vector center, final float majorRadius, final float minorRadius) {
        this.center = center;
        this.axis = Vector.UNIT_Y;
        this.tangent = Vector.UNIT_X;
        this.binormal = Vector.UNIT_Z;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
    }

    public Torus(final Vector center, final Vector axis, final float majorRadius, final float minorRadius) {
        this.center = center;
        this.axis = axis;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.tangent = axis.cross(Vector.UNIT_Z);
        this.binormal = tangent.cross(axis);
    }

    public Vector transformToLocal(final Vector vector) {
        return rotateToLocal(vector.subtract(center));
    }

    public Vector transformToWorld(final Vector vector) {
        return rotateToWorld(vector).add(center);
    }

    public Vector rotateToLocal(final Vector vector) {
        return new Vector(
                vector.dot(tangent),
                vector.dot(axis),
                vector.dot(binormal));
    }

    public Vector rotateToWorld(final Vector vector) {
        return Vector.ZERO
                .add(tangent.multiply(vector.x))
                .add(axis.multiply(vector.y))
                .add(binormal.multiply(vector.z));
    }

    public boolean isOriented() {
        return Math.abs(axis.y - 1.0f) > 0.00001f;
    }

}
