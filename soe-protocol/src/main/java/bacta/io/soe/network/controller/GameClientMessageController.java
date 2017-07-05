package bacta.io.soe.network.controller;

import bacta.io.soe.network.connection.SoeUdpConnection;
import io.bacta.shared.GameNetworkMessage;

/**
 * Created by crush on 5/27/2016.
 */
public interface GameClientMessageController<T extends GameNetworkMessage> {
    void handleIncoming(long[] distributionList, boolean reliable, T message, SoeUdpConnection connection) throws Exception;
}
