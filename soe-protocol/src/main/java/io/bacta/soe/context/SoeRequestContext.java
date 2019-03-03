package io.bacta.soe.context;

import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

@Getter
public class SoeRequestContext {

    private final SoeSessionContext sessionContext;

    public SoeRequestContext(final SoeSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public void sendMessage(GameNetworkMessage message) {
        sessionContext.sendMessage(message);
    }
}
