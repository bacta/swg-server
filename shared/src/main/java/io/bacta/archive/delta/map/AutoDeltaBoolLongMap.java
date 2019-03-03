package io.bacta.archive.delta.map;

import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.hash.TByteLongHashMap;
import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AutoDeltaBoolLongMap extends AutoDeltaContainer {
    private transient final List<Command> changes;
    private final TByteLongMap container;
    private transient int baselineCommandCount;

    public AutoDeltaBoolLongMap() {
        this.changes = new ArrayList<>(5);
        this.container = new TByteLongHashMap();
        this.baselineCommandCount = 0;
    }

    public void clear() {
        container.forEachEntry((key, value) -> {
            erase(key != 0);
            return true;
        });
    }

    @Override
    public void clearDelta() {
        changes.clear();
    }

    public void erase(final boolean key) {
        final long value = container.get(key ? (byte) 1 : (byte) 0);
        final Command command = new Command(Command.ERASE, key, value);
        changes.add(command);
        ++baselineCommandCount;
        container.remove(key ? (byte) 1 : (byte) 0);
        touch();
        onErase(key, value);
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    public TByteLongIterator iterator() {
        return container.iterator();
    }

    public boolean containsKey(final boolean key) {
        return container.containsKey(key ? (byte) 1 : (byte) 0);
    }

    public long get(final boolean key) {
        return container.get(key ? (byte) 1 : (byte) 0);
    }

    public TByteLongMap getMap() {
        return container;
    }

    public void insert(final boolean key, final long value) {
        if (containsKey(key))
            return;

        final Command command = new Command(Command.ADD, key, value);
        container.put(key ? (byte) 1 : (byte) 0, value);
        touch();
        onInsert(key, value);
        changes.add(command);
        ++baselineCommandCount;
    }

    @Override
    public boolean isDirty() {
        return !changes.isEmpty();
    }

    @Override
    public int size() {
        return container.size();
    }

    public void set(final boolean key, final long value) {
        if (!containsKey(key)) {
            //Inserting...
            final Command command = new Command(Command.ADD, key, value);
            container.put(key ? (byte) 1 : (byte) 0, value);
            touch();
            onInsert(key, value);
            changes.add(command);
            ++baselineCommandCount;
        } else {
            //Setting...
            final Command command = new Command(Command.SET, key, value);
            final long oldValue = container.get(key ? (byte) 1 : (byte) 0);
            container.put(key ? (byte) 1 : (byte) 0, value);
            touch();
            onSet(key, oldValue, value);
            changes.add(command);
            ++baselineCommandCount;
        }
    }

    @Override
    public void pack(final ByteBuffer buffer) {
        buffer.putInt(container.size());
        buffer.putInt(baselineCommandCount);

        container.forEachEntry((key, value) -> {
            buffer.put(Command.ADD);
            BufferUtil.put(buffer, key);
            BufferUtil.put(buffer, value);
            return true;
        });
    }

    @Override
    public void packDelta(final ByteBuffer buffer) {
        buffer.putInt(changes.size());
        buffer.putInt(baselineCommandCount);

        changes.stream().forEach(command -> {
            command.writeToBuffer(buffer);
        });

        clearDelta();
    }

    @Override
    public void unpack(final ByteBuffer buffer) {
        container.clear();
        clearDelta();

        final int commandCount = buffer.getInt();
        baselineCommandCount = buffer.getInt();

        for (int i = 0; i < commandCount; ++i) {
            final Command command = new Command(buffer);
            assert command.cmd == Command.ADD : "Only add is valid in unpack";
            container.put(command.key ? (byte) 1 : (byte) 0, command.value);
            onInsert(command.key, command.value);
        }
    }

    @Override
    public void unpackDelta(final ByteBuffer buffer) {
        int skipCount;

        final int commandCount = buffer.getInt();
        final int targetBaselineCommandCount = buffer.getInt();

        //if (commandCount + baselineCommandCount) < targetBaselineCommandCount, it
        //means that we have missed some changes and are behind; when this happens,
        //catch up by applying all the deltas that came in, and set
        //baselineCommandCont to targetBaselineCommandCount

        if ((commandCount + baselineCommandCount) > targetBaselineCommandCount)
            skipCount = commandCount + baselineCommandCount - targetBaselineCommandCount;
        else
            skipCount = 0;

        //If this fails, it means that the deltas we are receiving are relative to baselines
        //which are newer than what we currently have. This usually means either we were not
        //observing an object for a time when deltas were sent, but aren't getting new
        //baselines, or our version of the container has been modified locally.
        if (skipCount > commandCount)
            skipCount = commandCount;

        int i;
        for (i = 0; i < skipCount; ++i) {
            final byte cmd = buffer.get();
            final boolean key = buffer.get() != 0;
            final long value = buffer.getLong();
        }

        for (; i < commandCount; ++i) {
            final Command command = new Command(buffer);

            switch (command.cmd) {
                case Command.ADD:
                case Command.SET:
                    set(command.key, command.value);
                    break;
                case Command.ERASE:
                    erase(command.key);
                    break;
                default:
                    assert false : "Unknown command";
                    break;
            }
        }

        //If we are behind, catch up.
        if (baselineCommandCount < targetBaselineCommandCount)
            baselineCommandCount = targetBaselineCommandCount;
    }

    private void onErase(final boolean key, final long value) {
        //callback
    }

    private void onInsert(final boolean key, final long value) {
        //callback
    }

    private void onSet(final boolean key, final long oldValue, final long newValue) {
        //callback
    }

    public static class Command implements ByteBufferWritable {
        public static final byte ADD = 0x0;
        public static final byte ERASE = 0x1;
        public static final byte SET = 0x2;

        public final byte cmd;
        public final boolean key;
        public final long value;

        public Command(int cmd, boolean key, long value) {
            this.cmd = (byte) cmd;
            this.key = key;
            this.value = value;
        }

        public Command(final ByteBuffer buffer) {
            this.cmd = buffer.get();
            this.key = buffer.get() != 0;
            this.value = buffer.getLong();
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            buffer.put(this.cmd);
            BufferUtil.put(buffer, key);
            BufferUtil.put(buffer, value);
        }
    }
}
