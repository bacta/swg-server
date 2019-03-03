package io.bacta.shared.collision;

/**
 * Created by crush on 5/13/2016.
 */
public enum HitResult {
    MISS,// Primitive does not hit the test shape
    HIT,// Primitive hits the test shape
    HIT_FRONT,// Primitive hits the front side of the test shape
    HIT_BACK,// Primtiive hits the back side of the test shape
    TOUCH_FRONT,// Segment's begin point is in front of the shape and its end point is on the shape
    TOUCH_BACK,// Segment's begin point is in front of the shape and its end point is on the shape
    LEAVE_FRONT,// Segment/ray begins on the shape and goes in front of the shape
    LEAVE_BACK,// Segment/ray begins on the shape and goes behind the shape
    COPLANAR,// Primitive is coplanar with the test shape

    INVALID
}
