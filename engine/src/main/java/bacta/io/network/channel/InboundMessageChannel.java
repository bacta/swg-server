package bacta.io.network.channel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
public interface InboundMessageChannel {
    void receiveMessage(InetSocketAddress sender, ByteBuffer message);
}
