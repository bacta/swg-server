package io.bacta.archive;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 8/13/2014.
 */
public class AutoByteStream {
    protected final List<AutoVariableBase> members = new ArrayList<>();

    public int getItemCount() {
        return members.size();
    }

    public final void addVariable(final AutoVariableBase variable) {
        members.add(variable);
    }

    public void pack(final ByteBuffer buffer) {
        final short size = (short) members.size();
        buffer.putShort(size);

        for (final AutoVariableBase variable : members) {
            variable.pack(buffer);
        }
    }

    public void unpack(final ByteBuffer buffer) {
        throw new RuntimeException("Not implemented.");
    }
}
