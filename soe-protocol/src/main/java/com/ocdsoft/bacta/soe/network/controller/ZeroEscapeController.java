package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kburkhardt on 1/9/15.
 */

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketZeroEscape})
public class ZeroEscapeController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int opcode = buffer.getInt();

        gameNetworkMessageDispatcher.dispatch(zeroByte, opcode, connection, buffer.slice().order(ByteOrder.LITTLE_ENDIAN));
    }

}
