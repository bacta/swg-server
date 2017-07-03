package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.engine.network.ConnectionState;
import com.ocdsoft.bacta.soe.network.message.ConfirmMessage;
import com.ocdsoft.bacta.soe.network.message.TerminateReason;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.config.SoeUdpConfiguration;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import com.ocdsoft.bacta.soe.service.SessionKeyService;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
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
