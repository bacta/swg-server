package io.bacta.soe.context;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.message.TerminateReason;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class SoeRequestContext {

    private final SoeSessionContext sessionContext;

    public SoeRequestContext(SoeSessionContext soeSessionContext) {
        this.sessionContext = soeSessionContext;
    }

    public void sendMessage(GameNetworkMessage message) {
        sessionContext.sendMessage(message);
    }

    public InetSocketAddress getRemoteAddress() {
        return sessionContext.getRemoteAddress();
    }

    public void disconnect(TerminateReason refused, boolean b) {
        //TODO: Implement
    }
}
