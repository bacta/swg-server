package com.ocdsoft.bacta.swg.login;

import com.ocdsoft.bacta.soe.network.ServerStatus;
import com.ocdsoft.bacta.soe.network.ServerState;
import com.ocdsoft.bacta.soe.protocol.ServerType;

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

