package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.message.EncryptMethod;
import io.bacta.soe.network.message.SoeMessage;
import io.bacta.soe.network.message.TerminateReason;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by kyle on 7/9/2017.
 */
@Getter
public class IncomingSoeConnection implements SoeConnection {

    private final SoeUdpConnection soeUdpConnection;

    private int bactaId;
    private String bactaUsername;

    private final List<ConnectionRole> roles;

    private long currentNetworkId;
    private String currentCharName;

    public IncomingSoeConnection(final SoeUdpConnection soeUdpConnection) {
        this.soeUdpConnection = soeUdpConnection;
        this.roles = new ArrayList<>();
    }

    @Override
    public List<ConnectionRole> getRoles() {
        return roles;
    }

    @Override
    public void addRole(ConnectionRole role) {
        roles.add(role);
        if(role != ConnectionRole.AUTHENTICATED && role != ConnectionRole.UNAUTHENTICATED) {
            roles.add(ConnectionRole.PRIVILEGED);
        }
    }

    @Override
    public boolean hasRole(ConnectionRole role) {
        for (int i = 0, size = roles.size(); i < size; ++i) {
            if (roles.get(i) == role)
                return true;
        }

        return false;
    }

    @Override
    public boolean isGod() {
        return hasRole(ConnectionRole.GOD);
    }

    @Override
    public boolean isPrivileged() {
        return hasRole(ConnectionRole.PRIVILEGED);
    }

    /**
     * Send SOE protocol layer message
     * @param message {@link SoeMessage} to send
     */
    @Override
    public void sendMessage(SoeMessage message) {
        this.soeUdpConnection.sendMessage(message);
    }

    /**
     * Send game protocol layer message
     * @param message {@link GameNetworkMessage} to send
     */
    @Override
    public void sendMessage(GameNetworkMessage message) {
        this.soeUdpConnection.sendMessage(message);
    }

    @Override
    public ConnectionState getState() {
        return soeUdpConnection.getConnectionState();
    }

    @Override
    public void setState(ConnectionState state) {
        soeUdpConnection.setConnectionState(state);
    }

    @Override
    public void connect(Consumer<SoeUdpConnection> connectCallback) {
        soeUdpConnection.connect(connectCallback);
    }

    @Override
    public void disconnect() {
        soeUdpConnection.terminate(TerminateReason.NONE);
    }

    @Override
    public boolean isConnected() {
        return getState() == ConnectionState.ONLINE;
    }

    @Override
    public void confirmed(int connectionID, int encryptCode, byte crcBytes, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2, int maxRawPacketSize) {
        this.soeUdpConnection.confirmed(connectionID, encryptCode, crcBytes, encryptMethod1, encryptMethod2, maxRawPacketSize);
    }

    @Override
    public void logReceivedMessage(GameNetworkMessage incomingMessage) {
        soeUdpConnection.logReceivedMessage(incomingMessage);
    }
}
