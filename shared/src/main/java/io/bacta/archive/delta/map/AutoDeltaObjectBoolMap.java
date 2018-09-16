package io.bacta.archive.delta.map;

import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AutoDeltaObjectBoolMap<K extends ByteBufferWritable> extends AutoDeltaContainer {
    private transient final List<Command<K>> changes;
    private final TObjectByteMap<K> container;
    private transient int baselineCommandCount;
    private final Function<ByteBuffer, K> keyCreator;

    public AutoDeltaObjectBoolMap(Function<ByteBuffer, K> keyCreator) {
        this.changes = new ArrayList<>(5);
        this.container = new TObjectByteHashMap<>();
        this.baselineCommandCount = 0;
        this.keyCreator = keyCreator;
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

    public void erase(final K key) {
        final boolean value = container.get(key) != 0;
        final Command<K> command = new Command<>(Command.ERASE, key, value);
        changes.add(command);
        ++baselineCommandCount;
        container.remove(key);
        touch();
        onErase(key, value);
    }

    public boolean isEmpty() {
        return container.isEmpty();
    }

    public TObjectByteIterator<K> iterator() {
        return container.iterator();
    }

    public boolean containsKey(final K key) {
        return container.containsKey(key);
    }

    public boolean get(final K key) {
        return container.get(key) != 0;
    }

    public TObjectByteMap<K> getMap() {
        return container;
    }

    public void insert(final K key, final boolean value) {
        if (containsKey(key))
            return;

        final Command<K> command = new Command<>(Command.ADD, key, value);
        container.put(key, value ? (byte) 1 : (byte) 0);
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

    public void set(final K key, final boolean value) {
        if (!containsKey(key)) {
            //Inserting...
            final Command<K> command = new Command<>(Command.ADD, key, value);
            container.put(key, value ? (byte) 1 : (byte) 0);
            touch();
            onInsert(key, value);
            changes.add(command);
            ++baselineCommandCount;
        } else {
            //Setting...
            final Command<K> command = new Command<>(Command.SET, key, value);
            final boolean oldValue = container.get(key) != 0;
            container.put(key, value ? (byte) 1 : (byte) 0);
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
            key.writeToBuffer(buffer);
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
            final Command<K> command = new Command<>(buffer, keyCreator);
            assert command.cmd == Command.ADD : "Only add is valid in unpack";
            container.put(command.key, command.value ? (byte) 1 : (byte) 0);
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
            final K key = keyCreator.apply(buffer);
            final boolean value = BufferUtil.getBoolean(buffer);
        }

        for (; i < commandCount; ++i) {
            final Command<K> command = new Command<>(buffer, keyCreator);

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

    private void onErase(final K key, final boolean value) {
        //callback
    }

    private void onInsert(final K key, final boolean value) {
        //callback
    }

    private void onSet(final K key, final boolean oldValue, final boolean newValue) {
        //callback
    }

    public static class Command<K extends ByteBufferWritable> implements ByteBufferWritable {
        public static final byte ADD = 0x0;
        public static final byte ERASE = 0x1;
        public static final byte SET = 0x2;

        public final byte cmd;
        public final K key;
        public final boolean value;

        public Command(int cmd, K key, boolean value) {
            this.cmd = (byte) cmd;
            this.key = key;
            this.value = value;
        }

        public Command(final ByteBuffer buffer, final Function<ByteBuffer, K> keyCreator) {
            this.cmd = buffer.get();
            this.key = keyCreator.apply(buffer);
            this.value = BufferUtil.getBoolean(buffer);
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            buffer.put(this.cmd);
            key.writeToBuffer(buffer);
            BufferUtil.put(buffer, value);
        }
    }
}
