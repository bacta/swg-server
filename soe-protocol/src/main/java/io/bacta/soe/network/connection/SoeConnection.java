package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.Connection;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.message.SoeMessage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by kyle on 7/9/2017.
 */
@Getter
public final class SoeConnection implements Connection {

    private final SoeUdpConnection soeUdpConnection;

    private int bactaId;
    private String bactaUsername;

    private final List<ConnectionRole> roles;

    private long currentNetworkId;
    private String currentCharName;

    public SoeConnection(final SoeUdpConnection soeUdpConnection) {
        this.soeUdpConnection = soeUdpConnection;
        this.roles = new ArrayList<>();
    }

    public List<ConnectionRole> getRoles() {
        return roles;
    }

    public void addRole(ConnectionRole role) {
        roles.add(role);
        if(role != ConnectionRole.AUTHENTICATED && role != ConnectionRole.UNAUTHENTICATED) {
            roles.add(ConnectionRole.PRIVILEGED);
        }
    }

    public boolean hasRole(ConnectionRole role) {
        for (int i = 0, size = roles.size(); i < size; ++i) {
            if (roles.get(i) == role)
                return true;
        }

        return false;
    }

    public boolean isGod() {
        return hasRole(ConnectionRole.GOD);
    }

    public boolean isPrivileged() {
        return hasRole(ConnectionRole.PRIVILEGED);
    }

    /**
     * Send SOE protocol layer message
     * @param message {@link SoeMessage} to send
     */
    public final void sendMessage(SoeMessage message) {
        this.soeUdpConnection.sendMessage(message);
    }

    /**
     * Send game protocol layer message
     * @param message {@link GameNetworkMessage} to send
     */
    public final void sendMessage(GameNetworkMessage message) {
        this.soeUdpConnection.sendMessage(message);
    }

    public ConnectionState getState() {
        return soeUdpConnection.getConnectionState();
    }

    public void setState(ConnectionState state) {
        soeUdpConnection.setConnectionState(state);
    }

    public void connect(Consumer<SoeUdpConnection> connectCallback) {
        soeUdpConnection.connect(connectCallback);
    }

    public boolean isConnected() {
        return getState() == ConnectionState.ONLINE;
    }
}
