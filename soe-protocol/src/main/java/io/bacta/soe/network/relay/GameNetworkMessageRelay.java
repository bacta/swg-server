package io.bacta.soe.network.relay;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;

public interface GameNetworkMessageRelay {
    void receiveMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage);
    void sendMessage(final int connectionId, GameNetworkMessage message);
}
