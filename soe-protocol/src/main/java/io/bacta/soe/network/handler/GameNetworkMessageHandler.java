package io.bacta.soe.network.handler;

import io.bacta.engine.network.handler.MessageHandler;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.SoeRequestContext;

public interface GameNetworkMessageHandler extends MessageHandler {
    void handle(final SoeRequestContext context, final GameNetworkMessage gameNetworkMessage);
}
