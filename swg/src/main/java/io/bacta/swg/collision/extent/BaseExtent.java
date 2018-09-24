package io.bacta.swg.collision.extent;

import io.bacta.swg.collision.ExtentType;
import io.bacta.swg.iff.Iff;
import io.bacta.swg.math.*;

/**
 * Created by crush on 4/22/2016.
 */
public abstract class BaseExtent {
    private final ExtentType extentType;

    public BaseExtent() {
        extentType = ExtentType.NULL;
    }

    public BaseExtent(final ExtentType extentType) {
        this.extentType = extentType;
    }

    public final ExtentType getType() {
        return extentType;
    }

    public abstract void load(final Iff iff);

    public abstract void write(final Iff iff);

    public abstract boolean contains(final Vector vector);

    public abstract boolean contains(final Vector begin, final Vector end);

    public abstract Range rangedIntersect(final Line3d line);

    public abstract Range rangedInsersect(final Ray3d ray);

    public abstract Range rangedIntersect(final Segment3d segment);

    public abstract BaseExtent clone();

    public abstract void copy(final BaseExtent source);

    public abstract void transform(final BaseExtent parent, final Transform transform, final float scale);

    public abstract AxialBox getBoundingBox();

    public abstract Sphere getBoundingSphere();

    public boolean intersect(final Vector begin, final Vector end, final Vector surfaceNormal, float time) {
        return realIntersect(begin, end, surfaceNormal, time);
    }

    public boolean intersect(final Vector begin, final Vector end, float time) {
        final Vector surfaceNormal = null;
        return realIntersect(begin, end, surfaceNormal, time);
    }

    public boolean interset(final Vector begin, final Vector end) {
        final Vector surfaceNormal = null;
        float time = 0;
        return realIntersect(begin, end, surfaceNormal, time);
    }

    public Vector getCenter() {
        return getBoundingSphere().getCenter();
    }

    public float getRadius() {
        return getBoundingSphere().getRadius();
    }

    public boolean validate() {
        return true;
    }

    protected abstract boolean realIntersect(final Vector begin, final Vector end, Vector surfaceNormal, float time);
}
