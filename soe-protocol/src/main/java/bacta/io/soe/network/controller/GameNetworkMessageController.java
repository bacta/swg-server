package bacta.io.soe.network.controller;

import bacta.io.network.controller.MessageController;
import bacta.io.soe.network.connection.SoeUdpConnection;
import io.bacta.shared.GameNetworkMessage;

public interface GameNetworkMessageController<Data extends GameNetworkMessage> extends MessageController {
    void handleIncoming(SoeUdpConnection connection, Data message) throws Exception;
}
