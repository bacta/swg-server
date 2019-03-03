package io.bacta.shared.math;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kyle on 4/7/2016.
 */
@Data
public final class Transform implements ByteBufferWritable {
    public static final Transform IDENTITY = new Transform(); // TODO: Can we make this immutable?

    public final float[][] matrix = new float[3][4];

    public Transform() {
    }

    public Transform(final ByteBuffer buffer) {
        final Quaternion q = new Quaternion(buffer);
        final Vector v = new Vector(buffer);

        q.getTransform(this);
        setPositionInParentSpace(v);
    }

    /**
     * Reorthogonalize a transform.
     * <p>
     * Repeated rotations will introduce numerical error into the transform,
     * which will cause the upper 3x3 matrix to become non-orthonormal.  If
     * enough error is introduced, weird errors will begin to occur when using
     * the transform.
     * <p>
     * This routine attempts to reduce the numerical error by reorthonormalizing
     * the upper 3x3 matrix.
     */
    public void reorthonormalize() {
        Vector k = getLocalFrameKInParentSpace().normalize();
        Vector j = getLocalFrameJInParentSpace().normalize();

        // build the remaining vector with the cross product
        final Vector i = j.cross(k);

        // use that result to rebuild the
        j = k.cross(i);

        // copy the results back into the transform
        matrix[0][0] = i.x;
        matrix[1][0] = i.y;
        matrix[2][0] = i.z;

        matrix[0][1] = j.x;
        matrix[1][1] = j.y;
        matrix[2][1] = j.z;

        matrix[0][2] = k.x;
        matrix[1][2] = k.y;
        matrix[2][2] = k.z;
    }

    /**
     * Invert a simple transform.
     * <p>
     * The source matrix has to be composed of purely rotational and translational
     * components.  It may not contain any scaling or shearing transforms.
     *
     * @param transform Simple transform to invert
     */
    public void invert(final Transform transform) {
        // transpose the upper 3x3 matrix
        matrix[0][0] = transform.matrix[0][0];
        matrix[0][1] = transform.matrix[1][0];
        matrix[0][2] = transform.matrix[2][0];

        matrix[1][0] = transform.matrix[0][1];
        matrix[1][1] = transform.matrix[1][1];
        matrix[1][2] = transform.matrix[2][1];

        matrix[2][0] = transform.matrix[0][2];
        matrix[2][1] = transform.matrix[1][2];
        matrix[2][2] = transform.matrix[2][2];

        // invert the translation
        final float x = transform.matrix[0][3];
        final float y = transform.matrix[1][3];
        final float z = transform.matrix[2][3];
        matrix[0][3] = -(matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z);
        matrix[1][3] = -(matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z);
        matrix[2][3] = -(matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z);
    }

    /**
     * Multiply two matrices together.
     * <p>
     * This routine will properly handle the case where the destination matrix
     * is also one or both of the source matrices.
     *
     * @param lhs Transform on the left-hand side
     * @param rhs Transform on the right-hand side
     */
    public void multiply(final Transform lhs, final Transform rhs) {
        internalMultiply(matrix, lhs.matrix, rhs.matrix);
    }

    /**
     * Move the transform in it's own local space.
     * <p>
     * This routine moves the transform according to its current frame of reference.
     * Therefore, moving along the Z axis will move the transform forward in the direction
     * in which it is pointed.
     *
     * @param vec Vector to rotate and translate
     */
    public void moveInLocalSpace(final Vector vec) {
        moveInParentSpace(rotateLocalToParent(vec));
    }

    /**
     * Move the transform in it's parent space.
     * <p>
     * This routine moves the transform in it's parent space, or the world space if
     * the transform has no parent. Therefore, moving along the Z axis will move the
     * transform forward along the Z-axis of it's parent space, not forward in the
     * direction in which it is pointed.
     *
     * @param vec Displacement to move in parent space
     * @see Transform::move_l()
     */
    public void moveInParentSpace(final Vector vec) {
        matrix[0][3] += vec.x;
        matrix[1][3] += vec.y;
        matrix[2][3] += vec.z;
    }

    public Vector rotateLocalToParent(final Vector vec) {
        return new Vector(
                matrix[0][0] * vec.x + matrix[0][1] * vec.y + matrix[0][2] * vec.z,
                matrix[1][0] * vec.x + matrix[1][1] * vec.y + matrix[1][2] * vec.z,
                matrix[2][0] * vec.x + matrix[2][1] * vec.y + matrix[2][2] * vec.z);
    }

