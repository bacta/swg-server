package io.bacta.soe.context;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.message.TerminateReason;

public interface SoeSessionContext {
    int getConnectionId();

    void sendMessage(GameNetworkMessage message);

    void addRole(ConnectionRole authenticated);

    void terminate(TerminateReason reason, boolean silent);

    java.util.Set<ConnectionRole> getRoles();
}
