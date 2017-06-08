package com.ocdsoft.bacta.soe.protocol.network.dispatch;

import com.ocdsoft.bacta.engine.io.network.dispatch.MessageDispatcher;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

/**
 * Created by kburkhardt on 2/10/15.
 */
public interface SoeMessageDispatcher extends MessageDispatcher {
    void dispatch(SoeUdpConnection client, ByteBuffer buffer);
}