    /**
     * Rotate vector from the parent's space to the local transform space.
     * <p>
     * This routine returns a temporary.
     * <p>
     * Pure rotation is most useful for vectors that are orientational, such as
     * normals.
     *
     * @param vec Vector to rotate
     * @return The vector in local space
     */
    public Vector rotateParentToLocal(final Vector vec) {
        return new Vector(
                matrix[0][0] * vec.x + matrix[1][0] * vec.y + matrix[2][0] * vec.z,
                matrix[0][1] * vec.x + matrix[1][1] * vec.y + matrix[2][1] * vec.z,
                matrix[0][2] * vec.x + matrix[1][2] * vec.y + matrix[2][2] * vec.z);
    }

    /**
     * Rotate an array of vectors from the parent's space to the local transform space.
     * <p>
     * Pure rotation is most useful for vectors that are orientational, such as
     * normals.
     * <p>
     * The source and result arrays may be the same array.
     *
     * @param source Source array of vectors to transform
     * @return List of transformed vectors.
     */

    public List<Vector> rotateParentToLocal(final List<Vector> source) {
        final List<Vector> vectorList = new ArrayList<>(source.size());

        for (final Vector vector : source) {
            vectorList.add(new Vector(
                    matrix[0][0] * vector.x + matrix[1][0] * vector.y + matrix[2][0] * vector.z,
                    matrix[0][1] * vector.x + matrix[1][1] * vector.y + matrix[2][1] * vector.z,
                    matrix[0][2] * vector.x + matrix[1][2] * vector.y + matrix[2][2] * vector.z));
        }

        return vectorList;
    }

    /**
     * Transform the vector from the matrix's current frame to the parent frame.
     * <p>
     * This routine returns a temporary.
     * <p>
     * Rotation and translation is most useful for vectors that are position, such as
     * vertex data.
     *
     * @param vec Vector to rotate and translate
     * @return The vector in parent space
     */
    public Vector rotateTranslateLocalToParent(final Vector vec) {
        return new Vector(
                matrix[0][0] * vec.x + matrix[0][1] * vec.y + matrix[0][2] * vec.z + matrix[0][3],
                matrix[1][0] * vec.x + matrix[1][1] * vec.y + matrix[1][2] * vec.z + matrix[1][3],
                matrix[2][0] * vec.x + matrix[2][1] * vec.y + matrix[2][2] * vec.z + matrix[2][3]);
    }

    /**
     * Transform the vector from the parent spave to the local transform space.
     * <p>
     * This routine returns a temporary.
     * <p>
     * Rotation and translation is most useful for vectors that are position, such as
     * vertex data.
     *
     * @return The vector in local space
     */
    public Vector rotateTranslateParentToLocal(final Vector vec) {
        final float x = vec.x - matrix[0][3];
        final float y = vec.y - matrix[1][3];
        final float z = vec.z - matrix[2][3];

        return new Vector(
                matrix[0][0] * x + matrix[1][0] * y + matrix[2][0] * z,
                matrix[0][1] * x + matrix[1][1] * y + matrix[2][1] * z,
                matrix[0][2] * x + matrix[1][2] * y + matrix[2][2] * z);
    }

    /**
     * Transform an array of vectors from the parent spave to the local transform space.
     * <p>
     * Rotation and translation is most useful for vectors that are position, such as
     * vertex data.
     * <p>
     * The source and result arrays may be the same array.
     *
     * @param source Source array of vectors to transform
     * @return An array of the result vectors.
     */
    public List<Vector> rotateTranslateParentToLocal(final List<Vector> source) {
        final List<Vector> vectorList = new ArrayList<>(source.size());

        for (final Vector vector : source) {
            final float x = vector.x - matrix[0][3];
            final float y = vector.y - matrix[1][3];
            final float z = vector.z - matrix[2][3];

            vectorList.add(new Vector(
                    matrix[0][0] * x + matrix[1][0] * y + matrix[2][0] * z,
                    matrix[0][1] * x + matrix[1][1] * y + matrix[2][1] * z,
                    matrix[0][2] * x + matrix[1][2] * y + matrix[2][2] * z));
        }

        return vectorList;
    }

