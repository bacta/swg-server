package bacta.io.soe.network.dispatch;

import bacta.io.network.dispatch.MessageDispatcher;
import bacta.io.soe.network.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

/**
 * Created by kburkhardt on 2/10/15.
 */
public interface SoeMessageDispatcher extends MessageDispatcher {
    void dispatch(SoeUdpConnection client, ByteBuffer buffer);
}
