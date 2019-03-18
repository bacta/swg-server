package io.bacta.soe.network.forwarder;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;

public interface GameNetworkMessageProcessor {
    void process(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage);
}