    public Transform rotateTranslateLocalToParent(final Transform transform) {
        final Vector i = rotateLocalToParent(transform.getLocalFrameIInParentSpace());
        final Vector j = rotateLocalToParent(transform.getLocalFrameJInParentSpace());
        final Vector k = rotateLocalToParent(transform.getLocalFrameKInParentSpace());
        final Vector p = rotateTranslateLocalToParent(transform.getPositionInParent());

        final Transform rotatedTransform = new Transform();
        rotatedTransform.setLocalFrameIJKInParentSpace(i, j, k);
        rotatedTransform.setPositionInParentSpace(p);
        rotatedTransform.reorthonormalize();

        return rotatedTransform;
    }

    public Transform rotateTranslateParentToLocal(final Transform transform) {
        final Vector i = rotateParentToLocal(transform.getLocalFrameIInParentSpace());
        final Vector j = rotateParentToLocal(transform.getLocalFrameJInParentSpace());
        final Vector k = rotateParentToLocal(transform.getLocalFrameKInParentSpace());
        final Vector p = rotateTranslateParentToLocal(transform.getPositionInParent());

        final Transform rotatedTransform = new Transform();
        rotatedTransform.setLocalFrameIJKInParentSpace(i, j, k);
        rotatedTransform.setPositionInParentSpace(p);
        rotatedTransform.reorthonormalize();

        return rotatedTransform;
    }

    /**
     * Set the uniform scale factor applied to the transform's rotation
     * matrix.
     * <p>
     * This function does not manipulate the position information within
     * the parent space.
     *
     * @param uniformScaleFactor the scale factor to apply to the diagonal
     *                           of the rotation matrix.
     */
    public void scale(final float uniformScaleFactor) {
        matrix[0][0] *= uniformScaleFactor;
        matrix[1][1] *= uniformScaleFactor;
        matrix[2][2] *= uniformScaleFactor;
    }

    /**
     * Scale the parent-space positioning information for the Transform.
     *
     * @param uniformScaleFactor the scale factor to apply to the position
     *                           of the Transform in parent space.
     */
    public void scaleParentPosition(final float uniformScaleFactor) {
        matrix[0][3] *= uniformScaleFactor;
        matrix[1][3] *= uniformScaleFactor;
        matrix[2][3] *= uniformScaleFactor;
    }

    /**
     * Get the transform-space vector pointing along the Z axis of the parent of reference.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The vector returned is in local space.
     *
     * @return The vector pointing along the Z axis of the parent's frame in local space
     */
    public Vector getParentFrameKInLocalSpace() {
        return new Vector(matrix[2][0], matrix[2][1], matrix[2][2]);
    }

    /**
     * Get the parent-space vector pointing along the Z axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The position returned is in parent space, which is world space if the
     * Transform has no parent.
     *
     * @return The vector pointing along the Z axis of the frame in parent space
     */
    public Vector getLocalFrameKInParentSpace() {
        return new Vector(matrix[0][2], matrix[1][2], matrix[2][2]);
    }

    /**
     * Get the transform-space vector pointing along the Y axis of the parent of reference.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The vector returned is in local space.
     *
     * @return The vector pointing along the Y axis of the parent's frame in local space
     */
    public Vector getParentFrameJInLocalSpace() {
        return new Vector(matrix[1][0], matrix[1][1], matrix[1][2]);
    }

    /**
     * Get the parent-space vector pointing along the Y axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The vector returned is in parent space, which is world space if the
     * Transform has no parent.
     *
     * @return The vector pointing along the Y axis of the frame in parent space
     */
    public Vector getLocalFrameJInParentSpace() {
        return new Vector(matrix[0][1], matrix[1][1], matrix[2][1]);
    }

    /**
     * Get the transform-space vector pointing along the X axis of the parent of reference.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The vector returned is in local space.
     *
     * @return The vector pointing along the X axis of the parent's frame in local space
     */
    public Vector getParentFrameIInLocalSpace() {
        return new Vector(matrix[0][0], matrix[0][1], matrix[0][2]);
    }

    /**
     * Get the parent-space vector pointing along the X axis of this frame of reference.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The vector returned is in parent space, which is world space if the
     * Transform has no parent.
     *
     * @return The vector pointing along the X axis of the frame in parent space
     */
    public Vector getLocalFrameIInParentSpace() {
        return new Vector(matrix[0][0], matrix[1][0], matrix[2][0]);
    }

