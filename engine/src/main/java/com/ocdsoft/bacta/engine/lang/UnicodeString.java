package com.ocdsoft.bacta.engine.lang;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.buffer.BufferUtil;
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
