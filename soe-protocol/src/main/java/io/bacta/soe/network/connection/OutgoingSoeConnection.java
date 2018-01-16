package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.message.EncryptMethod;
import io.bacta.soe.network.message.SoeMessage;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

class OutgoingSoeConnection extends IncomingSoeConnection {

    private final Queue<GameNetworkMessage> pendingQueue = new LinkedBlockingQueue<>(100);

    OutgoingSoeConnection(SoeUdpConnection soeUdpConnection) {
        super(soeUdpConnection);
    }

    @Override
    public final void sendMessage(SoeMessage message) {
        super.sendMessage(message);
    }

    @Override
    public final void sendMessage(GameNetworkMessage message) {
        if(getState() == ConnectionState.ONLINE) {
            flushPending();
            super.sendMessage(message);
        } else {
            pendingQueue.add(message);
            if(getState() != ConnectionState.PENDING) {
                connect(null);
            }
        }
    }

    @Override
    public void connect(Consumer<SoeUdpConnection> connectCallback) {
        setState(ConnectionState.PENDING);
        super.connect(connectCallback);
    }


    @Override
    public void confirmed(int connectionID, int encryptCode, byte crcBytes, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2, int maxRawPacketSize) {
        super.confirmed(connectionID, encryptCode, crcBytes, encryptMethod1, encryptMethod2, maxRawPacketSize);
        flushPending();
    }

    private void flushPending() {
        while(!pendingQueue.isEmpty()) {
            super.sendMessage(pendingQueue.remove());
        }
    }

}
