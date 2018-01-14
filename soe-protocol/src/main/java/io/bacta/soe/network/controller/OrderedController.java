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

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.connection.SoeIncomingMessageProcessor;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketOrdered, SoeMessageType.cUdpPacketOrdered2})
public class OrderedController extends BaseSoeController {

    private final Counter rejectedOrderedMessages;

    @Inject
    public OrderedController(final MetricRegistry metrics) {
        rejectedOrderedMessages =  metrics.counter(MetricRegistry.name( "com.ocdsoft.bacta.swg.login.message", "rejected-ordered"));
    }

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeConnection connection, ByteBuffer buffer) {

        SoeUdpConnection soeUdpConnection = connection.getSoeUdpConnection();
        SoeIncomingMessageProcessor incomingMessageProcessor = soeUdpConnection.getIncomingMessageProcessor();

        short orderedStamp = buffer.getShort();
        int diff = orderedStamp - incomingMessageProcessor.getOrderedStampLast();

        if (diff <= 0) {      // equal here makes it strip dupes too
            diff += 0x10000;
        }
        if (diff < 30000) {
            incomingMessageProcessor.setOrderedStampLast(orderedStamp);

            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int opcode = buffer.getInt();

            gameNetworkMessageDispatcher.dispatch(zeroByte, opcode, connection, buffer.slice().order(ByteOrder.LITTLE_ENDIAN));

        } else {
            rejectedOrderedMessages.inc();
        }
    }

}