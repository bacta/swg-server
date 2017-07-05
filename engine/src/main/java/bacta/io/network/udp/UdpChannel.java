package bacta.io.network.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
public interface UdpChannel {

    public static final int MAIN = 0;

    void writeAndFlush(InetSocketAddress destination, ByteBuffer message);
}
