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

package bacta.io.soe.network.message;


/**
 * My Struct
 *
     struct UdpConnection::UdpPacketClockReflect {
         char zeroByte;
         char packetType;
         unsigned __int16 timeStamp;
         unsigned int serverSyncStampLong;
         __int64 yourSent;
         __int64 yourReceived;
         __int64 ourSent;
         __int64 ourReceived;
     };

   Sample Message - PreCU
     0000:   00 08 E2 7E A5 1E B4 37 C3 C3 9A 01 0C 94 2E 43   ...~...7.......C
     0010:   68 AB 8F 10 DA 5A 0F 2A 7E 01 00 8E 11 06 0F 01   h....Z.*~.......
     0020:   6C 30                                             l0

         00  - Zero Byte
         08  - Packet Type = Clock Reflect
         E2 7E A5 1E   - TimeStamp =  Reflect value sent

 B4 37 C3 C3 9A 01 0C 94 2E 43 68 AB 8F 10 DA 5A 0F 2A 7E
 01 00 8E 11 06 0F

   Sample Message - NGE - First Response
     0000:   00 08 72 56 2D 0A 99 4A 00 00 00 00 00 00 00 02    ..rV-..J........
     0010:   00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 01    ................
     0020:   00 00 00 00 00 00 00 02 01 A3 DC                   ...........

         00  - Zero Byte
         08  - Packet Type = Clock Reflect
         72 56   - TimeStamp =  Reflect value sent
         2D 0A 99 4A - Server Sync Stamp - 755669322  ms since server start? @8 days
         00 00 00 00 00 00 00 02 - Connection Sent
         00 00 00 00 00 00 00 01 - Connection Received
         00 00 00 00 00 00 00 01 - Server Sent
         00 00 00 00 00 00 00 02 - Server Received

   Sample Message - NGE - Later Response
     0000:   00 08 4B 42 2D 0B 72 45 00 00 00 00 00 00 00 B2    ..KB-.rE........
     0010:   00 00 00 00 00 00 08 02 00 00 00 00 00 00 08 02    ................
     0020:   00 00 00 00 00 00 00 B2 01 10 D6                   ...........

         00  - Zero Byte
         08  - Packet Type = Clock Reflect
         4B 42   - TimeStamp =  Reflect value sent
         2D 0B 72 45 - Server Sync Stamp - 755724869  ms since server start? 55547 ms between
         00 00 00 00 00 00 00 02 - Connection Sent
         00 00 00 00 00 00 00 01 - Connection Received
         00 00 00 00 00 00 00 01 - Server Sent
         00 00 00 00 00 00 00 02 - Server Received


 */
public final class ClockReflectMessage extends SoeMessage {

	public ClockReflectMessage(final short timestamp,
                               final int serverSyncStampLong,
                               final long yourSent,
                               final long yourReceived,
                               final long ourSent,
                               final long ourReceived) {

		super(SoeMessageType.cUdpPacketClockReflect);
		
		buffer.putShort(timestamp);
        buffer.putInt(serverSyncStampLong);
        buffer.putLong(yourSent); // Client Sent ?
        buffer.putLong(yourReceived); // Client Received ?
        buffer.putLong(ourSent);
        buffer.putLong(ourReceived);
	}
}
