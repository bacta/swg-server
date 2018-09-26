package io.bacta.swg.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class AttributeList implements ByteBufferWritable {
    private final List<Attribute> attributes;

    public AttributeList(int maxAttributes) {
        this.attributes = new ArrayList<>(maxAttributes);
    }

    public AttributeList(final ByteBuffer buffer) {
        this.attributes = BufferUtil.getArrayList(buffer, Attribute::new);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, attributes);
    }

    public void add(final String name, final String value) {
        attributes.add(new Attribute(name, value));
    }

    public void clear() {
        this.attributes.clear();
    }

    @RequiredArgsConstructor
    private static class Attribute implements ByteBufferWritable {
        private final String name;
        private final String value;

        public Attribute(ByteBuffer buffer) {
            name = BufferUtil.getAscii(buffer);
            value = BufferUtil.getUnicode(buffer);
        }

        @Override
        public void writeToBuffer(ByteBuffer buffer) {
            BufferUtil.putAscii(buffer, name);
            BufferUtil.putUnicode(buffer, value);
        }
    }
}
