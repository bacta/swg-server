package io.bacta.soe.network.channel;

import io.bacta.shared.GameNetworkMessage;

public interface BroadcastService {
    void broadcast(GameNetworkMessage message);
    void setChannel(SoeMessageChannel messageChannel);
}
