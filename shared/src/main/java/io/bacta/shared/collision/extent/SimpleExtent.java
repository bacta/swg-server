package io.bacta.shared.collision.extent;

import io.bacta.shared.collision.ExtentType;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.math.*;
import lombok.extern.slf4j.Slf4j;

import static io.bacta.shared.foundation.Tag.TAG_0000;

/**
 * Created by crush on 5/13/2016.
 */
@Slf4j
public class SimpleExtent extends BaseExtent {
    public static final int TAG_XSMP = Tag.convertStringToTag("XSMP");
    public static final int TAG_MSHP = Tag.convertStringToTag("MSHP");

    private MultiShape shape;

    public SimpleExtent() {
        super(ExtentType.SIMPLE);

        this.shape = new MultiShape();
    }

    public SimpleExtent(final MultiShape newShape) {
        super(ExtentType.SIMPLE);

        this.shape = newShape;
    }

    public MultiShape getShape() {
        return shape;
    }

    public void setShape(final MultiShape shape) {
        this.shape = shape;
    }

    @Override
    public void load(final Iff iff) {

        iff.enterForm(TAG_XSMP);
        {
            final int version = iff.getCurrentName();

            if (version == TAG_0000) {
                load0000(iff);
            } else {
                LOGGER.error("Failed to load. Unsupported version {}.", version);
            }
        }
        iff.exitForm(TAG_XSMP);
    }

    @Override
    public void write(final Iff iff) {
        iff.insertForm(TAG_XSMP);
        {
            iff.insertForm(TAG_0000);
            {
                writeShape(iff);
            }
            iff.exitForm(TAG_0000);
        }
        iff.exitForm(TAG_XSMP);
    }

    @Override
    public boolean contains(final Vector vector) {
        //return ContainmentUtility.isContainment(Containment3d.test(vector, getShape()));
        return false;
    }

    @Override
    public boolean contains(final Vector begin, final Vector end) {
        return contains(begin) && contains(end);
    }

    @Override
    public Range rangedIntersect(final Line3d line) {
        //return Intersect3d.intersect(line, getShape());
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Range rangedInsersect(final Ray3d ray) {
        //return Intersect3d.intersect(ray, getShape());
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Range rangedIntersect(final Segment3d segment) {
        //return Intersect3d.intersect(segment, getShape());
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BaseExtent clone() {
        return new SimpleExtent(shape);
    }

    @Override
    public void copy(final BaseExtent source) {
        final SimpleExtent simpleSource = (SimpleExtent) source;
        setShape(simpleSource.getShape());
    }

    @Override
    public void transform(final BaseExtent parent, final Transform transform, final float scale) {
        final SimpleExtent simpleSource = (SimpleExtent) parent;
        //setShape(ShapeUtils.transform(simpleSource.getShape(), transform, scale));
    }

    @Override
    public AxialBox getBoundingBox() {
        return getShape().getBoundingBox();
    }

    @Override
    public Sphere getBoundingSphere() {
        return getShape().getBoundingSphere();
    }

    @Override
    public Vector getCenter() {
        return getShape().getCenter();
    }

    @Override
    public float getRadius() {
        return getShape().calcAvoidanceRadius();
    }

    @Override
    protected boolean realIntersect(final Vector begin, final Vector end, final Vector surfaceNormal, final float time) {
        //NOTE TO FUTURE IMPLEMENTER: surfaceNormal and time are out vars. We need to return some kind of object that
        //represents these two values and if this method succeeded or not.

        throw new UnsupportedOperationException("Not implemented");
//        final Vector direction = end.subtract(begin);
//        final Vector normalized = direction.normalize();
//
//        if (normalized != null) {
//            final Ray3d ray = new Ray3d(begin, direction);
//            final boolean getSurfaceNormal = surfaceNormal != null;
//            Intersect3d.ResultData data = new Intersect3d.ResultData(getSurfaceNormal);
//
//            if (Intersect3d.insersectRayShapeWithData(ray, getShape(), data)) {
//                final float magnitude = direction.magnitude();
//
//                if (data.length <= magnitude) {
//                    if (surfaceNormal != null)
//                        surfaceNormal = data.surfaceNormal;
//
//                    if (time != 0)
//                        time = data.length / magnitude;
//
//                    return true;
//                }
//            }
//        }
//      return false;
    }

    protected void load0000(final Iff iff) {
        iff.enterForm(TAG_0000);
        {
            loadShape(iff);
        }
        iff.exitForm(TAG_0000);
    }

    protected void loadShape(final Iff iff) {
        iff.enterForm(TAG_MSHP);
        {
            final MultiShape.BaseType baseType = MultiShape.BaseType.from(iff.readInt());
            final MultiShape.ShapeType shapeType = MultiShape.ShapeType.from(iff.readInt());

            final Vector center = new Vector(iff);
            final Vector axisX = new Vector(iff);
            final Vector axisY = new Vector(iff);
            final Vector axisZ = new Vector(iff);

            final float extentX = iff.readFloat();
            final float extentY = iff.readFloat();
            final float extentZ = iff.readFloat();

            this.shape = new MultiShape(baseType, shapeType, center, axisX, axisY, axisZ, extentX, extentY, extentZ);
        }
        iff.exitForm(TAG_MSHP);
    }

    protected void writeShape(final Iff iff) {
        iff.insertChunk(TAG_MSHP);
        {
            iff.insertChunkData(getShape().getBaseType().value);
            iff.insertChunkData(getShape().getShapeType().value);

            throw new UnsupportedOperationException("Not yet implemented");

            //Needs a way to write a vector to an Iff. IffWritable interface maybe?

//            iff.insertChunkData(getShape().getCenter());
//
//            iff.insertChunkData(getShape().getAxisX());
//            iff.insertChunkData(getShape().getAxisY());
//            iff.insertChunkData(getShape().getAxisZ());
//
//            iff.insertChunkData(getShape().getExtentX());
//            iff.insertChunkData(getShape().getExtentY());
//            iff.insertChunkData(getShape().getExtentZ());
        }
    }
}
