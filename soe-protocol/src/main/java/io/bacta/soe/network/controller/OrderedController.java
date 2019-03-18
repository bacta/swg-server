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
import io.bacta.soe.network.connection.IncomingMessageProcessor;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.forwarder.GameNetworkMessageProcessor;
import io.bacta.soe.network.message.SoeMessageType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketOrdered, SoeMessageType.cUdpPacketOrdered2})
public class OrderedController implements SoeMessageController {

    private final Counter rejectedOrderedMessages;
    private final ZeroEscapeController zeroEscapeController;

    @Inject
    public OrderedController(final MetricRegistry metrics, final ZeroEscapeController zeroEscapeController) {
        rejectedOrderedMessages =  metrics.counter(MetricRegistry.name( "io.bacta.swg.login.message", "rejected-ordered"));
        this.zeroEscapeController = zeroEscapeController;
    }

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageProcessor processor) {

        IncomingMessageProcessor incomingMessageProcessor = connection.getIncomingMessageProcessor();

        short orderedStamp = buffer.getShort();
        int diff = orderedStamp - incomingMessageProcessor.getOrderedStampLast();

        if (diff <= 0) {      // equal here makes it strip dupes too
            diff += 0x10000;
        }
        if (diff < 30000) {

            incomingMessageProcessor.setOrderedStampLast(orderedStamp);
            zeroEscapeController.handleIncoming(zeroByte, type, connection, buffer.slice().order(ByteOrder.LITTLE_ENDIAN), processor);

        } else {
            rejectedOrderedMessages.inc();
        }
    }
}
