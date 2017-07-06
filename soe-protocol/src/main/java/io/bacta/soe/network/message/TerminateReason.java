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

import io.bacta.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 * Created by kburkhardt on 1/9/15.
 */
public enum TerminateReason implements ByteBufferWritable {
    NONE(0x0, "None"),
    ICMPERROR(0x1, "ICMP Error"),
    TIMEOUT(0x2, "Timeout"),
    OTHERSIDETERMINATED(0x3, "Other side terminated"),
    MANAGERDELETED(0x4, "Manager deleted"),
    CONNECTFAIL(0x5, "Connect fail"),
    APPLICATION(0x6, "Application"),
    UNREACHABLE(0x7, "Unreachable soe"),
    UNACKTIMEOUT(0x8, "Unacknowledged timeout"),
    NEWATTEMPT(0x9, "New soe attempt"),
    REFUSED(0xA, "Connection refused"),
    MUTUALERROR(0xB, "Mutual connect error"),
    SELFCONNECT(0xC, "Connecting to self"),
    RELIABLEOVERFLOW(0xD, "Reliable Overflow");

    private final short value;
    private final String reason;

    TerminateReason(int value, String reason) {
        this.value = (short) value;
        this.reason = reason;
    }

    public short getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putShort(value);
    }

}
