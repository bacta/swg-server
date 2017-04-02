package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.protocol.connection.EncryptMethod;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketConfirm})
public class ConfirmController extends BaseSoeController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfirmController.class);


    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

        int connectionID = buffer.getInt();
        int encryptCode = buffer.getInt();
        byte crcBytes = buffer.get();
        boolean compression = BufferUtil.getBoolean(buffer);
        byte cryptMethod = buffer.get();
        int maxRawPacketSize = buffer.getInt();

        connection.setId(connectionID);

        connection.getConfiguration().setEncryptCode(encryptCode);
        connection.getConfiguration().setMaxRawPacketSize(maxRawPacketSize);
        connection.getConfiguration().setCrcBytes(crcBytes);
        connection.getConfiguration().setEncryptMethod(EncryptMethod.values()[cryptMethod]);
        connection.getConfiguration().setCompression(compression);

        connection.confirm();
    }
}
