package io.bacta.shared.tre.math;

import lombok.Getter;

/**
 * Created by crush on 5/13/2016.
 * <p>
 * MultiShape is a simple class that can be used to represent a sphere, cylinder, or box, oriented or axial.
 */
@Getter()
public class MultiShape {
    private static final float AXIS_EPSILON = 0.0000001f;

    private BaseType baseType;
    private ShapeType shapeType;
    private Vector center;
    private Vector axisX;
    private Vector axisY;
    private Vector axisZ;
    private float extentX;
    private float extentY;
    private float extentZ;

    public MultiShape() {
        this.baseType = BaseType.INVALID;
        this.shapeType = ShapeType.INVALID;
        this.center = Vector.ZERO;
        this.axisX = Vector.UNIT_X;
        this.axisY = Vector.UNIT_Y;
        this.axisZ = Vector.UNIT_Z;
        this.extentX = 1.f;
        this.extentY = 1.f;
        this.extentZ = 1.f;
    }

    public MultiShape(final BaseType baseType,
                      final Vector center,
                      final Vector axisX,
                      final Vector axisY,
                      final Vector axisZ,
                      final float extentX,
                      final float extentY,
                      final float extentZ) {
        this.baseType = baseType;
        this.shapeType = ShapeType.INVALID;
        this.center = center;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
        this.extentX = extentX;
        this.extentY = extentY;
        this.extentZ = extentZ;
    }

    public MultiShape(final BaseType baseType,
                      final ShapeType shapeType,
                      final Vector center,
                      final Vector axisX,
                      final Vector axisY,
                      final Vector axisZ,
                      final float extentX,
                      final float extentY,
                      final float extentZ) {
        this.baseType = baseType;
        this.shapeType = shapeType;
        this.center = center;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
        this.extentX = extentX;
        this.extentY = extentY;
        this.extentZ = extentZ;

        updateShapeType();
    }

