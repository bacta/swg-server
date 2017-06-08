package com.ocdsoft.bacta.engine.io.network.udp;

import com.ocdsoft.bacta.engine.io.network.channel.CoreMessageChannel;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/24/2017.
 */
public class TestMessageChannel extends CoreMessageChannel<ByteBuffer> {

    @Inject
    public TestMessageChannel() {

    }

    @Override
    protected void handleIncoming(InetSocketAddress sender, ByteBuffer message) {

    }

    @Override
    protected ByteBuffer handleOutgoing(UdpConnection sender, ByteBuffer message) {
        return null;
    }
}
