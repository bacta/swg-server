package io.bacta.soe.network.connection;

import io.bacta.shared.GameNetworkMessage;

public interface BroadcastService {
    void broadcast(GameNetworkMessage message);
}
