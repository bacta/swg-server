package io.bacta.archive.delta.map;

import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AutoDeltaByteObjectMap<V extends ByteBufferWritable> extends AutoDeltaContainer {
    private transient final List<Command<V>> changes;
    private final TByteObjectMap<V> container;
    private transient int baselineCommandCount;
    private final Function<ByteBuffer, V> valueCreator;

    public AutoDeltaByteObjectMap(Function<ByteBuffer, V> valueCreator) {
        this.changes = new ArrayList<>(5);
        this.container = new TByteObjectHashMap<>();
        this.baselineCommandCount = 0;
        this.valueCreator = valueCreator;
    }

    public void clear() {
        container.forEachEntry((key, value) -> {
            erase(key);
            return true;
        });
    }

    @Override
    public void clearDelta() {
        changes.clear();
    }

    public void erase(final byte key) {
        final V value = container.get(key);
        if (value != null) {
            final Command<V> command = new Command<>(Command.ERASE, key, value);
            changes.add(command);
            ++baselineCommandCount;
            container.remove(key);
            touch();
            onErase(key, value);
        }
        }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    public TByteObjectIterator<V> iterator() {
        return container.iterator();
    }

    public boolean containsKey(final byte key) {
        return container.containsKey(key);
    }

    public V get(final byte key) {
        return container.get(key);
    }

    public TByteObjectMap<V> getMap() {
        return container;
    }

    public void insert(final byte key, final V value) {
        if (containsKey(key))
            return;

        final Command<V> command = new Command<>(Command.ADD, key, value);
        container.put(key, value);
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

    public void set(final byte key, final V value) {
        if (!containsKey(key)) {
            //Inserting...
            final Command<V> command = new Command<>(Command.ADD, key, value);
            container.put(key, value);
            touch();
            onInsert(key, value);
            changes.add(command);
            ++baselineCommandCount;
        } else {
            //Setting...
            final Command<V> command = new Command<>(Command.SET, key, value);
            final V oldValue = container.get(key);
            container.put(key, value);
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
            value.writeToBuffer(buffer);
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
            final Command<V> command = new Command<>(buffer, valueCreator);
            assert command.cmd == Command.ADD : "Only add is valid in unpack";
            container.put(command.key, command.value);
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
            final byte key = buffer.get();
            final V value = valueCreator.apply(buffer);
        }

        for (; i < commandCount; ++i) {
            final Command<V> command = new Command<>(buffer, valueCreator);

            switch (command.cmd) {
                case Command.ADD:
                case Command.SET:
                    set(command.key, command.value);
                    break;
                case Command.ERASE:
                    erase(command.key);
                    break;
                default:
                    assert false : "UNKNOWN command";
                    break;
            }
        }

        //If we are behind, catch up.
        if (baselineCommandCount < targetBaselineCommandCount)
            baselineCommandCount = targetBaselineCommandCount;
    }

    private void onErase(final byte key, final V value) {
        //callback
    }

    private void onInsert(final byte key, final V value) {
        //callback
    }

    private void onSet(final byte key, final V oldValue, final V newValue) {
        //callback
    }

    public static class Command<V extends ByteBufferWritable> implements ByteBufferWritable {
        public static final byte ADD = 0x0;
        public static final byte ERASE = 0x1;
        public static final byte SET = 0x2;

        public final byte cmd;
        public final byte key;
        public final V value;

        public Command(int cmd, byte key, V value) {
            this.cmd = (byte) cmd;
            this.key = key;
            this.value = value;
        }

        public Command(final ByteBuffer buffer, final Function<ByteBuffer, V> valueCreator) {
            this.cmd = buffer.get();
            this.key = buffer.get();
            this.value = valueCreator.apply(buffer);
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            buffer.put(this.cmd);
            BufferUtil.put(buffer, key);
            value.writeToBuffer(buffer);
        }
    }
}
