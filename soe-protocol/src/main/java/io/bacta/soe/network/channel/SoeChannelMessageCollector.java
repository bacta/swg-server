package io.bacta.soe.network.channel;

import io.bacta.soe.network.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

public interface SoeChannelMessageCollector {
    void onReceiveEncrypted(SoeUdpConnection connection, ByteBuffer message);
    void onReceive(SoeUdpConnection connection, ByteBuffer message);

    void onSend(SoeUdpConnection connection, ByteBuffer message);
    void onSendEncrypted(SoeUdpConnection connection, ByteBuffer message);
}
