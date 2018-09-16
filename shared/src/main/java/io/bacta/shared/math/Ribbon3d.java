package io.bacta.shared.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 */
@Getter
@AllArgsConstructor
public final class Ribbon3d {
    private final Vector pointA;
    private final Vector pointB;
    private final Vector direction;

    public Ribbon3d(final Segment3d segment, final Vector direction) {
        this.pointA = segment.getBegin();
        this.pointB = segment.getEnd();
        this.direction = direction;
    }

    public Ribbon3d(final Line3d line, final Vector delta) {
        this.pointA = line.getPoint();
        this.pointB = line.getPoint().add(delta);
        this.direction = line.getNormal();
    }

    public Vector getDelta() {
        return pointB.subtract(pointA);
    }

    public Plane3d getPlane() {
        final Vector e = pointB.subtract(pointA);
        final Vector n = e.cross(direction);

        return new Plane3d(pointA, n);
    }

    public Line3d getEdgeA() {
        return new Line3d(pointA, direction);
    }

    public Line3d getEdgeB() {
        return new Line3d(pointB, direction);
    }
}
