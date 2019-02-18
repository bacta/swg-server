package io.bacta.soe.network.forwarder;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@AllArgsConstructor
@Getter
public class SwgRequestMessage {
    final byte zeroByte;
    final int opcode;
    final InetSocketAddress remoteAddress;
    final ByteBuffer buffer;
}
