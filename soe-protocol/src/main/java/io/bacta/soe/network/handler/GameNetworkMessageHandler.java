package io.bacta.soe.network.handler;

import io.bacta.engine.network.handler.MessageHandler;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.SoeSessionContext;

public interface GameNetworkMessageHandler extends MessageHandler {
    void handle(final SoeSessionContext context, final GameNetworkMessage gameNetworkMessage);
}
