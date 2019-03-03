package io.bacta.shared.collision;

import io.bacta.shared.collision.extent.BaseExtent;
import io.bacta.shared.math.Transform;

/**
 * Created by crush on 5/10/2016.
 */
public class Floor implements CollisionSurface {
    public Floor() {

    }

    @Override
    public Transform getTransformObjectToParent() {
        return null;
    }

    @Override
    public Transform getTransformObjectToWorld() {
        return null;
    }

    @Override
    public float getScale() {
        return 0;
    }

    @Override
    public BaseExtent getExtentInLocal() {
        return null;
    }

    @Override
    public BaseExtent getExtentInParent() {
        return null;
    }
}
