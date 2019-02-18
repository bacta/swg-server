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
import io.bacta.engine.buffer.UnsignedUtil;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.dispatch.SoeMessageDispatcher;
import io.bacta.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Slf4j
@Component
@SoeController(handles = {SoeMessageType.cUdpPacketGroup})
public class GroupMessageController implements SoeMessageController {

    private final SoeMessageDispatcher soeMessageDispatcher;

    @Inject
    public GroupMessageController(final SoeMessageDispatcher soeMessageDispatcher) {
        this.soeMessageDispatcher = soeMessageDispatcher;
    }

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        while (buffer.remaining() > 3) {

            LOGGER.trace("Buffer: {} {}", buffer, BufferUtil.bytesToHex(buffer));

            int length = UnsignedUtil.getUnsignedByte(buffer);

            LOGGER.trace("Length: {}", length);

            ByteBuffer slicedBuffer = buffer.slice();
            slicedBuffer.limit(length);

            LOGGER.trace("Slice: {} {}", slicedBuffer, BufferUtil.bytesToHex(slicedBuffer));

            soeMessageDispatcher.dispatch(connection, slicedBuffer);

            buffer.position(buffer.position() + length);
        }
    }
}
