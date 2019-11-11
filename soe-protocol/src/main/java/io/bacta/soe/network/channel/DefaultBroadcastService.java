package io.bacta.soe.network.channel;

import io.bacta.shared.GameNetworkMessage;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Holder class for the connection retrieval method
 */
@Component
public final class DefaultBroadcastService implements BroadcastService {

    @Setter
    private SoeMessageChannel channel;

    @Override
    public void broadcast(GameNetworkMessage message) {
        if(channel != null) {
            channel.broadcast(message);
        } else {
            throw new BroadcastChannelNotReady();
        }
    }
}
