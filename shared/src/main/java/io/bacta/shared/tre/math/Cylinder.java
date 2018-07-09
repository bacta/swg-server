package io.bacta.shared.tre.math;

import lombok.Data;

/**
 * Created by crush on 5/13/2016.
 */
@Data
public class Cylinder {
    protected Vector base;
    protected float radius;
    protected float height;

    public Cylinder() {
        base = Vector.ZERO;
        radius = 1.0f;
        height = 1.0f;
    }

    public Cylinder(final Vector base, final float radius, final float height) {
        this.base = base;
        this.radius = radius;
        this.height = height;
    }

    public Vector getCenter() {
        return base.add(new Vector(0.0f, height / 2.0f, 0.0f));
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
        return getHeight() / 2.0f;
    }

    public float getExtentZ() {
        return getRadius();
    }

    public Range getRangeY() {
        return new Range(base.y, base.y + height);
    }

    public Circle getTopCircle() {
        return new Circle(new Vector(base.x, base.y + height, base.z), radius);
    }

    public Circle getBaseCircle() {
        return new Circle(base, radius);
    }

    public Ring getTopRing() {
        return new Ring(new Vector(base.x, base.y + height, base.z), radius);
    }

    public Ring getBaseRing() {
        return new Ring(base, radius);
    }
}
