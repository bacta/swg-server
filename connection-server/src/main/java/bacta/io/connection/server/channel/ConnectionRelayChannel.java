package bacta.io.connection.server.channel;

import bacta.io.network.channel.InboundMessageChannel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/29/2017.
 */
public class ConnectionRelayChannel implements InboundMessageChannel {

    //private final Cache<Integer, InetSocketAddress> connectionCache;

    public ConnectionRelayChannel() {
//        connectionCache = CacheBuilder.newBuilder()
//                .removalListener()
//                .initialCapacity()
//                .maximumSize()
//                .expireAfterAccess()
//                .build();
    }

    @Override
    public void receiveMessage(InetSocketAddress sender, ByteBuffer message) {


    }

}
