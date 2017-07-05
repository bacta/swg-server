package bacta.io.soe.network.controller;

import bacta.io.buffer.BufferUtil;
import bacta.io.buffer.UnsignedUtil;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketGroup})
public class GroupMessageController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(GroupMessageController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

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
