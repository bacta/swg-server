package com.ocdsoft.bacta.engine.network;

/**
 * Created by kburkhardt on 2/22/14.
 */
public interface ServerState {
    int getClusterId();
    String getServerType();
    ServerStatus getServerStatus();
    void setServerStatus(ServerStatus status);
}
