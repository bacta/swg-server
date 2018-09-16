package io.bacta.archive.delta;

import io.bacta.archive.AutoByteStream;
import io.bacta.archive.OnDirtyCallbackBase;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by crush on 8/14/2014.
 */
public class AutoDeltaByteStream extends AutoByteStream {
    private transient final Set<AutoDeltaVariableBase> dirtyList = new TreeSet<>();
    private transient OnDirtyCallbackBase onDirtyCallback;

    protected void addToDirtyList(final AutoDeltaVariableBase variable) {
        dirtyList.add(variable);

        if (onDirtyCallback != null)
            onDirtyCallback.onDirty();
    }

    public void addOnDirtyCallback(final OnDirtyCallbackBase onDirtyCallback) {
        this.onDirtyCallback = onDirtyCallback;
    }

    public void removeOnDirtyCallback() {
        onDirtyCallback = null;
    }

    public void unpackDeltas(final ByteBuffer buffer) {
        final short count = buffer.getShort();

        for (int i = 0; i < count; ++i) {
            final short index = buffer.getShort();
            final AutoDeltaVariableBase variable = (AutoDeltaVariableBase) members.get(index);
            variable.unpackDelta(buffer);
        }
    }

    public void packDeltas(final ByteBuffer buffer) {
        final short count = (short) getItemCount();

        buffer.putShort(count);

        if (count > 0) {
            dirtyList.stream()
                    .filter(AutoDeltaVariableBase::isDirty)
                    .forEachOrdered(variable -> {
                        buffer.putShort(variable.getIndex());
                        variable.packDelta(buffer);
                    });
        }

        dirtyList.clear();
    }

    public void clearDeltas() {
        if (!dirtyList.isEmpty()) {
            dirtyList.stream().forEach(AutoDeltaVariableBase::clearDelta);
            dirtyList.clear();
        }
    }

    public void addVariable(final AutoDeltaVariableBase variable) {
        variable.setIndex((short) members.size());
        variable.setOwner(this);
        super.addVariable(variable);
    }

    @Override
    public int getItemCount() {
        short count = 0;

        if (!dirtyList.isEmpty()) {
            count = (short) dirtyList.stream().filter(AutoDeltaVariableBase::isDirty).count();
        }

        return count;
    }
}
