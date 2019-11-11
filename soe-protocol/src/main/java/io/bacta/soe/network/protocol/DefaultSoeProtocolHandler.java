/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.soe.network.protocol;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.SoeEncryption;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Handler of the SOE protocol for decoding and encoding incoming
 * Datagram messages
 *
 * @author Kyle Burkhardt
 * @since 1.0
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class DefaultSoeProtocolHandler implements SoeProtocolHandler {

    private final SoeEncryption encryption;
    private final SoeNetworkConfiguration networkConfiguration;

    @Inject
    public DefaultSoeProtocolHandler(final SoeNetworkConfiguration networkConfiguration,
                                     final SoeEncryption encryption) {
        this.encryption = encryption;
        this.networkConfiguration = networkConfiguration;
        encryption.setCompression(networkConfiguration.isCompression());
    }

    /**
     * Handles applying SOE protocol to outgoing messages
     *
     * @param soeUdpConnection reference to user receiving this reference
     * @param buffer message buffer without any encoding
     * @return SOE Encoded and Compressed {@link ByteBuffer}
     */
    @Override
    public ByteBuffer processOutgoing(SoeUdpConnection soeUdpConnection, ByteBuffer buffer) {
        SoeMessageType packetType = SoeMessageType.values()[buffer.get(1)];

        if (packetType != SoeMessageType.cUdpPacketConnect && packetType != SoeMessageType.cUdpPacketConfirm) {
            buffer = encryption.encode(soeUdpConnection.getEncryptCode(), buffer, true);
            encryption.appendCRC(soeUdpConnection.getEncryptCode(), buffer, networkConfiguration.getCrcBytes());
            buffer.rewind();
        }

        buffer.rewind();
        return buffer;
    }

    /**
     * Handles unwrapping SOE protocol for incoming {@link ByteBuffer} and dispatching
     * to
     *
     * @param soeUdpConnection
     * @param buffer
     */
    @Override
    public ByteBuffer processIncoming(SoeUdpConnection soeUdpConnection, ByteBuffer buffer, SoeMessageType packetType) {

        ByteBuffer decodedBuffer;
        if (packetType != SoeMessageType.cUdpPacketConnect && packetType != SoeMessageType.cUdpPacketConfirm && packetType != SoeMessageType.cUdpPacketUnreachableConnection) {
            decodedBuffer = encryption.decode(soeUdpConnection.getEncryptCode(), buffer.order(ByteOrder.LITTLE_ENDIAN));
        } else {
            decodedBuffer = buffer;
        }

        if(decodedBuffer == null) {
            LOGGER.warn("Unable to decode incoming message {}", BufferUtil.bytesToHex(buffer));
        }

        return decodedBuffer;
    }
}
