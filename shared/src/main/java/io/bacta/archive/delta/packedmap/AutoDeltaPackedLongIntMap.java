package io.bacta.archive.delta.packedmap;

import io.bacta.archive.delta.map.AutoDeltaLongIntMap;

import java.nio.ByteBuffer;

/**
 * Created by crush on 8/14/2014.
 * <p>
 * An AutoDeltaPackedMap is an AutoDeltaMap that will be packed into
 * a single value for storage.  It functions as an AutoDeltaMap in
 * all respects except that packDeltas() will send the entire map
 * on the network.
 */
public class AutoDeltaPackedLongIntMap extends AutoDeltaLongIntMap {
    private static final String emptyPackedMap = "";

    /**
     * Packs a packed-string into a target byte buffer.
     *
     * @param target The target byte buffer.
     * @param buffer The string to pack into the buffer.
     */
    public static void packFromString(final ByteBuffer target, final String buffer) {
        final String[] entries = buffer.split(":");

        target.putInt(entries.length); //commandCount
        target.putInt(0); //baselineCommandCount

        if (entries.length <= 0)
            return;

        for (final String entry : entries) {
            final String[] entryTokens = entry.split(" ");

            final long key = Long.parseLong(entryTokens[0]);
            final int val = Integer.parseInt(entryTokens[1]);

            final Command command = new Command(Command.ADD, key, val);
            command.writeToBuffer(target);
        }
    }

    /**
     * Unpacks this source buffer into a packed string format.
     *
     * @param source The source buffer to unpack.
     * @return A string with the packed source buffer contents.
     */
    public static String unpackToString(final ByteBuffer source) {
        final int commandCount = source.getInt();
        final int baselineCommandCount = source.getInt();

        if (commandCount == 0) {
            return emptyPackedMap;
        } else {
            final StringBuilder sb = new StringBuilder(200);

            for (int i = 0; i < commandCount; ++i) {
                final Command command = new Command(source);
                sb.append(String.format("%d %d:", command.key, command.value));
            }

            return sb.toString();
        }
    }

    @Override
    public void packDelta(final ByteBuffer target) {
        super.pack(target);
    }

    @Override
    public void unpackDelta(final ByteBuffer source) {
        super.unpack(source);
        onChanged();
    }

    private void onChanged() {
        //callback
    }
}
