package io.bacta.swg.collision;

import io.bacta.swg.math.Vector;
import io.bacta.swg.object.GameObject;

/**
 * Created by crush on 5/10/2016.
 */
public class Footprint {
    protected CollisionProperty parent;
    //protected Watcher<Object> cellWatcher;
    protected Vector positionInParent;
    protected Vector positionInWorld;
    protected float radius;
    //protected MultiListHandle floorList;
    protected float swimHeight;
    protected boolean hasTerrainHeight;
    protected boolean hasFloorHeight;
    protected boolean hasGroundHeight;
    protected float terrainHeight;
    protected float floorHeight;
    protected float groundHeight;
    protected Vector groundNormal;
    protected int addToWorldtime;
    protected Vector addToWorldPosition;
    protected boolean floating;
    protected int floatingTime;


    public Footprint(final Vector position, final float radius, final CollisionProperty parent, final float swimHeight) {

    }

    public void updatePreResolve(final float time) {

    }

    public void updatePostResolve(final float time) {

    }

//    public int elevatorMove(final int nFloors, final Transform outTransform) {
//
//    }

    public GameObject getStandingOn() {
        return null;
    }
}
