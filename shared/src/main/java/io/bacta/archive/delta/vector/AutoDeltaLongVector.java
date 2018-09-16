package io.bacta.archive.delta.vector;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.BufferUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

;

public class AutoDeltaLongVector extends AutoDeltaContainer {
    private transient final List<Command> commands;
    private final TLongList v;
    private transient int baselineCommandCount;

    public AutoDeltaLongVector() {
        this.commands = new ArrayList<>(5);
        this.v = new TLongArrayList();
        this.baselineCommandCount = 0;
    }

    public AutoDeltaLongVector(final int initialSize) {
        this.commands = new ArrayList<>(5);
        this.v = new TLongArrayList(initialSize);
        this.baselineCommandCount = 0;
    }

    public void clear() {
        if (!isEmpty()) {
            final Command command = new Command(Command.CLEAR);
            commands.add(command);
            ++baselineCommandCount;
            for (int i = 0, size = v.size(); i < size; ++i)
                onErase(i, v.get(i));
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
            final long oldValue = v.get(element);
            v.removeAt(element);
            touch();
            onErase(element, oldValue);
            onChanged();
        }
    }

    public void add(final long value) {
        insert(v.size(), value);
    }

    public void insert(final int index, final long value) {
        final Command command = new Command(Command.INSERT, (short) index, value);
        commands.add(command);
        ++baselineCommandCount;
        v.insert(index, value);
        touch();
        onInsert(index, value);
        onChanged();
    }

    public void set(final int element, final long value) {
        if (element < v.size() && v.get(element) == (value))
            return;

        final Command command = new Command(Command.SET, (short) element, value);
        commands.add(command);
        ++baselineCommandCount;

        //Resize v to element + 1
        if (element >= v.size())
            v.fill(v.size(), element + 1, 0);

        final long oldValue = v.get(element);
        v.set(element, value);
        touch();
        onSet(element, oldValue, value);
        onChanged();
    }

    public void set(final TLongList value) {
        final Command command = new Command(Command.SETALL, (short) value.size());
        commands.add(command);
        ++baselineCommandCount;

        //Is this the best way to do this?
        v.clear();
        v.addAll(value);

        for (int i = 0, size = value.size(); i < size; ++i) {
            final Command subCommand = new Command(Command.SET, (short) i, value.get(i));
            commands.add(subCommand);
            ++baselineCommandCount;
        }

        touch();
        onChanged();
    }

    public int find(final long value) {
        for (int i = 0, size = v.size(); i < size; ++i) {
            if ((v.get(i)) == value)
                return i;
        }

        return -1;
    }

    public TLongList get() {
        return v;
    }

    public long get(int index) {
        return v.get(index);
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
            final long value = v.get(i);
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
                    final long value = command.value; //value will never be null in this case - ignore warning.
                    BufferUtil.put(buffer, value);
                    break;
                }
                case Command.SETALL: {
                    buffer.putShort(command.index);
                    for (int j = 0; j < command.index; ++j) {
                        ++i;
                        final Command nextCommand = commands.get(i);
                        final long value = nextCommand.value;//value will never be null in this case - ignore warning.
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
            final long value = buffer.getLong();
            v.add(value);
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
                    buffer.getLong();
                }
            } else if (cmd != Command.ERASE && cmd != Command.CLEAR)
                buffer.getLong();
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
                    final long value = buffer.getLong();
                    insert(index, value);
                    break;
                }
                case Command.SET: {
                    final short index = buffer.getShort();
                    final long value = buffer.getLong();
                    set(index, value);
                    break;
                }
                case Command.SETALL: {
                    final short index = buffer.getShort();
                    final TLongList tempList = new TLongArrayList(index);

                    for (int j = 0; j < index; ++j) {
                        ++i;
                        final long value = buffer.getLong();
                        tempList.add(value);
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

    private void onErase(final int index, final long oldValue) {
    }

    private void onInsert(final int index, final long value) {
    }

    private void onSet(final int index, final long oldValue, final long newValue) {
    }

    public final class Command {
        public static final byte ERASE = 0x00;
        public static final byte INSERT = 0x01;
        public static final byte SET = 0x02;
        public static final byte SETALL = 0x03;
        public static final byte CLEAR = 0x04;

        public final byte cmd;
        public final short index;
        public final long value;

        public Command(final byte cmd) {
            this.cmd = cmd;
            this.index = 0;
            this.value = 0;
        }

        public Command(final byte cmd, final short index) {
            this.cmd = cmd;
            this.index = index;
            this.value = 0;
        }

        public Command(final byte cmd, final short index, final long value) {
            this.cmd = cmd;
            this.index = index;
            this.value = value;
        }

        public Command(final ByteBuffer buffer) {
            this.cmd = buffer.get();
            this.index = buffer.getShort();
            this.value = buffer.getLong();
        }
    }
}
