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

package io.bacta.soe.network.controller;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.forwarder.GameNetworkMessageProcessor;
import io.bacta.soe.network.message.EncryptMethod;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.message.TerminateReason;
import io.bacta.soe.service.SessionKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Slf4j
@Component
@SoeController(handles = {SoeMessageType.cUdpPacketConnect})
public class ConnectController implements SoeMessageController {

    private final SoeNetworkConfiguration networkConfiguration;
    private final SessionKeyService keyService;

    @Inject
    public ConnectController(final SessionKeyService keyService,
                             final SoeNetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
        this.keyService = keyService;
    }

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageProcessor processor) {

        int protocolVersion = buffer.getInt();
        
        if(protocolVersion != networkConfiguration.getProtocolVersion()) {
            connection.terminate(TerminateReason.REFUSED);
            LOGGER.warn("Client from '{}' attempted to use a non-supported protocol version: {}", connection.getRemoteAddress().getHostString(), protocolVersion);
            return;
        }
        
        int connectionId = buffer.getInt();
        int maxRawPacketSize = buffer.getInt();
        int encryptCode = keyService.getNextKey();

        final EncryptMethod encryptMethod1, encryptMethod2;
        if (networkConfiguration.isCompression()) {
            encryptMethod1 = EncryptMethod.USERSUPPLIED;
            encryptMethod2 = EncryptMethod.XOR;
        } else {
            encryptMethod1 = EncryptMethod.NONE;
            encryptMethod2 = EncryptMethod.NONE;
        }

        connection.doConfirm(
                connectionId,
                encryptCode,
                maxRawPacketSize,
                encryptMethod1,
                encryptMethod2
        );
    }
}
