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

package io.bacta.soe.network.message;

import java.nio.ByteBuffer;

public final class MultiMessage extends SoeMessage {

    public MultiMessage(ByteBuffer inbuffer1, ByteBuffer inbuffer2) {
        super(SoeMessageType.cUdpPacketMulti);

        add(inbuffer1);
        add(inbuffer2);
    }

    public void add(ByteBuffer inbuffer) {

        assert inbuffer.remaining() <= 0xFF : "Buffer is too large ( > 0xFF ) and should never have reached this";

        int byteCount = inbuffer.remaining();
        if(byteCount > 0xFF) {
            byte sizeCount = (byte)((byteCount / 0xFF) - (byteCount % 0xFF == 0 ? 1 : 0));

            buffer.put((byte) 0xFF);
            buffer.put(sizeCount);
            byteCount -= 0xFF;

            for (int i = 0; i < sizeCount; ++i) {
                buffer.put(byteCount > 0xFF ? (byte) 0xFF : (byte) byteCount);
                byteCount -= 0xFF;
            }

        } else {
            buffer.put((byte) byteCount);
        }

        buffer.put(inbuffer);
    }
}
