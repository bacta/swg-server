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

import io.bacta.network.Message;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
public abstract class SoeMessage implements Message {

    protected transient boolean compressed = true;
    protected final byte zeroByte;
    protected final SoeMessageType packetType;

    protected transient final ByteBuffer buffer;

    public SoeMessage(SoeMessageType packetType) {
        buffer = ByteBuffer.allocate(496).order(ByteOrder.BIG_ENDIAN);

        this.zeroByte = 0;
        this.packetType = packetType;

        buffer.put(zeroByte);
        packetType.writeToBuffer(buffer);
    }

    public ByteBuffer prepare() {
        buffer.limit(buffer.position());
        buffer.rewind();
        return buffer;
    }

    public ByteBuffer slice() {
        return prepare().slice();
    }

    public int size() {
        return buffer.limit();
    }

    public int position() {
        return buffer.position();
    }
}
