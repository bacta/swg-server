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

import io.bacta.network.ConnectionState;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.config.SoeUdpConfiguration;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.ConfirmMessage;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.message.TerminateReason;
import io.bacta.soe.service.SessionKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketConnect})
public class ConnectController extends BaseSoeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectController.class);

    private final SoeNetworkConfiguration networkConfiguration;
    private final SessionKeyService keyService;

    @Inject
    public ConnectController(final SessionKeyService keyService,
                             final SoeNetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
        this.keyService = keyService;
    }

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        int protocolVersion = buffer.getInt();
        
        if(protocolVersion != networkConfiguration.getProtocolVersion()) {
            connection.terminate(TerminateReason.REFUSED);
            LOGGER.warn("Client from '{}' attempted to use a non-supported protocol version: {}", connection.getRemoteAddress().getHostString(), protocolVersion);
            return;
        }
        
        int connectionId = buffer.getInt();
        int maxRawPacketSize = buffer.getInt();
        int encryptCode = keyService.getNextKey();

        SoeUdpConfiguration configuration = connection.getConfiguration();
        connection.setId(connectionId);
        
        configuration.setEncryptCode(encryptCode);
        configuration.setMaxRawPacketSize(maxRawPacketSize);
        
        connection.setState(ConnectionState.ONLINE);

        ConfirmMessage response = new ConfirmMessage(
                networkConfiguration.getCrcBytes(), 
                connectionId,
                encryptCode,
                networkConfiguration.getEncryptMethod(), 
                networkConfiguration.isCompression(), 
                maxRawPacketSize
        );
        
        connection.sendMessage(response);
    }
}
