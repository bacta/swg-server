package io.bacta.shared.collision.extent;


import io.bacta.shared.iff.Iff;
import io.bacta.shared.math.*;

/**
 * Created by crush on 4/22/2016.
 */
public class Extent extends BaseExtent {
    @Override
    public void load(Iff iff) {

    }

    @Override
    public void write(Iff iff) {

    }

    @Override
    public boolean contains(Vector vector) {
        return false;
    }

    @Override
    public boolean contains(Vector begin, Vector end) {
        return false;
    }

    @Override
    public Range rangedIntersect(Line3d line) {
        return null;
    }

    @Override
    public Range rangedInsersect(Ray3d ray) {
        return null;
    }

    @Override
    public Range rangedIntersect(Segment3d segment) {
        return null;
    }

    @Override
    public BaseExtent clone() {
        return null;
    }

    @Override
    public void copy(BaseExtent source) {

    }

    @Override
    public void transform(BaseExtent parent, Transform transform, float scale) {

    }

    @Override
    public AxialBox getBoundingBox() {
        return null;
    }

    @Override
    public Sphere getBoundingSphere() {
        return null;
    }

    @Override
    protected boolean realIntersect(Vector begin, Vector end, Vector surfaceNormal, float time) {
        return false;
    }
}
