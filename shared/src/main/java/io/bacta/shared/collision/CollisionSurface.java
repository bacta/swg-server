package io.bacta.shared.collision;

import io.bacta.shared.collision.extent.BaseExtent;
import io.bacta.shared.math.Transform;

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