    /**
     * Set the transform matrix from K and J vectors.
     * <p>
     * This routine assumes that K and J are part of a left-handed orthonormal basis.
     * If they are not, the reorthonormalize() routine must be called after this routine.
     *
     * @param k Unit vector along the Z axis
     * @param j Unit vector along the Y axis
     */
    public void setLocalFrameKJInParentSpace(final Vector k, final Vector j) {
        final Vector i = j.cross(k);
        setLocalFrameIJKInParentSpace(i, j, k);
    }

    /**
     * Set the transform matrix from the I, J, and K vectors.
     * <p>
     * This routine assumes that I, J, and K are a left-handed orthonormal basis.
     * If they are not, the reorthonormalize() routine must be called after this routine.
     *
     * @param i Unit vector along the X axis
     * @param j Unit vector along the Y axis
     * @param k Unit vector along the Z axis
     */
    public void setLocalFrameIJKInParentSpace(final Vector i, final Vector j, final Vector k) {
        matrix[0][0] = i.x;
        matrix[1][0] = i.y;
        matrix[2][0] = i.z;

        matrix[0][1] = j.x;
        matrix[1][1] = j.y;
        matrix[2][1] = j.z;

        matrix[0][2] = k.x;
        matrix[1][2] = k.y;
        matrix[2][2] = k.z;
    }

    /**
     * Set the positional offset of this transform.
     * <p>
     * The position is specified in parent space, which is world space if the
     * Transform has no parent.
     *
     * @param x New X translation for this transform
     * @param y New Y translation for this transform
     * @param z New Z translation for this transform
     */
    public void setPositionInParentSpace(final float x, final float y, final float z) {
        matrix[0][3] = x;
        matrix[1][3] = y;
        matrix[2][3] = z;
    }

    /**
     * Set the positional offset of this transform.
     * <p>
     * The position is specified in parent space, which is world space if the
     * Transform has no parent.
     *
     * @param vec New translation for this transform
     */
    public void setPositionInParentSpace(final Vector vec) {
        matrix[0][3] = vec.x;
        matrix[1][3] = vec.y;
        matrix[2][3] = vec.z;
    }

    /**
     * Get the positional offset of this transform.
     * <p>
     * This routine returns a temporary.
     * <p>
     * The position returned is in parent space, which is world space if the
     * Transform has no parent.
     *
     * @return The positional offest of this transform in parent space.
     */
    public Vector getPositionInParent() {
        return new Vector(matrix[0][3], matrix[1][3], matrix[2][3]);
    }

    /**
     * Turn the transform into the identity transform.
     * <p>
     * The identity Transform consists of a matrix of all 0 values with the exception of
     * the diagonal, which is all 1 values.  This matrix will result in no change when
     * multiplied against other matrices or transforming vectors.
     */
    public void resetRotateTranslateLocalSpaceToParentSpace() {
        matrix[0][0] = 1;
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = 0;

        matrix[1][0] = 0;
        matrix[1][1] = 1;
        matrix[1][2] = 0;
        matrix[1][3] = 0;

        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;
        matrix[2][3] = 0;
    }

    /**
     * Set the orientation to the identity orientation.
     * <p>
     * This routine does NOT affect position
     */
    public void resetRotateLocalSpaceToParentSpace() {
        matrix[0][0] = 1;
        matrix[0][1] = 0;
        matrix[0][2] = 0;

        matrix[1][0] = 0;
        matrix[1][1] = 1;
        matrix[1][2] = 0;

        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;
    }

    public void yaw(final float radians) {
        // a  b  c  d    C  0  S  0   aC-cS b aS+cC d
        // e  f  g  h *  0  1  0  0 = eC-gS f es+gC h
        // i  j  k  l   -S  0  C  0   iC-kS j iS+kS l
        // 0  0  0  1    0  0  0  1   0     0     0 1

        final float sine = (float) Math.sin(radians);
        final float cosine = (float) Math.cos(radians);

        final float a = matrix[0][0];
        final float c = matrix[0][2];
        final float e = matrix[1][0];
        final float g = matrix[1][2];
        final float i = matrix[2][0];
        final float k = matrix[2][2];

        matrix[0][0] = a * cosine + c * -sine;
        matrix[0][2] = a * sine + c * cosine;

        matrix[1][0] = e * cosine + g * -sine;
        matrix[1][2] = e * sine + g * cosine;

        matrix[2][0] = i * cosine + k * -sine;
        matrix[2][2] = i * sine + k * cosine;
    }

