package bacta.io.soe.network.controller;

import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.SoeMessageType;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kburkhardt on 1/9/15.
 */

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketZeroEscape})
public class ZeroEscapeController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int opcode = buffer.getInt();

        gameNetworkMessageDispatcher.dispatch(zeroByte, opcode, connection, buffer.slice().order(ByteOrder.LITTLE_ENDIAN));
    }

}
