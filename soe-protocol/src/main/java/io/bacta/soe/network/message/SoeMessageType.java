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

import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 * Created by Kyle on 8/21/2014.
 */
public enum SoeMessageType implements ByteBufferWritable {
    cUdpPacketZeroEscape(0x0),
    cUdpPacketConnect(0x1),
    cUdpPacketConfirm(0x2),
    cUdpPacketMulti(0x3),
    cUdpPacketBig(0x4),
    cUdpPacketTerminate(0x5),
    cUdpPacketKeepAlive(0x6),
    cUdpPacketClockSync(0x7),
    cUdpPacketClockReflect(0x8),
    cUdpPacketReliable1(0x9),     // Handled the same
    cUdpPacketReliable2(0xA),     //     |
    cUdpPacketReliable3(0xB),     //     |
    cUdpPacketReliable4(0xC),     //     |
    cUdpPacketFragment1(0xD),     //     |
    cUdpPacketFragment2(0xE),     //     |
    cUdpPacketFragment3(0xF),     //     |
    cUdpPacketFragment4(0x10),    //     v
    cUdpPacketAck1(0x11),
    cUdpPacketAck2(0x12),
    cUdpPacketAck3(0x13),
    cUdpPacketAck4(0x14),
    cUdpPacketAckAll1(0x15),
    cUdpPacketAckAll2(0x16),
    cUdpPacketAckAll3(0x17),
    cUdpPacketAckAll4(0x18),
    cUdpPacketGroup(0x19),
    cUdpPacketOrdered(0x1A),
    cUdpPacketOrdered2(0x1B),
    cUdpPacketPortAlive(0x1C),
    cUdpPacketUnreachableConnection(0x1D),
    cUdpPacketRequestRemap(0x1E);

    private byte value;

    SoeMessageType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.put(value);
    }

}
