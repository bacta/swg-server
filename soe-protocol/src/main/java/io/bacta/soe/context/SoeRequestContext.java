package io.bacta.soe.context;

import akka.actor.ActorRef;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.forwarder.SwgResponseMessage;
import io.bacta.soe.network.message.TerminateReason;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Set;

@Getter
public class SoeRequestContext {

    private final ActorRef soeClient;
    private final Set<ConnectionRole> roles;
    private final InetSocketAddress remoteAddress;

    public SoeRequestContext(final ActorRef soeClient, final Set<ConnectionRole> roles, final InetSocketAddress remoteAddress) {
        this.soeClient = soeClient;
        this.roles = roles;
        this.remoteAddress = remoteAddress;
    }

    public void addRole(ConnectionRole role) {
        roles.add(role);
    }

    public void disconnect(TerminateReason refused, boolean b) {
        //TODO: Implement
    }

    public void sendMessage(GameNetworkMessage message) {
        soeClient.tell(new SwgResponseMessage(message, remoteAddress), ActorRef.noSender());
    }
}
