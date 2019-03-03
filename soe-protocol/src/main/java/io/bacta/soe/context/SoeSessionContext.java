package io.bacta.soe.context;

import akka.actor.ActorRef;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.forwarder.SwgResponseMessage;
import io.bacta.soe.network.message.SwgTerminateMessage;
import io.bacta.soe.network.message.TerminateReason;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Set;

@Getter
public class SoeSessionContext {

    private final ActorRef soeClient;
    private final Set<ConnectionRole> roles;
    private InetSocketAddress remoteAddress = null;

    public SoeSessionContext(final ActorRef soeClient, final Set<ConnectionRole> roles) {
        this.soeClient = soeClient;
        this.roles = roles;
    }

    public void sendMessage(GameNetworkMessage message) {
        soeClient.tell(new SwgResponseMessage(message, remoteAddress), soeClient);
    }

    public void addRole(ConnectionRole authenticated) {
        roles.add(authenticated);
    }

    public void terminate(TerminateReason reason, boolean silent) {
        soeClient.tell(new SwgTerminateMessage(reason, silent, remoteAddress), soeClient);
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        if(this.remoteAddress == null) {
            this.remoteAddress = remoteAddress;
        } else {
            throw new RemoteAddressAlreadySetException(remoteAddress);
        }
    }
}
