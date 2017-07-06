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

import io.bacta.buffer.BufferUtil;
import io.bacta.buffer.UnsignedUtil;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 *
 *
 * Parsing the multi byte size
 unsigned int __cdecl UdpMisc::GetVariableValue(const void *buffer, unsigned int *value)
 {
     unsigned int result; // eax@4

     if ( *(_BYTE *)buffer == 255 )
     {
         if ( *((_BYTE *)buffer + 1) != 255 || *((_BYTE *)buffer + 2) != 255 )
         {
            *value = *((_BYTE *)buffer + 2) | (*((_BYTE *)buffer + 1) << 8);
            result = 3;
         }
         else
         {
            *value = *((_BYTE *)buffer + 6) | (*((_BYTE *)buffer + 5) << 8) | (*((_BYTE *)buffer + 4) << 16) | (*((_BYTE *)buffer + 3) << 24);
            result = 7;
         }
     }
     else
     {
         *value = *(_BYTE *)buffer;
         result = 1;
     }
     return result;
 }
 */

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketMulti})
public class MultiController extends BaseSoeController {

    private static final transient Logger logger = LoggerFactory.getLogger(MultiController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        while (buffer.remaining() > 3) {
            
            logger.trace("Buffer: {} {}", buffer, BufferUtil.bytesToHex(buffer));

            short length = UnsignedUtil.getUnsignedByte(buffer);

            logger.trace("Length: {}", length);

            if (length == 0xFF) {

                short value1 = UnsignedUtil.getUnsignedByte(buffer);
                short value2 = UnsignedUtil.getUnsignedByte(buffer);

                length =  (short)(value2 | value1 << 8);
            }

            ByteBuffer slicedMessage = buffer.slice();
            slicedMessage.limit(length);

            logger.trace("Slice: {} {}", slicedMessage, BufferUtil.bytesToHex(slicedMessage));

            soeMessageDispatcher.dispatch(connection, slicedMessage);
            
            buffer.position(buffer.position() + length);
        }
    }

}
