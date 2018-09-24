package io.bacta.swg.math;


import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.swg.iff.Iff;
import io.bacta.swg.iff.IffWritable;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/5/2016.
 */
public class Quaternion implements ByteBufferWritable, IffWritable {
    public static final float EPSILON = 1.19209e-007f;
    public static final float EQUALITY_EPSILON = 1e-027f;

    public static final float PI_OVER_2 = (float) Math.PI / 2;

    public static final Quaternion IDENTITY = new Quaternion();

    public float w;
    public float x;
    public float y;
    public float z;

    public Quaternion() {
        this(1.0f, 0.0f, 0.0f, 0.0f);
    }

    public Quaternion(final float w, final float x, final float y, final float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(final Transform transform) {
        final float trace = transform.matrix[0][0] + transform.matrix[1][1] + transform.matrix[2][2] + 1.0f;

        if (trace >= 2.0f) {
            final float sqrtTrace = (float) Math.sqrt(trace);
            w = sqrtTrace * 0.5f;

            final float d = 0.5f / sqrtTrace;
            x = (transform.matrix[2][1] - transform.matrix[1][2]) * d;
            y = (transform.matrix[0][2] - transform.matrix[2][0]) * d;
            z = (transform.matrix[1][0] - transform.matrix[0][1]) * d;
        } else {
            int i = 0, j = 1, k = 2;

            if (transform.matrix[1][1] > transform.matrix[i][i]) {
                i = 1;
                j = 2;
                k = 0;
            }

            if (transform.matrix[2][2] > transform.matrix[i][i]) {
                i = 2;
                j = 0;
                k = 1;
            }

            x = (float) Math.sqrt(((transform.matrix[i][i] - transform.matrix[j][j]) - transform.matrix[k][k]) + 1.0f) * 0.5f;
            final float d = 1.0f / (4.0f * x);
            y = (transform.matrix[j][i] + transform.matrix[i][j]) * d;
            z = (transform.matrix[k][i] + transform.matrix[i][k]) * d;
            w = (transform.matrix[k][j] - transform.matrix[j][k]) * d;
        }
    }

    /**
     * construct a quaternion representing the orientation specified by spinning
     * 'angle' number of radians around unit vector 'vector'.
     * <p>
     * Make sure 'vector' is normalized.  This routine will not normalize it
     * for you.
     *
     * @param angle  angle to spin around vector (in radians)
     * @param vector vector around which angle is spun (must be normalized)
     */
    public Quaternion(final float angle, final Vector vector) {
        final float halfAngle = 0.5f * angle;
        final float sinHalfAngle = (float) Math.sin(halfAngle);

        w = (float) Math.cos(halfAngle);
        x = vector.x * sinHalfAngle;
        y = vector.y * sinHalfAngle;
        z = vector.z * sinHalfAngle;
    }

    public Quaternion(final Quaternion quaternion) {
        this.w = quaternion.w;
        this.x = quaternion.x;
        this.y = quaternion.y;
        this.z = quaternion.z;
    }

    public Quaternion(final ByteBuffer buffer) {
        x = buffer.getFloat();
        y = buffer.getFloat();
        z = buffer.getFloat();
        w = buffer.getFloat();
    }

    public Quaternion(final Iff iff) {
        w = iff.readFloat();
        x = iff.readFloat();
        y = iff.readFloat();
        z = iff.readFloat();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
    }

    public void getTransform(final Transform transform) {
        getTransformPreserveTranslation(transform);
        transform.setPositionInParentSpace(Vector.ZERO);
    }

    public void getTransformPreserveTranslation(final Transform transform) {
        if ((w + EQUALITY_EPSILON) < 1.0f) {
            final float yyTimes2 = y * y * 2.0f;
            final float zzTimes2 = z * z * 2.0f;
            final float xyTimes2 = x * y * 2.0f;
            final float wzTimes2 = w * z * 2.0f;
            final float xzTimes2 = x * z * 2.0f;
            final float wyTimes2 = w * y * 2.0f;

            transform.matrix[0][0] = (1.0f - yyTimes2) - zzTimes2;
            transform.matrix[0][1] = xyTimes2 - wzTimes2;
            transform.matrix[0][2] = xzTimes2 + wyTimes2;

            final float xxTimes2 = x * x * 2.0f;
            final float yzTimes2 = y * z * 2.0f;
            final float wxTimes2 = w * x * 2.0f;

            transform.matrix[1][0] = xyTimes2 + wzTimes2;
            transform.matrix[1][1] = (1.0f - xxTimes2) - zzTimes2;
            transform.matrix[1][2] = yzTimes2 - wxTimes2;

            transform.matrix[2][0] = xzTimes2 - wyTimes2;
            transform.matrix[2][1] = yzTimes2 + wxTimes2;
            transform.matrix[2][2] = (1.0f - xxTimes2) - yyTimes2;
        } else {
            transform.resetRotateLocalSpaceToParentSpace();
        }
    }

    public Quaternion negative() {
        return new Quaternion(-w, -x, -y, -z);
    }

    /**
     * Retrieve the complex conjugate of this Quaternion instance.
     * <p>
     * When a Quaternion has a unit length Vector, the complex conjugate
     * is equivalent to the inverse.  This is similar to a pure rotation
     * matrix, which has a simple inverse equivalent to the transpose of
     * the matrix.
     *
     * @return
     */
    public Quaternion getComplexConjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    public float getMagnitudeSquared() {
        return w * w + x * x + y * y + z * z;
    }

    public void normalize() {
        final float reciprocalMag = 1.0f / (float) Math.sqrt(x * x + y * y + z * z + w * w);

        x *= reciprocalMag;
        y *= reciprocalMag;
        z *= reciprocalMag;
        w *= reciprocalMag;
    }

    /**
     * perform spherical linear interpolation between this quaternion and
     * 'other' quaternion.
     * <p>
     * This routine performs a spherical linear interpolation between the
     * orientation represented by this quaternion and the orientation
     * represented by 'other' quaternion.  'fractionOfOther' specifies
     * the fraction of 'other' blended with this quaternion.  A fraction
     * of 0.0 indicates only this quaternion, whereas a fraction of 1.0
     * indicates only the 'other' quaternion.  Values in between represent
     * a spherical linear interpolation between the two quaternions.
     * <p>
     * Although not a strict requirement, 'fractionOfOther' typically should
     * be restricted to the range zero to one.
     */
    public Quaternion slerp(final Quaternion otherOriginal, final float fractionOfOther) {
        // rls - check ensure interpolation using the shortest path around the "hypersphere."
        final float dotOriginal = dot(otherOriginal);
        final Quaternion otherClosest = new Quaternion(dotOriginal < 0.0f ? otherOriginal.negative() : otherOriginal);

        final float cosTheta = dot(otherClosest);

        if ((1.0f + cosTheta) > EPSILON) {
            float c1, c2;

            // usual case. this means sin theta has enough value.
            if ((1.0f - cosTheta) > EPSILON) {
                // usual
                final float theta = (float) Math.acos(cosTheta);
                final float ooSinTheta = 1.0f / (float) Math.sin(theta); // rls - multiply instead of divide.
                final float fractionTimesTheta = fractionOfOther * theta;
                c1 = (float) Math.sin(theta - fractionTimesTheta) * ooSinTheta;
                c2 = (float) Math.sin(fractionTimesTheta) * ooSinTheta;
            } else {
                // ends very close
                c1 = 1.0f - fractionOfOther;
                c2 = fractionOfOther;
            }

            return new Quaternion(
                    c1 * w + c2 * otherClosest.w,
                    c1 * x + c2 * otherClosest.x,
                    c1 * y + c2 * otherClosest.y,
                    c1 * z + c2 * otherClosest.z);
        }

        // ends nearly opposite
        final float fractionTimesTheta = (float) Math.PI * fractionOfOther;
        final float c1 = (float) Math.sin(PI_OVER_2 - fractionTimesTheta);
        final float c2 = (float) Math.sin(fractionTimesTheta);

        return new Quaternion(
                c1 * w + c2 * z,
                c1 * x - c2 * y,
                c1 * y + c2 * x,
                c1 * z - c2 * w);
    }

    public float dot(final Quaternion rhs) {
        return w * rhs.w + x * rhs.x + y * rhs.y + z * rhs.z;
    }

    public Quaternion add(final Quaternion rhs) {
        return new Quaternion(w + rhs.w, x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Quaternion subtract(final Quaternion rhs) {
        return new Quaternion(w - rhs.w, x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Quaternion multiply(final Quaternion rhs) {
        if ((rhs.w + EQUALITY_EPSILON) >= 1.f) {
            return this; // return this because the other is an identity quaternion.
        } else if ((w + EQUALITY_EPSILON) >= 1.f) {
            return rhs; // return rhs because this quaternion is an identity quaternion.
        }

        // Equation from CRC Concise Encyclopedia of Mathematics, p 1494, equations 24 and 25
        //
        // Assume quaternion of form (a1,A) = a1 + a2*i + a3*j + a4*k
        //        (that is, A = [a2 a3 a4]T)
        // then (s1,V1) * (s2,V2) = (s1*s2 - V1 <dot> V2, s1*V2 + s2*V1 + V1 <cross> V2)
        //        where <dot>   = dot product binary operator and
        //              <cross> = cross product binary operator
        //
        // lhs * rhs
        //
        //   w = w * rhs.w - (x * rhs.x + y * rhs.y + z * rhs.z)
        //   x = w * rhs.x + rhs.w * x + (y * rhs.z - z * rhs.y)
        //   y = w * rhs.y + rhs.w * y + (z * rhs.x - x * rhs.z)
        //   z = w * rhs.z + rhs.w * z + (x * rhs.y - y * rhs.x)

        return new Quaternion(
                w * rhs.w - (x * rhs.x + y * rhs.y + z * rhs.z),
                w * rhs.x + rhs.w * x + (y * rhs.z - z * rhs.y),
                w * rhs.y + rhs.w * y + (z * rhs.x - x * rhs.z),
                w * rhs.z + rhs.w * z + (x * rhs.y - y * rhs.x));
    }

    @Override
    public void writeToIff(final Iff iff) {
        iff.insertChunkData(w);
        iff.insertChunkData(x);
        iff.insertChunkData(y);
        iff.insertChunkData(z);
    }
}
