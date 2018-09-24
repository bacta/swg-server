package io.bacta.archive.delta.packedmap;

import io.bacta.archive.delta.map.AutoDeltaIntObjectMap;
import io.bacta.swg.object.Buff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by crush on 8/14/2014.
 * <p>
 * An AutoDeltaPackedMap is an AutoDeltaMap that will be packed into
 * a single value for storage.  It functions as an AutoDeltaMap in
 * all respects except that packDeltas() will send the entire map
 * on the network.
 */
public class AutoDeltaPackedBuffMap extends AutoDeltaIntObjectMap<Buff.PackedBuff> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeltaPackedBuffMap.class);
    private static final String emptyPackedMap = "";

    public AutoDeltaPackedBuffMap() {
        super(Buff.PackedBuff::new);
    }

    /**
     * Packs a packed-string into a target byte buffer.
     * <p>
     * The packed string should have a version number, then all the data of the map.
     * The format to be used would be
     * [(versionNumber)v](packedData)
     * <p>
     * packedData versions
     * version 2: [crc endtime value duration caster stackCount]
     *
     * @param target The target byte buffer.
     * @param buffer The string to pack into the buffer.
     */
    public static void packFromString(final ByteBuffer target, final String buffer) {
        //If we don't find a 'v', then its an oldschool buff, which doesn't have string characters. So we won't get
        //a false positive here.
        final int versionIndex = buffer.indexOf('v');
        final int version = versionIndex == -1 ? 0 : Integer.parseInt(buffer.substring(0, versionIndex));

        final String[] entries = versionIndex != -1
                ? buffer.substring(versionIndex + 1).split(":")
                : buffer.split(":");

        target.putInt(entries.length); //commandCount
        target.putInt(0); //baselineCommandCount

        if (entries.length <= 0)
            return;

        if (version == 0) {
            throw new UnsupportedOperationException("old school buffs not supported yet.");
        } else if (version == 1) {
            throw new UnsupportedOperationException("version 1 buffs not supported yet.");
        } else if (version == 2) {
            for (final String entry : entries) {
                final String[] entryTokens = entry.split(" ");

                final int crc = Integer.parseInt(entryTokens[0]);
                final Buff.PackedBuff value = Buff.PackedBuff.unpackFromStringTokens(entryTokens);

                final Command<Buff.PackedBuff> command = new Command<>(Command.ADD, crc, value);
                command.writeToBuffer(target);
            }
        } else {
            LOGGER.error("UNKNOWN version {} when packing PackedBuff string to buffer.", version);
        }
    }

    /**
     * Unpacks this source buffer into a packed string format.
     * <p>
     * The format appears to be [(versionNumber)v](packedData)
     * <p>
     * CURRENT iteration for NGE is version 2. Version 0 is called "old school" and doesn't have the version prefix.
     * We shouldn't encounter these because none are being loaded from an old database. However, we might want to use
     * them if we are talking to the Pre-CU client.
     * <p>
     * version 2: crc endtime value duration caster stackCount
     * verison 1: crc endtime value duration
     * version 0: crc packed64 (buff info packed into a 64 bit map)
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
            final StringBuilder sb = new StringBuilder();

            //We are always going to write the v2 packed string type from now on.
            for (int i = 0; i < commandCount; ++i) {
                final Command<Buff.PackedBuff> command = new Command<>(source, Buff.PackedBuff::new);
                sb.append(String.format("2v%d %s:", command.key, command.value.packToString()));
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
