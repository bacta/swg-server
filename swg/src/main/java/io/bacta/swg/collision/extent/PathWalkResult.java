package io.bacta.swg.collision.extent;

/**
 * Created by crush on 5/13/2016.
 */
public enum PathWalkResult {
    MISSED_START_TRI,     // The walk failed because the path didn't enter the mesh at the starting triangle
    CANT_ENTER,          // The walk failed because the path couldn't enter the starting triangle
    DOESNT_ENTER,        // The walk didn't enter the floor
    HIT_BEFORE_ENTER,     // The footprint circle hit an edge of the floor before the center entered the floor
    WALK_ON,             // The walk succeeded
    WALK_FAILED,         // The walk failed (probably an error)
    HIT_EDGE,            // The walk hit an edge of the mesh
    HIT_PORTAL_EDGE,      // The walk hit a portal-adjacent edge of the mesh
    HIT_PAST,            // The walk hit an edge some time in the past
    IN_CONTACT,          // The walk was unable to move because the point/circle was already in contact with something
    EXITED_MESH,         // The walk exited the mesh
    CENTER_HIT_EDGE,      // The center of the circle hit an edge
    CENTER_IN_CONTACT,    // The center of the circle is in contact with an edge
    START_LOC_INVALID,    // bad starting point for the walk (embedded in a wall or something)
}
