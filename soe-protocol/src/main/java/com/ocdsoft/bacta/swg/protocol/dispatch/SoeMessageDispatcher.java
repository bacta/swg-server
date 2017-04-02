package com.ocdsoft.bacta.swg.protocol.dispatch;

import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

/**
 * Created by kburkhardt on 2/10/15.
 */
public interface SoeMessageDispatcher {
    void dispatch(SoeUdpConnection client, ByteBuffer buffer);
}
