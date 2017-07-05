package bacta.io.soe.network.controller;

import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketAck1, SoeMessageType.cUdpPacketAck2, SoeMessageType.cUdpPacketAck3, SoeMessageType.cUdpPacketAck4})
public class AckController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception {
        short sequenceNum = buffer.getShort();
        connection.sendAck(sequenceNum);
        LOGGER.trace("{} Client Ack for Sequence {} {}", connection.getId(), sequenceNum, buffer.order());
    }
}