    public void pitch(final float radians) {
        //  a  b  c  d   1  0  0  0    a bC+cS -bS+cC d
        //  e  f  g  h * 0  C -S  0 =  e fC+gS -fS+gC h
        //  i  j  k  l   0  S  C  0    i jC+kS -jS+kC l
        //  0  0  0  1   0  0  0  1    0     0      0 1

        final float sine = (float) Math.sin(radians);
        final float cosine = (float) Math.cos(radians);

        final float b = matrix[0][1];
        final float c = matrix[0][2];
        final float f = matrix[1][1];
        final float g = matrix[1][2];
        final float j = matrix[2][1];
        final float k = matrix[2][2];

        matrix[0][1] = b * cosine + c * sine;
        matrix[0][2] = b * -sine + c * cosine;

        matrix[1][1] = f * cosine + g * sine;
        matrix[1][2] = f * -sine + g * cosine;

        matrix[2][1] = j * cosine + k * sine;
        matrix[2][2] = j * -sine + k * cosine;
    }

    public void roll(final float radians) {
        // a  b  c  d   C -S  0  0   aC+bS -aS+bC c d
        // e  f  g  h * S  C  0  0 = eC+fS -eS+fC g h
        // i  j  k  l   0  0  1  0   iC+jS -iS+jC k l
        // 0  0  0  1   0  0  0  1       0      0 0 1

        final float sine = (float) Math.sin(radians);
        final float cosine = (float) Math.cos(radians);

        final float a = matrix[0][0];
        final float b = matrix[0][1];
        final float e = matrix[1][0];
        final float f = matrix[1][1];
        final float i = matrix[2][0];
        final float j = matrix[2][1];

        matrix[0][0] = a * cosine + b * sine;
        matrix[0][1] = a * -sine + b * cosine;

        matrix[1][0] = e * cosine + f * sine;
        matrix[1][1] = e * -sine + f * cosine;

        matrix[2][0] = i * cosine + j * sine;
        matrix[2][1] = i * -sine + j * cosine;
    }

    /**
     * Set yaw pitch and roll.
     *
     * @param yaw
     * @param pitch
     * @param roll
     */
    public void setYawPitchRoll(final float yaw, final float pitch, final float roll) {
        final float cy = (float) Math.cos(yaw);
        final float sy = (float) Math.sin(yaw);
        final float cp = (float) Math.cos(pitch);
        final float sp = (float) Math.sin(pitch);
        final float cr = (float) Math.cos(roll);
        final float sr = (float) Math.sin(roll);

        matrix[0][0] = sp * sr * sy + cr * cy;
        matrix[0][1] = cr * sp * sy - cy * sr;
        matrix[0][2] = cp * sy;

        matrix[1][0] = cp * sr;
        matrix[1][1] = cp * cr;
        matrix[1][2] = -sp;

        matrix[2][0] = cy * sp * sr - cr * sy;
        matrix[2][1] = cr * cy * sp + sr * sy;
        matrix[2][2] = cp * cy;
    }

    /**
     * Set this Transform to a matrix that does nothing but scale
     * objects by the specified amount when computing from local to
     * parent space.
     *
     * @param scaleFactor the scale factor applied to x, y, z when transforming from local to parent space.
     */
    public void setToScale(final Vector scaleFactor) {
        matrix[0][0] = scaleFactor.x;
        matrix[0][1] = 0.0f;
        matrix[0][2] = 0.0f;
        matrix[0][3] = 0.0f;

        matrix[1][0] = 0.0f;
        matrix[1][1] = scaleFactor.y;
        matrix[1][2] = 0.0f;
        matrix[1][3] = 0.0f;

        matrix[2][0] = 0.0f;
        matrix[2][1] = 0.0f;
        matrix[2][2] = scaleFactor.z;
        matrix[2][3] = 0.0f;
    }

    public boolean approximates(final Transform rhs,
                                float rotationDelta,
                                float positionDelta) {
        rotationDelta = 1 - rotationDelta;
        positionDelta *= positionDelta;

        final float[] dot = new float[3];
        int i;
        for (i = 0; i < 3; ++i) {
            dot[i] = matrix[0][i] * rhs.matrix[0][i] + matrix[1][i] * rhs.matrix[1][i] + matrix[2][i] * rhs.matrix[2][i];
        }

        float dx = matrix[0][3] - rhs.matrix[0][3];
        float dy = matrix[1][3] - rhs.matrix[1][3];
        float dz = matrix[2][3] - rhs.matrix[2][3];

        for (i = 0; i < 3; ++i) {
            if (dot[i] < rotationDelta)
                return false;
        }

        return dx * dx + dy * dy + dz * dz <= positionDelta;
    }

