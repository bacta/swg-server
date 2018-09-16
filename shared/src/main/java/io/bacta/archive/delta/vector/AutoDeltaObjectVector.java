package io.bacta.archive.delta.vector;


import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

;

public class AutoDeltaObjectVector<T extends ByteBufferWritable> extends AutoDeltaContainer {
    private transient final List<Command> commands;
    private final List<T> v;
    private transient int baselineCommandCount;
    private transient final Function<ByteBuffer, T> valueCreator;

    public AutoDeltaObjectVector(Function<ByteBuffer, T> valueCreator) {
        this.commands = new ArrayList<>(5);
        this.v = new ArrayList<T>();
        this.baselineCommandCount = 0;
        this.valueCreator = valueCreator;
    }

    public AutoDeltaObjectVector(final int initialSize, Function<ByteBuffer, T> valueCreator) {
        this.commands = new ArrayList<>(5);
        this.v = new ArrayList<T>(initialSize);
        this.baselineCommandCount = 0;
        this.valueCreator = valueCreator;
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
            final T oldValue = v.get(element);
            v.remove(element);
            touch();
            onErase(element, oldValue);
            onChanged();
        }
    }

    public void add(final T value) {
        insert(v.size(), value);
    }

    public void insert(final int index, final T value) {
        final Command command = new Command(Command.INSERT, (short) index, value);
        commands.add(command);
        ++baselineCommandCount;
        v.add(index, value);
        touch();
        onInsert(index, value);
        onChanged();
    }

    public void set(final int element, final T value) {
        if (element < v.size() && v.get(element) == (value))
            return;

        final Command command = new Command(Command.SET, (short) element, value);
        commands.add(command);
        ++baselineCommandCount;

        //Resize v to element + 1 - TIntObjectList has no fill method.
        if (element >= v.size()) {
            for (int i = 0, size = v.size(); i < element + 1 - size; ++i)
                v.add(null);
        }

        final T oldValue = v.get(element);
        v.set(element, value);
        touch();
        onSet(element, oldValue, value);
        onChanged();
    }

    public void set(final List<T> value) {
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

    public int find(final T value) {
        for (int i = 0, size = v.size(); i < size; ++i) {
            if ((v.get(i)) == value)
                return i;
        }

        return -1;
    }

    public List<T> get() {
        return v;
    }

    public T get(int index) {
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
            final T value = v.get(i);
            value.writeToBuffer(buffer);
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
                    final T value = command.value; //value will never be null in this case - ignore warning.
                    value.writeToBuffer(buffer);
                    break;
                }
                case Command.SETALL: {
                    buffer.putShort(command.index);
                    for (int j = 0; j < command.index; ++j) {
                        ++i;
                        final Command nextCommand = commands.get(i);
                        final T value = nextCommand.value;//value will never be null in this case - ignore warning.
                        value.writeToBuffer(buffer);
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
            final T value = valueCreator.apply(buffer);
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
                    valueCreator.apply(buffer);
                }
            } else if (cmd != Command.ERASE && cmd != Command.CLEAR)
                valueCreator.apply(buffer);
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
                    final T value = valueCreator.apply(buffer);
                    insert(index, value);
                    break;
                }
                case Command.SET: {
                    final short index = buffer.getShort();
                    final T value = valueCreator.apply(buffer);
                    set(index, value);
                    break;
                }
                case Command.SETALL: {
                    final short index = buffer.getShort();
                    final List<T> tempList = new ArrayList<T>(index);

                    for (int j = 0; j < index; ++j) {
                        ++i;
                        final T value = valueCreator.apply(buffer);
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

    private void onErase(final int index, final T oldValue) {
    }

    private void onInsert(final int index, final T value) {
    }

    private void onSet(final int index, final T oldValue, final T newValue) {
    }

    public final class Command {
        public static final byte ERASE = 0x00;
        public static final byte INSERT = 0x01;
        public static final byte SET = 0x02;
        public static final byte SETALL = 0x03;
        public static final byte CLEAR = 0x04;

        public final byte cmd;
        public final short index;
        public final T value;

        public Command(final byte cmd) {
            this.cmd = cmd;
            this.index = 0;
            this.value = null;
        }

        public Command(final byte cmd, final short index) {
            this.cmd = cmd;
            this.index = index;
            this.value = null;
        }

        public Command(final byte cmd, final short index, final T value) {
            this.cmd = cmd;
            this.index = index;
            this.value = value;
        }

        public Command(final ByteBuffer buffer) {
            this.cmd = buffer.get();
            this.index = buffer.getShort();
            this.value = valueCreator.apply(buffer);
        }
    }
}
