package io.bacta.archive.delta.set;

import io.bacta.archive.delta.AutoDeltaContainer;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AutoDeltaStringSet extends AutoDeltaContainer {
    private transient final List<Command> commands;
    private final Set<String> set;
    private transient int baselineCommandCount;

    public AutoDeltaStringSet() {
        this.commands = new ArrayList<>(5);
        this.set = new TreeSet<>();
        this.baselineCommandCount = 0;
    }

    public Set<String> get() {
        return set;
    }

    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean contains(final String value) {
        return set.contains(value);
    }

    public void clear() {
        if (!isEmpty()) {
            final Command command = new Command(Command.CLEAR, null);
            commands.add(command);
            ++baselineCommandCount;
            set.forEach(value -> {
                erase(value);
            });
            set.clear();
            touch();
            onChanged();
        }
    }

    public void erase(final String value) {
        if (set.contains(value)) {
            final Command command = new Command(Command.ERASE, value);
            commands.add(command);
            ++baselineCommandCount;
            set.remove(value);
            touch();
            onErase(value);
            onChanged();
        }
    }

    public void insert(final String value) {
        if (set.add(value)) {
            final Command command = new Command(Command.INSERT, value);
            commands.add(command);
            ++baselineCommandCount;
            touch();
            onInsert(value);
            onChanged();
        }
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
    public void pack(ByteBuffer buffer) {
        buffer.putInt(set.size());
        buffer.putInt(baselineCommandCount);

        set.forEach(value -> BufferUtil.put(buffer, value));
    }

    @Override
    public void packDelta(ByteBuffer buffer) {
        buffer.putInt(commands.size());
        buffer.putInt(baselineCommandCount);

        for (final Command command : commands)
            command.writeToBuffer(buffer);

        clearDelta();
    }

    @Override
    public void unpackDelta(ByteBuffer buffer) {
        int skipCount;
        final int commandCount = buffer.getInt();
        final int targetBaselineCommandCount = buffer.getInt();

        // if (commandCount+baselineCommandCount) < targetBaselineCommandCount, it
        // means that we have missed some changes and are behind; when this happens,
        // catch up by applying all the deltas that came in, and set
        // baselineCommandCount to targetBaselineCommandCount
        if ((commandCount + baselineCommandCount) > targetBaselineCommandCount)
            skipCount = commandCount + baselineCommandCount - targetBaselineCommandCount;
        else
            skipCount = 0;

        if (skipCount > commandCount)
            skipCount = commandCount;

        int i;
        for (i = 0; i < skipCount; ++i) {
            final byte cmd = buffer.get();

            if (cmd != Command.CLEAR)
                BufferUtil.getAscii(buffer);
        }

        for (; i < commandCount; ++i) {
            final Command command = new Command(buffer);

            switch (command.cmd) {
                case Command.ERASE:
                    erase(command.value);
                    break;
                case Command.INSERT:
                    insert(command.value);
                    break;
                case Command.CLEAR:
                    clear();
                    break;
            }
        }

        // if we are behind, catch up
        if (baselineCommandCount < targetBaselineCommandCount)
            baselineCommandCount = targetBaselineCommandCount;
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        set.clear();
        clearDelta();

        final int commandCount = buffer.getInt();
        baselineCommandCount = buffer.getInt();

        for (int i = 0; i < commandCount; ++i) {
            final String value = BufferUtil.getAscii(buffer);
            set.add(value);
        }

        onChanged();
    }

    private void onChanged() {
        //implement on changed callback
    }

    private void onErase(final String value) {
        //implement on erase callback
    }

    private void onInsert(final String value) {
        //implement on insert callback
    }


    public final class Command implements ByteBufferWritable {
        private static final byte ERASE = 0x00;
        private static final byte INSERT = 0x01;
        private static final byte CLEAR = 0x02;

        public final byte cmd;
        public final String value;

        public Command(final byte cmd, final String value) {
            this.cmd = cmd;
            this.value = value;
        }

        public Command(final ByteBuffer buffer) {
            cmd = buffer.get();

            switch (cmd) {
                case ERASE:
                case INSERT:
                    value = BufferUtil.getAscii(buffer);
                    break;
                case CLEAR:
                    value = null;
                    break;
                default:
                    value = null;
                    assert false : "Unknown command type.";
            }
        }

        @Override
        public void writeToBuffer(ByteBuffer buffer) {
            buffer.put(cmd);

            switch (cmd) {
                case ERASE:
                case INSERT:
                    BufferUtil.put(buffer, value);
                    break;
                case CLEAR:
                    break;
                default:
                    assert false : "Unknown command type.";
            }
        }
    }
}
