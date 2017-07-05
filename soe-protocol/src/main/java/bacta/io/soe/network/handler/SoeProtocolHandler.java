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

package bacta.io.soe.network.handler;

import bacta.io.soe.config.SoeNetworkConfiguration;
import bacta.io.soe.network.SoeEncryption;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.dispatch.SoeMessageDispatcher;
import bacta.io.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
@Component
@Scope("prototype")
public final class SoeProtocolHandler {

    private final SoeEncryption encryption;
    private final SoeMessageDispatcher soeMessageDispatcher;

    @Inject
    public SoeProtocolHandler(final SoeNetworkConfiguration networkConfiguration,
                              final SoeEncryption encryption,
                              final SoeMessageDispatcher soeMessageDispatcher) {
        this.encryption = encryption;
        this.soeMessageDispatcher = soeMessageDispatcher;
        encryption.setCompression(networkConfiguration.isCompression());
    }

//    @Override
//    public void sendMessage(UdpConnection sender, ByteBuffer buffer) {
//        UdpPacketType packetType = UdpPacketType.values()[buffer.get(1)];
//
//        if (packetType != UdpPacketType.cUdpPacketConnect && packetType != UdpPacketType.cUdpPacketConfirm) {
//            buffer = protocol.encode(soe.getConfiguration().getEncryptCode(), buffer, true);
//            protocol.appendCRC(soe.getConfiguration().getEncryptCode(), buffer, 2);
//            buffer.rewind();
//        }
//
//        return buffer;
//

    public void handleIncoming(SoeUdpConnection sender, ByteBuffer buffer) {
        SoeMessageType packetType = SoeMessageType.values()[buffer.get(1)];

        ByteBuffer decodedBuffer;
        if (packetType != SoeMessageType.cUdpPacketConnect && packetType != SoeMessageType.cUdpPacketConfirm) {
            decodedBuffer = encryption.decode(sender.getConfiguration().getEncryptCode(), buffer.order(ByteOrder.LITTLE_ENDIAN));
        } else {
            decodedBuffer = buffer;
        }

        if(decodedBuffer != null) {
            sender.increaseProtocolMessageReceived();
            soeMessageDispatcher.dispatch(sender, decodedBuffer);
        } else {
            LOGGER.warn("Unhandled message {}", packetType);
        }
    }
}
