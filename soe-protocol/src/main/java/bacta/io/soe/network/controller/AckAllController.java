package bacta.io.soe.network.controller;

import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketAckAll1, SoeMessageType.cUdpPacketAckAll2, SoeMessageType.cUdpPacketAckAll3, SoeMessageType.cUdpPacketAckAll4})
public class AckAllController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckAllController.class);


    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short sequenceNum = buffer.getShort();
        connection.ackAllFromClient(sequenceNum);
        LOGGER.trace("{} Client AckAll for Sequence {} {}", connection.getId(), sequenceNum, buffer.order());

    }
}
