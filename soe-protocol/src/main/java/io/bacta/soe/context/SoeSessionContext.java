package io.bacta.soe.context;

import akka.actor.ActorRef;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.message.TerminateReason;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SoeSessionContext {

    private final ActorRef connection;
    private final InetSocketAddress remoteAddress;
    private final List<ConnectionRole> roles;
    private final Consumer<GameNetworkMessage> sendMethod;

    private String currentCharName;
    private boolean god = false;

    public SoeSessionContext(final ActorRef connection, final InetSocketAddress remoteAddress, Consumer<GameNetworkMessage> sendMethod) {
        this.connection = connection;
        this.remoteAddress = remoteAddress;
        this.roles = new ArrayList<>();
        this.sendMethod = sendMethod;
    }

    public void sendMessage(GameNetworkMessage message) {
        sendMethod.accept(message);
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void disconnect(TerminateReason refused, boolean b) {
        //TODO: Implement
    }

    public List<ConnectionRole> getRoles() {
        return roles;
    }

    public void addRole(ConnectionRole role) {
        roles.add(role);
    }

    public SoeRequestContext newRequest() {
        return new SoeRequestContext(this);
    }
}
