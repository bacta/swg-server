package io.bacta.shared.math;

import lombok.Data;

import java.util.List;

/**
 * Created by crush on 5/13/2016.
 * <p>
 * AxialBox needs not be immutable.
 */
@Data
public class AxialBox {
    private Vector min;
    private Vector max;

    public AxialBox() {
        this.min = Vector.MAX_XYZ;
        this.max = Vector.NEGATIVE_MAX_XYZ;
    }

    public AxialBox(final Vector cornerA, final Vector cornerB) {
        this.min = new Vector(
                cornerA.x < cornerB.x ? cornerA.x : cornerB.x,
                cornerA.y < cornerB.y ? cornerA.y : cornerB.y,
                cornerA.z < cornerB.z ? cornerA.z : cornerB.z);

        this.max = new Vector(
                cornerA.x < cornerB.x ? cornerB.x : cornerA.x,
                cornerA.y < cornerB.y ? cornerB.y : cornerA.y,
                cornerA.z < cornerB.z ? cornerB.z : cornerA.z);
    }

    public AxialBox(final Range rangeX, final Range rangeY, final Range rangeZ) {
        this.min = new Vector(rangeX.getMin(), rangeY.getMin(), rangeZ.getMin());
        this.max = new Vector(rangeX.getMax(), rangeY.getMax(), rangeZ.getMax());
    }

    public AxialBox(final AxialBox boxA, final AxialBox boxB) {
        this.min = new Vector(boxA.getMin());
        this.max = new Vector(boxA.getMax());

        add(boxB);
    }

    public void clear() {
        //TODO: We need to copy these static vectors here. First, I want to enforce that the statics are immutable
        min = Vector.MAX_XYZ;
        max = Vector.NEGATIVE_MAX_XYZ;
    }

    public void add(final Vector vector) {
        if (min.x > max.x) {
            addMin(vector);
            addMax(vector);
        } else {
            //TODO: This seems like it could be simplified, but the weird elseif blocks makes it non-intuitive.
            float minx = min.x, miny = min.y, minz = min.z;
            float maxx = max.x, maxy = max.y, maxz = max.z;

            if (vector.x < min.x) {
                minz = vector.x;
            } else if (vector.x > max.x) {
                maxx = vector.x;
            }

            if (vector.y < min.y) {
                miny = vector.y;
            } else if (vector.y > max.y) {
                maxy = vector.y;
            }

            if (vector.z < min.z) {
                minz = vector.z;
            } else if (vector.z > max.z) {
                maxz = vector.z;
            }

            //Only set them if a change occurred.
            if (minx < min.x || miny < min.y || minz < min.z)
                this.min = new Vector(minx, miny, minz);

            if (maxx > max.x || maxy > max.y || maxz > max.z)
                this.max = new Vector(maxx, maxy, maxz);
        }
    }

    public void addMin(final Vector vector) {
        //We only want to do this if it changes something.
        if (vector.x < min.x || vector.y < min.y || vector.z < min.z) {
            this.min = new Vector(
                    vector.x < min.x ? vector.x : min.x,
                    vector.y < min.y ? vector.y : min.y,
                    vector.z < min.z ? vector.z : min.z);
        }
    }

    public void addMax(final Vector vector) {
        //We only want to do this if it changes something.
        if (vector.x > max.x || vector.y > max.y || vector.z > max.z) {
            this.max = new Vector(
                    vector.x > max.x ? vector.x : max.x,
                    vector.y > max.y ? vector.y : max.y,
                    vector.z > max.z ? vector.z : max.z);
        }
    }

    public void add(final List<Vector> vertices) {
        vertices.forEach(this::add);
    }

    public void add(final AxialBox axialBox) {
        addMin(axialBox.getMin());
        addMax(axialBox.getMax());
    }

    public boolean contains(final Vector vector) {
        return vector.x >= min.x
                && vector.y >= min.y
                && vector.z >= min.z
                && vector.x <= max.x
                && vector.y <= max.y
                && vector.z <= max.z;
    }

    public boolean contains(final AxialBox axialBox) {
        return contains(axialBox.min) && contains(axialBox.max);
    }

    public boolean isEmpty() {
        return min.x > max.x;
    }

    public float getWidth() {
        return max.x - min.x;
    }

    public float getHeight() {
        return max.y - min.y;
    }

    public float getDepth() {
        return max.z - min.z;
    }

    public Vector getSize() {
        return max.subtract(min);
    }

    public Vector getCenter() {
        return max.add(min).divide(2.f);
    }

    public Vector getDelta() {
        return max.subtract(min).divide(2.f);
    }

    public float getRadius() {
        return getDelta().magnitude();
    }

    public float getRadiusSquared() {
        return getDelta().magnitudeSquared();
    }

    public Range getRangeX() {
        return new Range(min.x, max.x);
    }

    public Range getRangeY() {
        return new Range(min.y, max.y);
    }

    public Range getRangeZ() {
        return new Range(min.z, max.z);
    }

    public Vector getBase() {
        return new Vector((min.x + max.x) / 2.f, min.y, (min.z + max.z) / 2.f);
    }

    public Vector getCorner(final int whichCorner) {
        switch (whichCorner) {
            case 0:
                return new Vector(min.x, min.y, min.z);
            case 1:
                return new Vector(max.x, min.y, min.z);
            case 2:
                return new Vector(min.x, min.y, max.z);
            case 3:
                return new Vector(max.x, min.y, max.z);
            case 4:
                return new Vector(min.x, max.y, min.z);
            case 5:
                return new Vector(max.x, max.y, min.z);
            case 6:
                return new Vector(min.x, max.y, max.z);
            case 7:
                return new Vector(max.x, max.y, max.z);
            default:
                return new Vector(0, 0, 0);
        }
    }

    public float getVolume() {
        return (max.x - min.x) * (max.y - min.y) * (max.z - min.z);
    }

    public float getArea() {
        final Vector vector = getSize();
        return ((vector.x * vector.y) + (vector.y * vector.z) + (vector.z * vector.x)) * 2.f;
    }

    public Vector getAxisX() {
        return Vector.UNIT_X;
    }

    public Vector getAxisY() {
        return Vector.UNIT_Y;
    }

    public Vector getAxisZ() {
        return Vector.UNIT_Z;
    }

    public float getExtentX() {
        return (max.x - min.x) / 2.f;
    }

    public float getExtentY() {
        return (max.y - min.y) / 2.f;
    }

    public float getExtentZ() {
        return (max.z - min.z) / 2.f;
    }
}
