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
import io.bacta.soe.network.forwarder.GameNetworkMessageProcessor;
import io.bacta.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 *         struct UdpPacketClockReflect
 *         {
 *             udp_uchar zeroByte;
 *             udp_uchar packetType;
 *             udp_ushort timeStamp;
 *             udp_uint serverSyncStampLong;
 *             udp_int64 yourSent;
 *             udp_int64 yourReceived;
 *             udp_int64 ourSent;
 *             udp_int64 ourReceived;
 *         };
 */
@Slf4j
@Component
@SoeController(handles = {SoeMessageType.cUdpPacketClockReflect})
public class ClockReflectController implements SoeMessageController {

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageProcessor processor) {

        // Controller not needed for server implementation
        //LOGGER.info("Controller not implemented");
    }

}
