package io.bacta.shared.collision;

/**
 * Created by crush on 5/13/2016.
 */
public enum ResolutionResult {
    NO_COLLISION,// The objects don't collide
    DONT_KNOW_HOW,// We don't know how to resolve collisions between those types of objects
    PAST,// The collision happened in the past, we can't resolve it.
    FUTURE,// The collision happens too far in the future, we can't resolve it
    RESOLVED,// The objects collided, and the collision has been resolved
    FAILED// The objects collided, but we were unable to resolve the collision
}
