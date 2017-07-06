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

import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {
        SoeMessageType.cUdpPacketReliable1,
        SoeMessageType.cUdpPacketReliable2,
        SoeMessageType.cUdpPacketReliable3,
        SoeMessageType.cUdpPacketReliable4,
        SoeMessageType.cUdpPacketFragment1,
        SoeMessageType.cUdpPacketFragment2,
        SoeMessageType.cUdpPacketFragment3,
        SoeMessageType.cUdpPacketFragment4})
public class ReliableMessageController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReliableMessageController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short sequenceNum = buffer.getShort();
        LOGGER.trace("{} Receiving Reliable Message Sequence {} {}", connection.getId(), sequenceNum, buffer.order());
        connection.sendAck(sequenceNum);

        if(type == SoeMessageType.cUdpPacketFragment1 ||
                type == SoeMessageType.cUdpPacketFragment2 ||
                type == SoeMessageType.cUdpPacketFragment3 ||
                type == SoeMessageType.cUdpPacketFragment4) {

            buffer = connection.addIncomingFragment(buffer);
        }

        if (buffer != null) {

            try {

                soeMessageDispatcher.dispatch(connection, buffer);

            } catch (Exception e) {
                LOGGER.error("Unable to handle ZeroEscape", e);
            }

        }
    }
}
