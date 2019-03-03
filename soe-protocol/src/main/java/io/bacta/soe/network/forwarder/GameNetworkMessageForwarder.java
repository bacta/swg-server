package io.bacta.soe.network.forwarder;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;

public interface GameNetworkMessageForwarder {
    void forward(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage);
}
