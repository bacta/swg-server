package io.bacta.game.waypoint;


import io.bacta.swg.util.NetworkId;
import lombok.Getter;

/**
 * Created by crush on 8/13/2014.
 */
@Getter
public final class WaypointData extends WaypointDataBase {
    private static final WaypointData invalidWaypoint = new WaypointData();

    public static WaypointData getInvalidWaypoint() {
        return invalidWaypoint;
    }

    long networkId;

    WaypointData() {
        networkId = NetworkId.INVALID;
    }

    WaypointData(final long networkId) {
        this.networkId = networkId;
    }
}
