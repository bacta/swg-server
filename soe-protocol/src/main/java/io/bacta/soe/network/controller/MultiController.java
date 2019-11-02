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

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.dispatch.SoeMessageDispatcher;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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

@Slf4j
@Component
@SoeController(handles = {SoeMessageType.cUdpPacketMulti})
public class MultiController implements SoeMessageController {

    private final SoeMessageDispatcher soeMessageDispatcher;

    @Inject
    public MultiController(final SoeMessageDispatcher soeMessageDispatcher) {
        this.soeMessageDispatcher = soeMessageDispatcher;
    }

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageRelay processor) {

        while (buffer.remaining() > 3) {
            
            LOGGER.trace("Buffer: {} {}", buffer, BufferUtil.bytesToHex(buffer));

            short length = SoeMessageUtil.getVariableValue(buffer);

            LOGGER.trace("Length: {}", length);

            ByteBuffer slicedMessage = buffer.slice();
            slicedMessage.limit(length);

            LOGGER.trace("Slice: {} {}", slicedMessage, BufferUtil.bytesToHex(slicedMessage));

            soeMessageDispatcher.dispatch(connection, slicedMessage, processor);
            
            buffer.position(buffer.position() + length);
        }
    }

}
