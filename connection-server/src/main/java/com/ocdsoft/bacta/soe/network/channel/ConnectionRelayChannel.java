package com.ocdsoft.bacta.soe.network.channel;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ocdsoft.bacta.engine.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpConnection;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/29/2017.
 */
public class ConnectionRelayChannel implements InboundMessageChannel {

    private final Cache<Integer, InetSocketAddress> connectionCache;

    public ConnectionRelayChannel() {
        connectionCache = CacheBuilder.newBuilder()
                .removalListener()
                .initialCapacity()
                .maximumSize()
                .expireAfterAccess()
                .build();
    }

    @Override
    public void receiveMessage(InetSocketAddress sender, ByteBuffer message) {


    }

}
