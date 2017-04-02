package com.ocdsoft.bacta.soe.protocol.controller;

import com.ocdsoft.bacta.soe.protocol.dispatch.GameNetworkMessageDispatcher;
import com.ocdsoft.bacta.soe.protocol.dispatch.SoeMessageDispatcher;

/**
 * Created by kburkhardt on 1/26/15.
 */
public abstract class BaseSoeController implements SoeMessageController {

    protected SoeMessageDispatcher soeMessageDispatcher;
    protected GameNetworkMessageDispatcher gameNetworkMessageDispatcher;

    public void setSoeMessageDispatcher(final SoeMessageDispatcher soeMessageDispatcher) {
        this.soeMessageDispatcher = soeMessageDispatcher;
    }

    public void setGameNetworkMessageDispatcher(final GameNetworkMessageDispatcher gameNetworkMessageDispatcher) {
        this.gameNetworkMessageDispatcher = gameNetworkMessageDispatcher;
    }
}
