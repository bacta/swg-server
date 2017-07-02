package com.ocdsoft.bacta.soe.network;

/**
 * Created by kburkhardt on 2/22/14.
 */
public interface ServerState {
    int getClusterId();
    ServerStatus getServerStatus();
    void setServerStatus(ServerStatus status);
}
