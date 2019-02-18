package io.bacta.soe.network.forwarder;

import io.bacta.soe.network.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

public interface GameNetworkMessageForwarder {
    void forward(byte zeroByte, int opcode, SoeUdpConnection connection, ByteBuffer buffer);
}
