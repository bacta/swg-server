package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.engine.io.network.ConnectionState;
import com.ocdsoft.bacta.soe.protocol.network.connection.Configuration;
import com.ocdsoft.bacta.soe.protocol.SharedNetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.network.message.ConfirmMessage;
import com.ocdsoft.bacta.soe.protocol.network.message.TerminateReason;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import com.ocdsoft.bacta.soe.protocol.service.SessionKeyService;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketConnect})
public class ConnectController extends BaseSoeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectController.class);

    private final SharedNetworkConfiguration networkConfiguration;
    private final SessionKeyService keyService;
    private final MBeanServer mBeanServer;

    @Inject
    public ConnectController(final SessionKeyService keyService,
                             final SharedNetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
        this.keyService = keyService;
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

        int protocolVersion = buffer.getInt();
        
        if(protocolVersion != networkConfiguration.getProtocolVersion()) {
            connection.terminate(TerminateReason.REFUSED);
            LOGGER.warn("Client from '{}' attempted to use a non-supported protocol version: {}", connection.getRemoteAddress().getHostString(), protocolVersion);
            return;
        }
        
        int connectionId = buffer.getInt();
        int maxRawPacketSize = buffer.getInt();
        int encryptCode = keyService.getNextKey();

        Configuration configuration = connection.getConfiguration();
        connection.setId(connectionId);
        
        configuration.setEncryptCode(encryptCode);
        configuration.setMaxRawPacketSize(maxRawPacketSize);
        
        connection.setState(ConnectionState.ONLINE);

        ConfirmMessage response = new ConfirmMessage(
                networkConfiguration.getCrcBytes(), 
                connectionId, encryptCode, 
                networkConfiguration.getEncryptMethod(), 
                networkConfiguration.isCompression(), 
                maxRawPacketSize
        );
        
        connection.sendMessage(response);

        if(!networkConfiguration.isDisableInstrumentation()) {
            try {

                if (!mBeanServer.isRegistered(connection.getBeanName())) {
                    mBeanServer.registerMBean(connection, connection.getBeanName());
                }

            } catch (Exception e) {
                LOGGER.error("Unable to register bean", e);
            }
        }
    }
}
