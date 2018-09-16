package io.bacta.archive.delta.vector;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.BufferUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

;

public class AutoDeltaBoolVector extends AutoDeltaContainer {
    private transient final List<Command> commands;
    private final TByteList v;
    private transient int baselineCommandCount;

    public AutoDeltaBoolVector() {
        this.commands = new ArrayList<>(5);
        this.v = new TByteArrayList();
        this.baselineCommandCount = 0;
    }

    public AutoDeltaBoolVector(final int initialSize) {
        this.commands = new ArrayList<>(5);
        this.v = new TByteArrayList(initialSize);
        this.baselineCommandCount = 0;
    }

    public void clear() {
        if (!isEmpty()) {
            final Command command = new Command(Command.CLEAR);
            commands.add(command);
            ++baselineCommandCount;
            for (int i = 0, size = v.size(); i < size; ++i)
                onErase(i, v.get(i) != 0);
            v.clear();
            touch();
            onChanged();
        }
    }

    public void erase(final int element) {
        if (element < size()) {
            final Command command = new Command(Command.ERASE, (short) element);
            commands.add(command);
            ++baselineCommandCount;
            final boolean oldValue = v.get(element) != 0;
            v.removeAt(element);
            touch();
            onErase(element, oldValue);
            onChanged();
        }
    }

    public void add(final boolean value) {
        insert(v.size(), value);
    }

    public void insert(final int index, final boolean value) {
        final Command command = new Command(Command.INSERT, (short) index, value);
        commands.add(command);
        ++baselineCommandCount;
        v.insert(index, value ? (byte) 1 : (byte) 0);
        touch();
        onInsert(index, value);
        onChanged();
    }

    public void set(final int element, final boolean value) {
        if (element < v.size() && v.get(element) == (value ? (byte) 1 : (byte) 0))
            return;

        final Command command = new Command(Command.SET, (short) element, value);
        commands.add(command);
        ++baselineCommandCount;

        //Resize v to element + 1
        if (element >= v.size())
            v.fill(v.size(), element + 1, (byte) 0);

        final boolean oldValue = v.get(element) != 0;
        v.set(element, value ? (byte) 1 : (byte) 0);
        touch();
        onSet(element, oldValue, value);
        onChanged();
    }

    public void set(final TByteList value) {
        final Command command = new Command(Command.SETALL, (short) value.size());
        commands.add(command);
        ++baselineCommandCount;

        //Is this the best way to do this?
        v.clear();
        v.addAll(value);

        for (int i = 0, size = value.size(); i < size; ++i) {
            final Command subCommand = new Command(Command.SET, (short) i, value.get(i) != 0);
            commands.add(subCommand);
            ++baselineCommandCount;
        }

        touch();
        onChanged();
    }

    public int find(final boolean value) {
        for (int i = 0, size = v.size(); i < size; ++i) {
            if ((v.get(i) != 0) == value)
                return i;
        }

        return -1;
    }

    public TByteList get() {
        return v;
    }

    public boolean get(int index) {
        return v.get(index) != 0;
    }

    public boolean isEmpty() {
        return v.isEmpty();
    }

    @Override
    public int size() {
        return v.size();
    }

    @Override
    public boolean isDirty() {
        return commands.size() > 0;
    }

    @Override
    public void clearDelta() {
        commands.clear();
    }

    @Override
    public void pack(final ByteBuffer buffer) {
        buffer.putInt(v.size());
        buffer.putInt(baselineCommandCount);

        for (int i = 0, size = v.size(); i < size; ++i) {
            final boolean value = v.get(i) != 0;
            BufferUtil.put(buffer, value);
        }
    }

