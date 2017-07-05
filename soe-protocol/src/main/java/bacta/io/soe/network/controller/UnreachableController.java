package bacta.io.soe.network.controller;

import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.SoeMessageType;
import bacta.io.soe.network.message.TerminateReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketUnreachableConnection})
public class UnreachableController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(UnreachableController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.terminate(TerminateReason.OTHERSIDETERMINATED, true);
    }
}
