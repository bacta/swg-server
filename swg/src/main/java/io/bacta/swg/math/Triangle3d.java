package io.bacta.swg.math;

import lombok.Getter;

/**
 * Created by crush on 5/15/2016.
 */
@Getter
public final class Triangle3d {
    private final Vector cornerA;
    private final Vector cornerB;
    private final Vector cornerC;
    private final Vector normal;

    public Triangle3d() {
        this.cornerA = Vector.ZERO;
        this.cornerB = Vector.ZERO;
        this.cornerC = Vector.ZERO;
        this.normal = Vector.ZERO;
    }

    public Triangle3d(final Vector a, final Vector b, final Vector c) {
        this.cornerA = a;
        this.cornerB = b;
        this.cornerC = c;
        this.normal = calcNormal();
    }

    public Vector getCorner(final int i) {
        final int index = i % 3;

        switch (index) {
            default:
            case 0:
                return cornerA;
            case 1:
                return cornerB;
            case 2:
                return cornerC;
        }
    }

    public float getArea() {
        return cornerB.subtract(cornerA).cross(cornerC.subtract(cornerA)).magnitude() * 0.5f;
    }

    public boolean isDegenerate() {
        final float area = getArea();
        return area < 0.000001f;    // one square millimeter
    }

    public boolean isFacing(final Vector direction) {
        return normal.dot(direction) > 0;
    }

    public Triangle3d flip() {
        return new Triangle3d(cornerA, cornerC, cornerB);
    }

    public Plane3d getPlane() {
        return new Plane3d(cornerA, normal);
    }

    public Vector getEdgeDir(int whichEdge) {
        return getCorner(whichEdge + 1).subtract(getCorner(whichEdge));
    }

    public Vector getEdgeDir0() {
        return cornerB.subtract(cornerA);
    }

    public Vector getEdgeDir1() {
        return cornerC.subtract(cornerB);
    }

    public Vector getEdgeDir2() {
        return cornerA.subtract(cornerC);
    }

    public Segment3d getEdgeSegment(int whichSegment) {
        return new Segment3d(getCorner(whichSegment), getCorner(whichSegment + 1));
    }

    public Segment3d getEdgeSegment0() {
        return new Segment3d(cornerA, cornerB);
    }

    public Segment3d getEdgeSegment1() {
        return new Segment3d(cornerB, cornerC);
    }

    public Segment3d getEdgeSegment2() {
        return new Segment3d(cornerC, cornerA);
    }

    public Line3d getEdgeLine(final int whichEdge) {
        return new Line3d(getCorner(whichEdge), getEdgeDir(whichEdge));
    }

    public Line3d getEdgeLine0() {
        return new Line3d(cornerA, getEdgeDir0());
    }

    public Line3d getEdgeLine1() {
        return new Line3d(cornerB, getEdgeDir1());
    }

    public Line3d getEdgeLine2() {
        return new Line3d(cornerC, getEdgeDir2());
    }

    private Vector calcNormal() {
        final Vector temp = getEdgeDir0().cross(getEdgeDir1());
        return temp.normalize();
    }
}
