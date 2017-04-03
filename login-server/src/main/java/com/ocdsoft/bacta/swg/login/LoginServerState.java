package com.ocdsoft.bacta.swg.login;

import com.ocdsoft.bacta.engine.network.client.ServerStatus;
import com.ocdsoft.bacta.swg.protocol.ServerState;
import com.ocdsoft.bacta.swg.protocol.ServerType;

/**
 * Created by Kyle on 3/22/14.
 */
public class LoginServerState implements ServerState {

    private ServerStatus serverStatus;

    public LoginServerState() {
        serverStatus = ServerStatus.DOWN;
    }

    public int getClusterId() {
        return 0;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.LOGIN;
    }

    @Override
    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    @Override
    public void setServerStatus(ServerStatus status) {
        this.serverStatus = status;
    }
}
