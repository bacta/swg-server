package bacta.io.soe.network.controller;

import bacta.io.soe.network.dispatch.GameNetworkMessageDispatcher;
import bacta.io.soe.network.dispatch.SoeMessageDispatcher;

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
