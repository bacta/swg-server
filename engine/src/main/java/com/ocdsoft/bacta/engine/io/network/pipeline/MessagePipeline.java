package com.ocdsoft.bacta.engine.io.network.pipeline;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
public interface MessagePipeline {
    void handle(InetSocketAddress source, ByteBuffer msg);
}
