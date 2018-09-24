package io.bacta.swg.math;



import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 * Created by crush on 4/23/2016.
 *
 * This class is immutable. Therefore, it won't have any set operations. In order to mutate this class, a new instance
 * must be returned with the mutated state.
 */
public final class Vector2d implements ByteBufferWritable {
    public static final Vector2d ZERO = new Vector2d(0.f, 0.f);

    public final float x;
    public final float y;

    public Vector2d(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(final ByteBuffer buffer) {
        this.x = buffer.getFloat();
        this.y = buffer.getFloat();
    }

    public boolean isZero() {
        return ZERO.equals(this);
    }

    /**
     * Calculates the dot product of this vector and another vector.
     *
     * @param vector The vector to dot with this vector.
     * @return The dot product of the two vectors.
     */
    public float dot(final Vector2d vector) {
        return (x * vector.x) + (y * vector.y);
    }

    /**
     * Calculates the theta of this vector.
     *
     * @return The theta of this vector.
     */
    public float theta() {
        return (float) Math.atan2(x, y); //TODO: Is the order of these right? Investigate...
    }

    public Vector2d rotate(final float radians) {
        final float cosAngle = (float) Math.cos(radians);
        final float sinAngle = (float) Math.sin(radians);

        final float oldX = x;
        final float oldY = y;

        return new Vector2d(
                oldX * cosAngle - oldY * sinAngle,
                oldX * sinAngle + oldY * cosAngle);
    }

    public Vector2d rotate(final float radians, final Vector2d origin) {
        final Vector2d point = new Vector2d(x - origin.x, y - origin.y);
        final float cosAngle = (float) Math.cos(radians);
        final float sinAngle = (float) Math.sin(radians);

        return new Vector2d(
                origin.x + point.x * cosAngle - point.y * sinAngle,
                origin.y + point.x * sinAngle - point.y * cosAngle);
    }

    /**
     * Normalizes the vector. If this operation fails, it will return null.
     *
     * @return The normalized vector. Otherwise, null.
     */
    public Vector2d normalize() {
        final float mag = magnitude();

        if (mag < 0.00001f)
            return null;

        return divide(mag);
    }

    /**
     * Creates the inverse Vector2d of this one.
     *
     * @return The inverse Vector2d of this one.
     */
    public Vector2d inverse() {
        return new Vector2d(-x, -y);
    }

    /**
     * Adds a Vector2d to this one and returns the new Vector2d.
     *
     * @param rhs The Vector2d to add.
     * @return The new Vector2d.
     */
    public Vector2d add(final Vector2d rhs) {
        return new Vector2d(x + rhs.x, y + rhs.y);
    }

    /**
     * Subtracts a Vector2d to this one and returns the new Vector2d.
     *
     * @param rhs The Vector2d to subtract.
     * @return The new Vector2d.
     */
    public Vector2d subtract(final Vector2d rhs) {
        return new Vector2d(x - rhs.x, y - rhs.y);
    }

    /**
     * Multiplies this Vector2d by a scalar value, and returns the new Vector2d.
     *
     * @param scalar The scalar value with which to multiply this Vector2d.
     * @return The new Vector2d.
     */
    public Vector2d multiply(final float scalar) {
        return new Vector2d(x * scalar, y * scalar);
    }

    /**
     * Divides this Vector2d by a scalar value, and returns the new Vector2d.
     *
     * @param scalar The scalar value with which to divide this Vector2d.
     * @return The new Vector2d.
     */
    public Vector2d divide(final float scalar) {
        return multiply(1.f / scalar);
    }

    public boolean equals(final Vector2d rhs) {
        return this == rhs || (x == rhs.x && y == rhs.y);
    }

    public float magnitude() {
        return (float) Math.sqrt(magnitudeSquared());
    }

    public float magnitudeSquared() {
        return x * x + y * y;
    }

    public float magnitudeBetween(final Vector2d vector) {
        return (float) Math.sqrt(magnitudeBetweenSquared(vector));
    }

    public float magnitudeBetweenSquared(final Vector2d vector) {
        final float vx = x - vector.x;
        final float vy = y - vector.y;

        return vx * vx + vy * vy;
    }

    public static Vector2d linearInterpolate(final Vector2d start, final Vector2d end, float t) {
        return new Vector2d(
                MathUtil.linearInterpolate(start.x, end.x, t),
                MathUtil.linearInterpolate(start.y, end.y, t));
    }

    /**
     * This is kind of pointless since we've changed {@link Vector2d#normalize()} to return a new Vector2d, but we
     * are leaving it here to help with code translation.
     *
     * @param vector The vector to normalize.
     * @return Returns the normalized vector, or null if it couldn't be normalized.
     */
    public static Vector2d normalized(final Vector2d vector) {
        return vector.normalize();
    }

    /**
     * Returns the normal of this Vector2d. If it should be normalized, then normalize is applied to it. If normalize
     * fails, then null is returned.
     *
     * @param vector    The vector from which to return the normal.
     * @param normalize Should the vector be normalized.
     * @return The normal of this vector. If normalize is true, then could return null if the normalize operation fails.
     */
    public static Vector2d normal(final Vector2d vector, final boolean normalize) {
        final Vector2d v = new Vector2d(-vector.y, vector.x);

        if (normalize)
            return v.normalize();

        return v;
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, x);
        BufferUtil.put(buffer, y);
    }
}
