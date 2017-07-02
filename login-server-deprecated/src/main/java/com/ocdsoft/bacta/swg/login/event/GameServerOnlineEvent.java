package com.ocdsoft.bacta.swg.login.event;

import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.swg.login.object.ClusterData;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kyle on 6/3/2016.
 */
@AllArgsConstructor
@Getter
public class GameServerOnlineEvent implements Event {
    private final ClusterData clusterServer;
}