    private void internalMultiply(final float[][] out, final float[][] left, final float[][] right) {
        if (left == out || right == out) {
            final float[][] temp = new float[3][4];

            temp[0][0] = left[0][0] * right[0][0] + left[0][1] * right[1][0] + left[0][2] * right[2][0];
            temp[0][1] = left[0][0] * right[0][1] + left[0][1] * right[1][1] + left[0][2] * right[2][1];
            temp[0][2] = left[0][0] * right[0][2] + left[0][1] * right[1][2] + left[0][2] * right[2][2];
            temp[0][3] = left[0][0] * right[0][3] + left[0][1] * right[1][3] + left[0][2] * right[2][3] + left[0][3];

            temp[1][0] = left[1][0] * right[0][0] + left[1][1] * right[1][0] + left[1][2] * right[2][0];
            temp[1][1] = left[1][0] * right[0][1] + left[1][1] * right[1][1] + left[1][2] * right[2][1];
            temp[1][2] = left[1][0] * right[0][2] + left[1][1] * right[1][2] + left[1][2] * right[2][2];
            temp[1][3] = left[1][0] * right[0][3] + left[1][1] * right[1][3] + left[1][2] * right[2][3] + left[1][3];

            temp[2][0] = left[2][0] * right[0][0] + left[2][1] * right[1][0] + left[2][2] * right[2][0];
            temp[2][1] = left[2][0] * right[0][1] + left[2][1] * right[1][1] + left[2][2] * right[2][1];
            temp[2][2] = left[2][0] * right[0][2] + left[2][1] * right[1][2] + left[2][2] * right[2][2];
            temp[2][3] = left[2][0] * right[0][3] + left[2][1] * right[1][3] + left[2][2] * right[2][3] + left[2][3];

            out[0][0] = temp[0][0];
            out[0][1] = temp[0][1];
            out[0][2] = temp[0][2];
            out[0][3] = temp[0][3];
            out[1][0] = temp[1][0];
            out[1][1] = temp[1][1];
            out[1][2] = temp[1][2];
            out[1][3] = temp[1][3];
            out[2][0] = temp[2][0];
            out[2][1] = temp[2][1];
            out[2][2] = temp[2][2];
            out[2][3] = temp[2][3];
        } else {
            out[0][0] = left[0][0] * right[0][0] + left[0][1] * right[1][0] + left[0][2] * right[2][0];
            out[0][1] = left[0][0] * right[0][1] + left[0][1] * right[1][1] + left[0][2] * right[2][1];
            out[0][2] = left[0][0] * right[0][2] + left[0][1] * right[1][2] + left[0][2] * right[2][2];
            out[0][3] = left[0][0] * right[0][3] + left[0][1] * right[1][3] + left[0][2] * right[2][3] + left[0][3];

            out[1][0] = left[1][0] * right[0][0] + left[1][1] * right[1][0] + left[1][2] * right[2][0];
            out[1][1] = left[1][0] * right[0][1] + left[1][1] * right[1][1] + left[1][2] * right[2][1];
            out[1][2] = left[1][0] * right[0][2] + left[1][1] * right[1][2] + left[1][2] * right[2][2];
            out[1][3] = left[1][0] * right[0][3] + left[1][1] * right[1][3] + left[1][2] * right[2][3] + left[1][3];

            out[2][0] = left[2][0] * right[0][0] + left[2][1] * right[1][0] + left[2][2] * right[2][0];
            out[2][1] = left[2][0] * right[0][1] + left[2][1] * right[1][1] + left[2][2] * right[2][1];
            out[2][2] = left[2][0] * right[0][2] + left[2][1] * right[1][2] + left[2][2] * right[2][2];
            out[2][3] = left[2][0] * right[0][3] + left[2][1] * right[1][3] + left[2][2] * right[2][3] + left[2][3];
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;

        final Transform transform = (Transform)obj;

        return Arrays.deepEquals(matrix, transform.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        final Quaternion q = new Quaternion(this);
        final Vector v = getPositionInParent();

        q.writeToBuffer(buffer);
        v.writeToBuffer(buffer);
    }
}
