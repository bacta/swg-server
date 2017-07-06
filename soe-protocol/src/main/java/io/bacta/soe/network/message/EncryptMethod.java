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

/**
 * Created by kburkhardt on 2/7/15.
 */

import io.bacta.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 enum UdpManager::EncryptMethod
 {
     cEncryptMethodNone = 0x0,
     cEncryptMethodUserSupplied = 0x1,
     cEncryptMethodUserSupplied2 = 0x2,
     cEncryptMethodXorBuffer = 0x3,
     cEncryptMethodXor = 0x4,
     cEncryptMethodCount = 0x5,
 };
 */
public enum EncryptMethod implements ByteBufferWritable {
    NONE(0x0),
    USERSUPPLIED(0x1),
    USERSUPPLIED2(0x2),
    XORBUFFER(0x3),
    XOR(0x4),
    COUNT(0x5);

    private final byte value;

    EncryptMethod(int i) {
         this.value = (byte) i;
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.put(value);
    }
}
