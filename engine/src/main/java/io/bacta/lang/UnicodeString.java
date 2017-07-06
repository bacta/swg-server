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

package io.bacta.lang;

import io.bacta.buffer.BufferUtil;
import io.bacta.buffer.ByteBufferWritable;
import lombok.Getter;

import java.nio.ByteBuffer;

public final class UnicodeString implements ByteBufferWritable {

    public static final UnicodeString EMPTY = new UnicodeString("");

    @Getter
    private String string;

    public UnicodeString() {
        string = new String();
    }

    public UnicodeString(String string) {
        this.string = string;
    }

    public UnicodeString(char[] buffer) {
        this.string = new String(buffer);
    }

    public UnicodeString(ByteBuffer buffer) {
        this.string = BufferUtil.getUnicode(buffer);
    }

    public boolean isEmpty() {
        return string.isEmpty();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putUnicode(buffer, string);
    }
}
