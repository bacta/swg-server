package io.bacta.shared.math;



import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.iff.IffWritable;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by kyle on 4/7/2016.
 * <p>
 * Vector is an immutable class. Any mutating operations must return a new Vector.
 * This will allow us to take advantage of value types in Java 10. Furthermore, it allows us to have static
 * vector types available without worrying about them being modified.
 */
public final class Vector implements ByteBufferWritable, IffWritable {
    public static final Vector UNIT_X = new Vector(1.f, 0.f, 0.f);
    public static final Vector UNIT_Y = new Vector(0.f, 1.f, 0.f);
    public static final Vector UNIT_Z = new Vector(0.f, 0.f, 1.f);
    public static final Vector NEGATIVE_UNIT_X = new Vector(-1.f, 0.f, 0.f);
    public static final Vector NEGATIVE_UNIT_Y = new Vector(0.f, -1.f, 0.f);
    public static final Vector NEGATIVE_UNIT_Z = new Vector(0.f, 0.f, -1.f);
    public static final Vector ZERO = new Vector(0, 0, 0);
    public static final Vector XYZ111 = new Vector(1, 1, 1);
    public static final Vector MAX_XYZ = new Vector(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector NEGATIVE_MAX_XYZ = new Vector(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

    public static final float NORMALIZE_THRESHOLD = 0.00001f;
    public static final float NORMALIZED_EPSILON = 0.00001f;
    public static final float NORMALIZED_RANGE_SQUARED_MIN = (1.f - (2.f * Vector.NORMALIZED_EPSILON)) + (NORMALIZED_EPSILON * NORMALIZED_EPSILON);
    public static final float NORMALIZED_RANGE_SQUARED_MAX = (1.f + (2.f * Vector.NORMALIZED_EPSILON)) + (NORMALIZED_EPSILON * NORMALIZED_EPSILON);

    private static final Random random = new Random();

    public final float x;
    public final float y;
    public final float z;

    /**
     * Makes a new vector that is zeroed out.
     */
    public Vector() {
        x = 0.f;
        y = 0.f;
        z = 0.f;
    }

    /**
     * Makes a new vector based on an existing vector.
     *
     * @param vector
     */
    public Vector(final Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public Vector(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(final ByteBuffer buffer) {
        this.x = buffer.getFloat();
        this.y = buffer.getFloat();
        this.z = buffer.getFloat();
    }

    public Vector(final Iff iff) {
        this.x = iff.readFloat();
        this.y = iff.readFloat();
        this.z = iff.readFloat();
    }

    public Vector add(final Vector rhs) {
        return new Vector(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Vector subtract(final Vector rhs) {
        return new Vector(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Vector multiply(final float scalar) {
        return new Vector(x * scalar, y * scalar, z * scalar);
    }

    public Vector divide(final float scalar) {
        return multiply(1.f / scalar);
    }

    public Vector inverse() {
        return new Vector(-x, -y, -z);
    }

    /***
     * Normalizes the vector.
     *
     * @return If it succeeds, returns a new normalized Vector instance. Otherwise,
     * returns null.
     */
    public Vector normalize() {
        final float mag = magnitude();

        if (mag < NORMALIZE_THRESHOLD)
            return null;

        return divide(mag);
    }

    /**
     * Calculate the square of the magnitude of this vector.
     * <p>
     * This routine is much faster than magnitude().
     *
     * @return The square of the magnitude of the vector
     * @see Vector::magnitude()
     */
    public float magnitudeSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Calculate the square of the magnitude of the vector between this vector and the specified vector.
     * <p>
     * This routine is much faster than magnitudeBetween().
     *
     * @param vector The other endpoint of the delta vector.
     * @return The square of the magnitude of the delta vector.
     * @see Vector#magnitudeBetween(Vector)
     */
    public float magnitudeBetweenSquared(final Vector vector) {
        final float vx = x - vector.x;
        final float vy = y - vector.y;
        final float vz = z - vector.z;

        return (vx * vx + vy * vy + vz * vz);
    }

    /**
     * Calculate the magnitude of the vector between this vector and the specified vector.
     *
     * @param vector The other endpoint of the delta vector.
     * @return The magnitude of the delta vector.
     * @see Vector#magnitudeBetweenSquared(Vector)
     */
    public float magnitudeBetween(final Vector vector) {
        return (float) Math.sqrt(magnitudeBetweenSquared(vector));
    }

    /**
     * Calculate the approximate magnitude of this vector.
     * <p>
     * The implementation of this routine has +/- 8% error.
     *
     * @return The approximate magnitude of the vector
     */
    public float approximateMagnitude() {
        float minc = Math.abs(x);
        float midc = Math.abs(y);
        float maxc = Math.abs(z);

        // sort the vectors
        // we do our own swapping to avoid heavy-weight includes in such a low-level class
        if (midc < minc) {
            final float temp = midc;
            midc = minc;
            minc = temp;
        }

        if (maxc < minc) {
            final float temp = maxc;
            maxc = minc;
            minc = temp;
        }

        if (maxc < midc) {
            final float temp = maxc;
            maxc = midc;
            midc = temp;
        }

        return (maxc + (11.0f / 32.0f) * midc + (0.25f) * minc);
    }

    /**
     * Calculate the magnitude of this vector.
     * <p>
     * This routine is slow because it requires a square root operation.
     *
     * @return The magnitude of the vector
     * @see Vector::magnitudeSquared()
     */
    public float magnitude() {
        return (float) Math.sqrt(magnitudeSquared());
    }

    /**
     * Normalize a vector to a length of approximately 1.
     * <p>
     * If the vector is too small, it cannot be normalized.
     *
     * @return Returns the approximately normalized vector. Otherwise, null.
     */
    public Vector approximateNormalize() {
        final float mag = approximateMagnitude();

        if (mag < NORMALIZE_THRESHOLD)
            return null;

        return divide(mag);
    }

    /**
     * Calculate the angle of the vector from the X-Z plane.
     * <p>
     * This routine uses sqrt() and atan2() so it is not particularly fast.
     *
     * @return The angle of the vector from the X-Z plane
     */
    public float phi() {
        return (float) Math.atan2(-y, Math.sqrt(x * x + z * z));
    }

    /**
     * Return the rotation of the vector around the Y plane.
     * <p>
     * The result is undefined if both the x and z values of the vector are zero.
     * <p>
     * This routine uses atan2() so it is not particularly fast.
     *
     * @return The rotation of the vector around the Y plane
     */
    public float theta() {
        return (float) Math.atan2(x, z);
    }

    /**
     * Compute the dot product between this vector and another vector.
     * <p>
     * The dot product value is equal to the cosine of the angle between
     * the two vectors multiplied by the sum of the lengths of the vectors.
     *
     * @param vector Vector to compute the dot product against
     */
    public float dot(final Vector vector) {
        return (x * vector.x) + (y * vector.y) + (z * vector.z);
    }

    /**
     * Calculate the cross product between two vectors.
     * <p>
     * This routine returns a temporary.
     * <p>
     * Cross products are not communitive.
     *
     * @param rhs The right-hand size of the expression
     * @return A vector that is the result of the cross product of the two vectors.
     */
    public Vector cross(final Vector rhs) {
        return new Vector(y * rhs.z - z * rhs.y, z * rhs.x - x * rhs.z, x * rhs.y - y * rhs.x);
    }

    public boolean inPolygon(final Vector v0, final Vector v1, final Vector v2) {
        return sameSide(this, v0, v1, v2) && sameSide(this, v1, v2, v0) && sameSide(this, v2, v0, v1);
    }

    /**
     * Find the point on the specified line that is as close to this point as possible.
     * <p>
     * The line is treated as an infinite line, not a line segment.
     *
     * @param line0 First point on the line.
     * @param line1 Second point on the line.
     * @return A result that encapsulates the point on the specified line as close to this point as possible, and the
     * parametric time along that line that is closest.
     */
    public ClosestPointResult findClosestPointOnLine(final Vector line0, final Vector line1) {
        final Vector delta = line1.subtract(line0);
        final float r = subtract(line0).dot(delta) / delta.magnitudeSquared();

        return new ClosestPointResult(line0.add(delta.multiply(r)), r);
    }

    public ClosestPointResult findClosestPointOnLineSegment(final Vector startPoint, final Vector endPoint) {
        final Vector delta = endPoint.subtract(startPoint);

        final float deltaMagnitudeSquared = delta.magnitudeSquared();

        if (deltaMagnitudeSquared < NORMALIZE_THRESHOLD)
            return new ClosestPointResult(startPoint, 0.f);

        final float r = MathUtil.clamp(0.f, subtract(startPoint).dot(delta) / deltaMagnitudeSquared, 1.f);

        return new ClosestPointResult(startPoint.add(delta.multiply(r)), r);
    }

    /**
     * Calculate the distance from this point to the specified line.
     * <p>
     * The line is treated as an infinite line, not a line segment.
     *
     * @param line0 First point on the line.
     * @param line1 Second point on the line.
     * @return Distance to the line.
     */
    public float distanceToLine(final Vector line0, final Vector line1) {
        return magnitudeBetween(findClosestPointOnLine(line0, line1).point);
    }

    /**
     * Calculate the distance from this point to the specified line segment.
     *
     * @param line0 First point on the line.
     * @param line1 Second point on the line.
     * @return Distance to the line segment.
     */
    public float distanceToLineSegment(final Vector line0, final Vector line1) {
        return magnitudeBetween(findClosestPointOnLineSegment(line0, line1).point);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
    }

    @Override
    public void writeToIff(final Iff iff) {
        iff.insertChunkData(x);
        iff.insertChunkData(y);
        iff.insertChunkData(z);
    }

    /**
     * Linearly interpolate between two vectors.
     * <p>
     * The time parameter should be between 0.0 and 1.0 inclusive in order to have
     * the result be between the two endpoints. At time 0.0, the result will be
     * vector1, and at time 1.0 the result will be vector2.
     *
     * @param begin The starting endpoint.
     * @param end   The terminating endpoint.
     * @param t     The time to interpolate.
     * @return A new vector linearly interpolated.
     */
    public static Vector linearInterpolate(final Vector begin, final Vector end, final float t) {
        return new Vector(
                begin.x + (end.x - begin.x) * t,
                begin.y + (end.y - begin.y) * t,
                begin.z + (end.z - begin.z) * t);
    }

    /**
     * Create a random unit vector that is evenly distributed on the unit sphere.
     *
     * @return A new vector randomly distributed on unit sphere.
     */
    public static Vector randomUnit() {
        final float lz = (float) Math.cos(random.nextFloat() * Math.PI);
        final float t = random.nextFloat() * MathUtil.PI_TIMES_2;
        final float r = (float) Math.sqrt(1.f - lz * lz);

        final float x = (float) (r * Math.cos(t));
        final float y = (float) (r * Math.sin(t));

        return new Vector(x, y, lz);
    }

    /**
     * Create a random vector. The vector will be within the cube [-halfSideLength .. halfSideLength].
     *
     * @param halfSideLength Size of the cube.
     * @return A new vector representing the cube.
     */
    public static Vector randomCube(final float halfSideLength) {
        return new Vector(
                random.nextFloat() * halfSideLength * 2 - halfSideLength,
                random.nextFloat() * halfSideLength * 2 - halfSideLength,
                random.nextFloat() * halfSideLength * 2 - halfSideLength);
    }

    /**
     * Compute the midpoint of two vectors.
     * <p>
     * This routine just averages the three components separately.
     *
     * @param vector1 First endpoint
     * @param vector2 Second endpoint
     */
    public static Vector midpoint(final Vector vector1, final Vector vector2) {
        return new Vector(
                (float) ((vector1.x + vector2.x) * 0.5),
                (float) ((vector1.y + vector2.y) * 0.5),
                (float) ((vector1.z + vector2.z) * 0.5));
    }

    public static Vector perpendicular(final Vector direction) {
        // Measure the projection of "direction" onto each of the axes
        final float id = Math.abs(direction.dot(UNIT_X));
        final float jd = Math.abs(direction.dot(UNIT_Y));
        final float kd = Math.abs(direction.dot(UNIT_Z));

        Vector result;

        if (id <= jd && id <= kd)
            // Projection onto i was the smallest
            result = direction.cross(UNIT_X);
        else if (jd <= id && jd <= kd)
            // Projection onto j was the smallest
            result = direction.cross(UNIT_Y);
        else
            // Projection onto k was the smallest
            result = direction.cross(UNIT_Z);

        result.normalize();

        return result;
    }

    /**
     * Tests whether a point lies withing a trinagle.
     * <p>
     * This should be on at least the plane for the test to work.
     * <p>
     * Adapated from http://www.blackpawn.com/texts/pointinpoly/default.html
     *
     * @param point1 Point 1
     * @param point2 Point 2
     * @param a      Side a
     * @param b      Side b
     * @return True if the points are on same side. Otherwise, false.
     */
    public static boolean sameSide(final Vector point1, final Vector point2, final Vector a, final Vector b) {
        final Vector ba = b.subtract(a);
        final Vector cp1 = ba.cross(point1.subtract(a));
        final Vector cp2 = ba.cross(point2.subtract(a));

        return cp1.dot(cp2) >= 0.0f;
    }

    public String getDebugString() {
        return String.format("{x:%f, y:%f, z:%f}", x, y, z);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Vector vector = (Vector) o;

        if (Float.compare(vector.x, x) != 0) return false;
        if (Float.compare(vector.y, y) != 0) return false;
        return Float.compare(vector.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    @AllArgsConstructor
    public static final class ClosestPointResult {
        public final Vector point;
        public final float time;
    }
}
