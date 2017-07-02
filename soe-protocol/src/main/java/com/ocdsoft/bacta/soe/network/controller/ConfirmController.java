package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.config.SoeUdpConfiguration;
import com.ocdsoft.bacta.soe.network.EncryptMethod;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@Slf4j
@SoeController(handles = {SoeMessageType.cUdpPacketConfirm})
public class ConfirmController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        int connectionID = buffer.getInt();
        int encryptCode = buffer.getInt();
        byte crcBytes = buffer.get();
        boolean compression = BufferUtil.getBoolean(buffer);
        byte cryptMethod = buffer.get();
        int maxRawPacketSize = buffer.getInt();

        connection.setId(connectionID);

        SoeUdpConfiguration configuration = new SoeUdpConfiguration(
                connection.getConfiguration().getProtocolVersion(),
                crcBytes,
                EncryptMethod.values()[cryptMethod],
                maxRawPacketSize,
                compression
        );

        connection.setConfiguration(configuration);
        connection.confirm();
    }
}