    public MultiShape(final Sphere shape) {
        /*
        this.baseType = BaseType.SPHERE;
        this.shapeType= ShapeType.SPHERE;
        this.center = shape.getCenter();
        this.axisX = shape.getAxisX();
        this.axisY = shape.getAxisY();
        this.axisZ = shape.getAxisZ();
        this.extentX = shape.getExtentX();
        this.extentY = shape.getExtentY();
        this.extentZ = shape.getExtentZ();
        */

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public MultiShape(final Cylinder shape) {
        /*
        this.baseType = BaseType.CYLINDER;
        this.shapeType= ShapeType.CYLINDER;
        this.center = shape.getCenter();
        this.axisX = shape.getAxisX();
        this.axisY = shape.getAxisY();
        this.axisZ = shape.getAxisZ();
        this.extentX = shape.getExtentX();
        this.extentY = shape.getExtentY();
        this.extentZ = shape.getExtentZ();
        */

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public MultiShape(final OrientedCylinder shape) {
        /*
        this.baseType = BaseType.CYLINDER;
        this.shapeType= ShapeType.ORIENTED_CYLINDER;
        this.center = shape.getCenter();
        this.axisX = shape.getAxisX();
        this.axisY = shape.getAxisY();
        this.axisZ = shape.getAxisZ();
        this.extentX = shape.getExtentX();
        this.extentY = shape.getExtentY();
        this.extentZ = shape.getExtentZ();
        */

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public MultiShape(final AxialBox shape) {
        /*
        this.baseType = BaseType.BOX;
        this.shapeType= ShapeType.AXIAL_BOX;
        this.center = shape.getCenter();
        this.axisX = shape.getAxisX();
        this.axisY = shape.getAxisY();
        this.axisZ = shape.getAxisZ();
        this.extentX = shape.getExtentX();
        this.extentY = shape.getExtentY();
        this.extentZ = shape.getExtentZ();
        */

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public MultiShape(final YawedBox shape) {
        /*
        this.baseType = BaseType.BOX;
        this.shapeType= ShapeType.YAWED_BOX;
        this.center = shape.getCenter();
        this.axisX = shape.getAxisX();
        this.axisY = shape.getAxisY();
        this.axisZ = shape.getAxisZ();
        this.extentX = shape.getExtentX();
        this.extentY = shape.getExtentY();
        this.extentZ = shape.getExtentZ();
        */

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public MultiShape(final OrientedBox shape) {
        /*
        this.baseType = BaseType.BOX;
        this.shapeType= ShapeType.ORIENTED_BOX;
        this.center = shape.getCenter();
        this.axisX = shape.getAxisX();
        this.axisY = shape.getAxisY();
        this.axisZ = shape.getAxisZ();
        this.extentX = shape.getExtentX();
        this.extentY = shape.getExtentY();
        this.extentZ = shape.getExtentZ();
        */

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public Vector getBase() {
        //getCenter() - getAxisY() * getExtentY();
        return getCenter().subtract(getAxisY().multiply(getExtentY()));
    }

    public float getWidth() {
        return getExtentX() * 2.0f;
    }

    public float getHeight() {
        return getExtentY() * 2.0f;
    }

    public float getDepth() {
        return getExtentZ() * 2.0f;
    }

    public Sphere getSphere() {
        return new Sphere(center, extentX);
    }

    public Cylinder getCylinder() {
        return new Cylinder(getBase(), extentX, getHeight());
    }

    public OrientedCylinder getOrientedCylinder() {
        return new OrientedCylinder(getBase(), axisY, extentX, getHeight());
    }

    public AxialBox getAxialBox() {
        final Vector vec = new Vector(extentX, extentY, extentZ);
        final Vector min = center.subtract(vec);
        final Vector max = center.add(vec);

        return new AxialBox(min, max);
    }

    public YawedBox getYawedBox() {
        return new YawedBox(getBase(), axisX, axisZ, extentX, extentZ, getHeight());
    }

    public OrientedBox getOrientedBox() {
        return new OrientedBox(center, axisX, axisY, axisZ, extentX, extentY, extentZ);
    }

    public Sphere getLocalSphere() {
        return new Sphere(Vector.ZERO, extentX);
    }

    public Cylinder getLocalCylinder() {
        return new Cylinder(new Vector(0.f, -extentY, 0.f), extentX, getHeight());
    }

    public AxialBox getLocalAxialBox() {
        final Vector max = new Vector(extentX, extentY, extentZ);
        ;
        final Vector min = max.inverse();

        return new AxialBox(min, max);
    }

    public AxialBox getBoundingBox() {
        switch (shapeType) {
            case SPHERE:
            case CYLINDER:
            case AXIAL_BOX:
                return getAxialBox();

            case YAWED_BOX: {
//                final YawedBox box = getYawedBox();
//                AxialBox temp = new AxialBox();
//
//                for (int i =0 ; i < 8; ++i)
//                    temp.add(box.getCorner(i));
                throw new UnsupportedOperationException("Not yet implemented");
            }
            case ORIENTED_CYLINDER:
            case ORIENTED_BOX: {
//                final OrientedBox box = getOrientedBox();
//                AxialBox temp = new AxialBox();
//
//                for (int i =0 ; i < 8; ++i)
//                    temp.add(box.getCorner(i));
                throw new UnsupportedOperationException("Not yet implemented");
            }
            default:
                return new AxialBox();
        }
    }

    public Sphere getBoundingSphere() {
        float radius;

        if (baseType == BaseType.SPHERE)
            radius = extentX;
        else
            radius = (float) Math.sqrt(extentX * extentX + extentY * extentY + extentZ * extentZ);

        return new Sphere(center, radius);
    }

    public Transform getTransformLocalToParent() {
        final Transform temp = new Transform();
        temp.setLocalFrameIJKInParentSpace(axisX, axisY, axisZ);
        temp.setPositionInParentSpace(center);

        return temp;
    }

    public Transform getTransformParentToLocal() {
        final Transform temp = new Transform();
        temp.invert(getTransformLocalToParent());

        return temp;
    }

    public void updateShapeType() {
        final boolean xOk = axisX.magnitudeBetweenSquared(Vector.UNIT_X) < AXIS_EPSILON;
        final boolean yOk = axisY.magnitudeBetweenSquared(Vector.UNIT_Y) < AXIS_EPSILON;
        final boolean zOk = axisZ.magnitudeBetweenSquared(Vector.UNIT_Z) < AXIS_EPSILON;

        OrientType orientType = OrientType.ORIENTED;

        if (xOk && yOk && zOk)
            orientType = OrientType.AXIS_ALIGNED;
        else if (yOk)
            orientType = OrientType.YAWED;

        this.shapeType = shapeTable[baseType.value][orientType.value];
    }

    /**
     * Calculate the radius of the tight-fitting axial cylinder.
     *
     * @return
     */
    public float calcAvoidanceRadius() {
        switch (shapeType) {
            case SPHERE:
                return extentX;

            case CYLINDER:
                return extentX;

            case ORIENTED_CYLINDER: {
                // Slightly tricky - there are two contact modes between an
                // oriented cylinder and its tight-fitting axial cylinder -
                // two-contact (cylinder is tilted slightly) and four-contact
                // cylinder is on its side). The maximum of those two radii is
                // the radius of the tight-fitting cylinder.

                float sinTheta = axisY.y;
                float cosTheta = (float) Math.sqrt(axisY.x * axisY.x + axisY.z * axisY.z);

                float twoContactRadius = Math.abs(extentY * sinTheta + extentX * cosTheta);

                float blah = extentY * sinTheta;

                float fourContactRadius = (float) Math.sqrt(extentX * extentX + blah * blah);

                return Math.max(twoContactRadius, fourContactRadius);
            }
            case AXIAL_BOX:
                return (float) Math.sqrt(extentX * extentX + extentZ * extentZ);

            case YAWED_BOX:
                return (float) Math.sqrt(extentX * extentX + extentZ * extentZ);

            case ORIENTED_BOX: {
                // There's gotta be a cheaper way to calculate this radius...

                // Take four corners at one end of the box, figure out which
                // is the farthest from the Y axis. Four is sufficient because
                // of symmetry.

                final Vector Y = axisY.multiply(extentY);

                final Vector A = Y.add(axisX.multiply(extentX).add(axisZ.multiply(extentZ)));
                final Vector B = Y.add(axisX.multiply(extentX).subtract(axisZ.multiply(extentZ)));
                final Vector C = Y.subtract(axisX.multiply(extentX).add(axisZ.multiply(extentZ)));
                final Vector D = Y.subtract(axisX.multiply(extentX).subtract(axisZ.multiply(extentZ)));

                float magA = (float) Math.sqrt(A.x * A.x + A.z * A.z);
                float magB = (float) Math.sqrt(B.x * B.x + B.z * B.z);
                float magC = (float) Math.sqrt(C.x * C.x + C.z * C.z);
                float magD = (float) Math.sqrt(D.x * D.x + D.z * D.z);

                return Math.max(Math.max(magA, magB), Math.max(magC, magD));
            }
            default:
                return 0.0f;
        }
    }


    public enum BaseType {
        INVALID(-1),
        SPHERE(0),
        CYLINDER(1),
        BOX(2);

        private static final BaseType[] values = values();
        public final int value;

        BaseType(final int value) {
            this.value = value;
        }

        public static BaseType from(final int value) {
            if (value < 0 || value > BOX.value)
                return INVALID;

            return values[value];
        }
    }

    public enum OrientType {
        AXIS_ALIGNED(0),
        YAWED(1),
        ORIENTED(2);

        private static final OrientType[] values = values();
        public final int value;

        OrientType(final int value) {
            this.value = value;
        }

        public static OrientType from(final int value) {
            return values[value];
        }
    }

    public enum ShapeType {
        INVALID(-1),
        SPHERE(0),
        CYLINDER(1),
        ORIENTED_CYLINDER(2),
        AXIAL_BOX(3),
        YAWED_BOX(4),
        ORIENTED_BOX(5);

        private static final ShapeType[] values = values();
        public final int value;

        ShapeType(final int value) {
            this.value = value;
        }

        public static ShapeType from(final int value) {
            if (value < 0 || value > ORIENTED_BOX.value)
                return INVALID;

            return values[value];
        }
    }

    //This array represents [BaseType][OrientType].
    private static final ShapeType[][] shapeTable = new ShapeType[][]{
            {ShapeType.SPHERE, ShapeType.SPHERE, ShapeType.SPHERE},
            {ShapeType.CYLINDER, ShapeType.CYLINDER, ShapeType.ORIENTED_CYLINDER},
            {ShapeType.AXIAL_BOX, ShapeType.YAWED_BOX, ShapeType.ORIENTED_BOX},
    };
}
