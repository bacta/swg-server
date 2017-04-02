package com.ocdsoft.bacta.soe.protocol.message;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;

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
    UNREACHABLE(0x7, "Unreachable connection"),
    UNACKTIMEOUT(0x8, "Unacknowledged timeout"),
    NEWATTEMPT(0x9, "New connection attempt"),
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
