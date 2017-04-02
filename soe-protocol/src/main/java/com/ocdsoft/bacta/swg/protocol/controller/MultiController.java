package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.engine.utils.UnsignedUtil;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@SoeController(handles = {UdpPacketType.cUdpPacketMulti})
public class MultiController extends BaseSoeController {

    private static final transient Logger logger = LoggerFactory.getLogger(MultiController.class);

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

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
