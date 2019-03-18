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
import io.bacta.soe.network.message.ClockReflectMessage;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.util.Clock;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * SOE Struct
 *
     struct UdpConnection::UdpPacketClockSync {
         char zeroByte;
         char packetType;
         unsigned __int16 timeStamp;
         int masterPingTime;
         int averagePingTime;
         int lowPingTime;
         int highPingTime;
         int lastPingTime;
         __int64 ourSent;
         __int64 ourReceived;
     };

    Sample Message - PreCU
     0000:   00 07 E2 7E A5 1E C4 C0 C0 E0 0F C4 EE 40 6C C8   ...~.........@l.
     0010:   C0 C0 CC 04 E5 03 81 D2 65 08 6D F5 11 00 49 C8   ........e.m...I.
     0020:   04 27 01 3C 94                                    .'.<.

         00  - Zero Byte
         07  - Packet Type = Clock Sync
         E2 7E A5 1E - Timestamp? - 3799950622  ?

 C4 C0 C0 E0 0F C4 EE 40 6C C8 C0 C0 CC 04 E5 03 81 D2 65 08 6D F5
 11 00 49 C8 04 27

    Sample Message - NGE - First Clock Sync
     0000:   00 07 72 56 00 00 00 00 00 00 00 00 00 00 00 00    ..rV............
     0010:   00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02    ................
     0020:   00 00 00 00 00 00 00 01 01 71 6C                   .........ql

         00  - Zero Byte
         07  - Packet Type = Clock Sync
         72 56   - TimeStamp =  29270  - Unknown What this is exactly
         00 00 00 00 - Master Ping Time  - 0
         00 00 00 00 - Average Ping Time  - 0
         00 00 00 00 - Low Ping Time  - 0
         00 00 00 00 - High Ping Time  - 0
         00 00 00 00 - Last Ping Time  - 0
         00 00 00 00 00 00 00 02  - Our Sent
         00 00 00 00 00 00 00 01  - Our Received

    Sample Message - NGE - Later Clock Sync
     0000:   00 07 4B 42 00 00 00 5C 00 00 00 5C 00 00 00 5C    ..KB...\...\...\
     0010:   00 00 00 5D 00 00 00 5C 00 00 00 00 00 00 00 B2    ...]...\........
     0020:   00 00 00 00 00 00 08 02 01 45 17                   .........E.

         00  - Zero Byte
         07  - Packet Type = Clock Sync
         4B 42   - TimeStamp =  19266  - Unknown What this is exactly
         00 00 00 5C - Master Ping Time  - 92
         00 00 00 5C - Average Ping Time  - 92
         00 00 00 5C - Low Ping Time  - 92
         00 00 00 5D - High Ping Time  - 93
         00 00 00 5C - Last Ping Time  - 92
         00 00 00 00 00 00 00 B2  - Our Sent - 178
         00 00 00 00 00 00 08 02  - Our Received - 2050

 */
@Component
@SoeController(handles = {SoeMessageType.cUdpPacketClockSync})
public class ClockSyncController implements SoeMessageController {

    private static final long serverStartTime = System.currentTimeMillis();

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageProcessor processor) {

        short timeStamp = buffer.getShort();
		int masterPingTime = buffer.getInt();
		int averagePingTime = buffer.getInt();
		int lowPingTime = buffer.getInt();
		int highPingTime = buffer.getInt();
		int lastPingTime = buffer.getInt();
		long ourSent = buffer.getLong();
		long ourReceived = buffer.getLong();

        connection.updatePingData(masterPingTime, averagePingTime, lowPingTime, highPingTime, lastPingTime);

        ClockReflectMessage outMessage = new ClockReflectMessage(
                timeStamp,
                Clock.now(),
                ourSent,
                ourReceived,
                connection.getProtocolMessagesSent(),
                connection.getProtocolMessagesReceived()
        );

        connection.sendMessage(outMessage);
    }

}
