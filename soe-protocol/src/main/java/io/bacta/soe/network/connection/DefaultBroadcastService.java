package io.bacta.soe.network.connection;

import io.bacta.shared.GameNetworkMessage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;


/**
 * Holder class for the connection retrieval method
 */
@Component
public final class DefaultBroadcastService implements BroadcastService {

    private SoeUdpConnectionCache soeConnectionCache;

    @Inject
    public DefaultBroadcastService(final SoeUdpConnectionCache soeConnectionCache) {
        this.soeConnectionCache = soeConnectionCache;
    }

    @Override
    public void broadcast(GameNetworkMessage message) {
        soeConnectionCache.broadcast(message);
    }
}
