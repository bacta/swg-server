package io.bacta.swg.collision;

import io.bacta.swg.collision.extent.BaseExtent;
import io.bacta.swg.math.Transform;

/**
 * Created by crush on 5/10/2016.
 */
public interface CollisionSurface {
    Transform getTransformObjectToParent();

    Transform getTransformObjectToWorld();

    float getScale();

    BaseExtent getExtentInLocal();

    BaseExtent getExtentInParent();
}
