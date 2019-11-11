/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.soe.network.controller;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kburkhardt on 1/9/15.
 */

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketZeroEscape})
public class ZeroEscapeController implements SoeMessageController {


    private final GameNetworkMessageSerializer gameNetworkMessageSerializer;

    @Inject
    public ZeroEscapeController(final GameNetworkMessageSerializer gameNetworkMessageSerializer) {
        this.gameNetworkMessageSerializer = gameNetworkMessageSerializer;
    }

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageRelay processor) {

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int opcode = buffer.getInt();

        final GameNetworkMessage gameNetworkMessage = gameNetworkMessageSerializer.readFromBuffer(opcode, buffer);
        connection.processIncomingGNM(gameNetworkMessage);
        processor.receiveMessage(connection, gameNetworkMessage);
    }
}
