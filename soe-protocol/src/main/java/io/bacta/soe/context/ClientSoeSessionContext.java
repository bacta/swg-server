package io.bacta.soe.context;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.message.TerminateReason;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ClientSoeSessionContext implements SoeSessionContext {

    private final int connectionId;
    private final Set<ConnectionRole> roles;

    public ClientSoeSessionContext(int id) {
        this.connectionId = id;
        this.roles = new HashSet<>();
    }

    @Override
    public void sendMessage(GameNetworkMessage message) {

    }

    @Override
    public void addRole(ConnectionRole authenticated) {
        roles.add(authenticated);
    }

    @Override
    public void terminate(TerminateReason reason, boolean silent) {

    }
}