    @Override
    public void packDelta(final ByteBuffer buffer) {
        buffer.putInt(commands.size());
        buffer.putInt(baselineCommandCount);

        for (int i = 0, size = commands.size(); i < size; ++i) {
            final Command command = commands.get(i);
            buffer.put(command.cmd);

            switch (command.cmd) {
                case Command.ERASE:
                    buffer.putShort(command.index);
                    break;
                case Command.INSERT:
                case Command.SET: {
                    buffer.putShort(command.index);
                    final boolean value = command.value; //value will never be null in this case - ignore warning.
                    BufferUtil.put(buffer, value);
                    break;
                }
                case Command.SETALL: {
                    buffer.putShort(command.index);
                    for (int j = 0; j < command.index; ++j) {
                        ++i;
                        final Command nextCommand = commands.get(i);
                        final boolean value = nextCommand.value;//value will never be null in this case - ignore warning.
                        BufferUtil.put(buffer, value);
                    }
                    break;
                }
                case Command.CLEAR:
                    break;
            }
        }

        clearDelta();
    }

    @Override
    public void unpack(final ByteBuffer buffer) {
        v.clear();
        clearDelta();

        final int commandCount = buffer.getInt();
        baselineCommandCount = buffer.getInt();

        for (int i = 0; i < commandCount; ++i) {
            final boolean value = BufferUtil.getBoolean(buffer);
            v.add(value ? (byte) 1 : (byte) 0);
        }

        onChanged();
    }

    @Override
    public void unpackDelta(final ByteBuffer buffer) {
        int skipCount;
        final int commandCount = buffer.getInt();
        final int targetBaselineCommandCount = buffer.getInt();

        skipCount = commandCount + baselineCommandCount - targetBaselineCommandCount;

        if (skipCount > commandCount)
            skipCount = commandCount;

        int i;
        for (i = 0; i < skipCount; ++i) {
            final byte cmd = buffer.get();
            short index = 0;

            if (cmd != Command.CLEAR)
                index = buffer.getShort();

            if (cmd == Command.SETALL) {
                for (int j = 0; j < index; ++j) {
                    ++i;
                    BufferUtil.getBoolean(buffer);
                }
            } else if (cmd != Command.ERASE && cmd != Command.CLEAR)
                BufferUtil.getBoolean(buffer);
        }

        for (; i < commandCount; ++i) {
            final byte cmd = buffer.get();

            switch (cmd) {
                case Command.ERASE: {
                    final short index = buffer.getShort();
                    erase(index);
                    break;
                }
                case Command.INSERT: {
                    final short index = buffer.getShort();
                    final boolean value = BufferUtil.getBoolean(buffer);
                    insert(index, value);
                    break;
                }
                case Command.SET: {
                    final short index = buffer.getShort();
                    final boolean value = BufferUtil.getBoolean(buffer);
                    set(index, value);
                    break;
                }
                case Command.SETALL: {
                    final short index = buffer.getShort();
                    final TByteList tempList = new TByteArrayList(index);

                    for (int j = 0; j < index; ++j) {
                        ++i;
                        final boolean value = BufferUtil.getBoolean(buffer);
                        tempList.add(value ? (byte) 1 : (byte) 0);
                    }
                    set(tempList);
                    break;
                }
                case Command.CLEAR:
                    clear();
                    break;
            }
        }
    }

    private void onChanged() {
    }

    private void onErase(final int index, final boolean oldValue) {
    }

    private void onInsert(final int index, final boolean value) {
    }

    private void onSet(final int index, final boolean oldValue, final boolean newValue) {
    }

    public final class Command {
        public static final byte ERASE = 0x00;
        public static final byte INSERT = 0x01;
        public static final byte SET = 0x02;
        public static final byte SETALL = 0x03;
        public static final byte CLEAR = 0x04;

        public final byte cmd;
        public final short index;
        public final boolean value;

        public Command(final byte cmd) {
            this.cmd = cmd;
            this.index = 0;
            this.value = false;
        }

        public Command(final byte cmd, final short index) {
            this.cmd = cmd;
            this.index = index;
            this.value = false;
        }

        public Command(final byte cmd, final short index, final boolean value) {
            this.cmd = cmd;
            this.index = index;
            this.value = value;
        }

        public Command(final ByteBuffer buffer) {
            this.cmd = buffer.get();
            this.index = buffer.getShort();
            this.value = BufferUtil.getBoolean(buffer);
        }
    }
}
