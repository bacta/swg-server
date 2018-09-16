package io.bacta.soe.context;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.Getter;

public class SoeRequestContext {

    @Getter
    protected final SoeConnection connection;

    public SoeRequestContext(final SoeConnection connection) {
        this.connection = connection;
    }

    public void sendMessage(GameNetworkMessage message) {
        connection.sendMessage(message);
    }
}
