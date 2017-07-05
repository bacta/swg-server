package bacta.io.soe.network.controller;

import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.SoeMessageType;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketOrdered, SoeMessageType.cUdpPacketOrdered2})
public class OrderedController extends BaseSoeController {

    private final Counter rejectedOrderedMessages;

    @Inject
    public OrderedController(final MetricRegistry metrics) {
        rejectedOrderedMessages =  metrics.counter(MetricRegistry.name( "com.ocdsoft.bacta.swg.login.message", "rejected-ordered"));
    }

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short orderedStamp = buffer.getShort();
        int diff = orderedStamp - connection.getOrderedStampLast();

        if (diff <= 0) {      // equal here makes it strip dupes too
            diff += 0x10000;
        }
        if (diff < 30000) {
            connection.setOrderedStampLast(orderedStamp);

            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int opcode = buffer.getInt();

            gameNetworkMessageDispatcher.dispatch(zeroByte, opcode, connection, buffer.slice().order(ByteOrder.LITTLE_ENDIAN));

        } else {
            rejectedOrderedMessages.inc();
        }
    }

}
