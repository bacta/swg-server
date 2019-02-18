package io.bacta.soe.network.dispatch;

import io.bacta.engine.network.dispatch.MessageDispatcher;
import io.bacta.soe.context.SoeRequestContext;

import java.nio.ByteBuffer;

public interface GameNetworkMessageDispatcher extends MessageDispatcher {
    void dispatch(short priority, int gameMessageType, SoeRequestContext context, ByteBuffer buffer);
}
