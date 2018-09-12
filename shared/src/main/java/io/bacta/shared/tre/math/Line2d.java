package io.bacta.shared.tre.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by crush on 4/23/2016.
 */
@Getter
public class Line2d {
    private Vector2d normal;
    private float c; //what is c?

    public Line2d() {
        this.normal = new Vector2d(1.f, 0.f);
    }

    public Line2d(final float x0, final float y0, final float x1, final float y1) {
        set(new Vector2d(x0, y0), new Vector2d(x1, y1));
    }

    public Line2d(final Vector2d normal, final float c) {
        this.normal = normal;
    }

    public Line2d(final Vector2d point0, final Vector2d point1) {
        set(point0, point1);
    }

    public void set(final Vector2d normal, final float c) {
        this.normal = normal;
        this.c = c;
    }

    public void set(final Vector2d point0, final Vector2d point1) {
        this.normal = new Vector2d(-point1.y + point0.y, point1.x - point0.x).normalize();

        if (this.normal == null)
            this.normal = new Vector2d(1.f, 0.f);

        this.c = -this.normal.dot(point0);
    }

    public float computeDistanceTo(final Vector2d point) {
        return normal.dot(point) + this.c;
    }

    public Vector2d project(final Vector2d point) {
        return point.subtract(normal.multiply(computeDistanceTo(point)));
    }

    public IntersectionResult findIntersection(final Vector2d start, final Vector2d end) {
        final float t0 = computeDistanceTo(start);
        final float t1 = computeDistanceTo(end);

        //check to make sure the endpoints span the plane
        if ((t0 * t1) > 0.f)
            return new IntersectionResult(false);

        if (t0 == t1)
            return new IntersectionResult(true, start, 0.f);

        final float t = t0 / (t0 - t1);
        final float x = (start.x + (end.x - start.x) * t);
        final float y = (start.y + (end.y - start.y) * t);

        return new IntersectionResult(true, new Vector2d(x, y), t);
    }

    @AllArgsConstructor
    public static final class IntersectionResult {
        public final boolean intersects;
        public final Vector2d intersection;
        public final float time;

        public IntersectionResult(final boolean intersects) {
            this.intersects = intersects;
            this.intersection = null;
            this.time = 0.f;
        }
    }

}
