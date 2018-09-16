package io.bacta.shared.math;

/**
 * Created by crush on 4/23/2016.
 */
public class Rectangle2d {
    public float x0;
    public float y0;
    public float x1;
    public float y1;

    public Rectangle2d() {
        set(0.f, 0.f, 0.f, 0.f);
    }

    public Rectangle2d(final float x0, final float y0, final float x1, final float y1) {
        set(x0, y0, x1, y1);
    }

    public void set(float x0, float y0, float x1, float y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public float getWidth() {
        return Math.abs(x1 - x0);
    }

    public float getHeight() {
        return Math.abs(y1 - y0);
    }

    public Vector2d getCenter() {
        return new Vector2d(
                x0 + getWidth() * 0.5f,
                y0 + getHeight() * 0.5f);
    }

    public boolean isWithin(final float x, final float y) {
        final float minX = (x0 < x1) ? x0 : x1;
        final float maxX = (x0 > x1) ? x0 : x1;

        final float minY = (y0 < y1) ? y0 : y1;
        final float maxY = (y0 > y1) ? y0 : y1;

        return MathUtil.withinRangeInclusiveInclusive(minX, x, maxX)
                && MathUtil.withinRangeInclusiveInclusive(minY, y, maxY);
    }

    public boolean isWithin(final Vector2d point) {
        return isWithin(point.x, point.y);
    }

    public boolean isVector2d() {
        return x0 == x1 && y0 == y1;
    }

    public void expand(final float x, final float y) {
        if (x < x0)
            x0 = x;

        if (y < y0)
            y0 = y;

        if (x > x1)
            x1 = x;

        if (y > y1)
            y1 = y;
    }

    public void expand(final Vector2d point) {
        expand(point.x, point.y);
    }

    public void expend(final Rectangle2d rectangle) {
        expand(rectangle.x0, rectangle.y0);
        expand(rectangle.x1, rectangle.y0);
        expand(rectangle.x0, rectangle.y1);
        expand(rectangle.x1, rectangle.y1);
    }

    public void translate(final float x, final float y) {
        x0 += x;
        y0 += y;
        x1 += x;
        y1 += y;
    }

    public void translate(final Vector2d point) {
        translate(point.x, point.y);
    }

    public void scale(final float scalar) {
        final Vector2d center = getCenter();

        x0 = (x0 - center.x) * scalar + center.x;
        y0 = (y0 - center.y) * scalar + center.y;
        x1 = (x1 - center.x) * scalar + center.x;
        y1 = (y1 - center.y) * scalar + center.y;
    }

    public boolean intersects(final Rectangle2d other) {
        return !(x1 < other.x0 || x0 > other.x1 || y1 < other.y0 || y0 > other.y1);
    }

    public boolean contains(final Rectangle2d other) {
        return other.x0 >= x0 && other.x1 <= x1 && other.y0 >= y0 && other.y1 <= y1;
    }

    public boolean intersects(final Line2d line) {
        final Vector2d v0 = new Vector2d(x0, y0);
        final Vector2d v1 = new Vector2d(x1, y0);

        Line2d.IntersectionResult intersectionResult = line.findIntersection(v0, v1);
        Vector2d intersection = intersectionResult.intersection;

        if (intersectionResult.intersects && withinRange(v0.x, intersection.x, v1.x) && withinRange(v0.y, intersection.y, v1.y))
            return true;

        final Vector2d v2 = new Vector2d(x1, y1);
        intersectionResult = line.findIntersection(v1, v2);
        intersection = intersectionResult.intersection;

        if (intersectionResult.intersects && withinRange(v1.x, intersection.x, v2.x) && withinRange(v1.y, intersection.y, v2.y))
            return true;

        final Vector2d v3 = new Vector2d(x0, y1);
        intersectionResult = line.findIntersection(v2, v3);
        intersection = intersectionResult.intersection;

        if (intersectionResult.intersects && withinRange(v2.x, intersection.x, v3.x) && withinRange(v2.y, intersection.y, v3.y))
            return true;

        intersectionResult = line.findIntersection(v3, v0);
        intersection = intersectionResult.intersection;

        if (intersectionResult.intersects && withinRange(v3.x, intersection.x, v0.x) && withinRange(v3.y, intersection.y, v0.y))
            return true;

        return false;
    }

    private static boolean withinRange(final float rangeMin, final float value, final float rangeMax) {
        return rangeMin <= rangeMax
                ? MathUtil.withinRangeInclusiveInclusive(rangeMin, value, rangeMax)
                : MathUtil.withinRangeInclusiveInclusive(rangeMax, value, rangeMin);
    }
}
