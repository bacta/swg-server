package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.engine.utils.UnsignedUtil;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketGroup})
public class GroupMessageController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(GroupMessageController.class);

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

        while (buffer.remaining() > 3) {

            logger.trace("Buffer: {} {}", buffer, BufferUtil.bytesToHex(buffer));

            int length = UnsignedUtil.getUnsignedByte(buffer);

            logger.trace("Length: {}", length);

            ByteBuffer slicedBuffer = buffer.slice();
            slicedBuffer.limit(length);

            logger.trace("Slice: {} {}", slicedBuffer, BufferUtil.bytesToHex(slicedBuffer));

            soeMessageDispatcher.dispatch(connection, slicedBuffer);

            buffer.position(buffer.position() + length);
        }
    }
}
