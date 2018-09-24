package io.bacta.archive.delta.packedmap;

import io.bacta.archive.delta.map.AutoDeltaIntObjectMap;
import io.bacta.swg.object.PlayerQuestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by crush on 8/14/2014.
 * <p>
 * An AutoDeltaPackedMap is an AutoDeltaMap that will be packed into
 * a single value for storage.  It functions as an AutoDeltaMap in
 * all respects except that packDeltas() will send the entire map
 * on the network.
 */
public class AutoDeltaPackedPlayerQuestDataMap extends AutoDeltaIntObjectMap<PlayerQuestData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeltaPackedPlayerQuestDataMap.class);
    private static final String emptyPackedMap = "";

    public AutoDeltaPackedPlayerQuestDataMap() {
        super(PlayerQuestData::new);
    }

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

            final int key = Integer.parseInt(entryTokens[0]);

            //active quests
            if (entryTokens.length == 4) {
                target.put(Command.ADD);
                target.putInt(key);

                //in-progress quest.
                final PlayerQuestData data = new PlayerQuestData(
                        Long.parseLong(entryTokens[3]), //questGiver
                        Short.parseShort(entryTokens[1]), //activeTasks
                        Short.parseShort(entryTokens[2]), //completedTasks
                        false); //hasReceivedRewards
                data.writeToBuffer(target);
                //old-style completed quests which don't store extra flags.
            } else if (entryTokens.length == 1) {
                //completed quest
                target.put(Command.ADD);
                target.putInt(key);

                final PlayerQuestData data = new PlayerQuestData(true, true);
                data.writeToBuffer(target);
                //completed quests with store flags
            } else if (entryTokens.length == 2) {
                //completed quest
                target.put(Command.ADD);
                target.putInt(key);

                final short flags = Short.parseShort(entryTokens[1]);
                final boolean hasReceivedReward = (flags != 0);
                final PlayerQuestData data = new PlayerQuestData(true, hasReceivedReward);
                data.writeToBuffer(target);
            } else {
                LOGGER.error("Could not parse packed quest data {}", entry);
                throw new IllegalArgumentException("Could not parse packed quest data");
            }
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
            final StringBuilder sb = new StringBuilder();
            final List<QuestDataToSort> sortedList = new ArrayList<>(commandCount);

            for (int i = 0; i < commandCount; ++i) {
                final Command<PlayerQuestData> command = new Command<>(source, PlayerQuestData::new);
                sortedList.add(new QuestDataToSort(command.key, command.value));
            }

            Collections.sort(sortedList, (qd1, qd2) -> Integer.compare(qd1.questData.getRelativeAgeIndex(), qd2.questData.getRelativeAgeIndex()));

            for (int i = 0, size = sortedList.size(); i < size; ++i) {
                final QuestDataToSort qd = sortedList.get(i);
                final int key = qd.key;
                final PlayerQuestData data = qd.questData;

                if (data.isCompleted()) {
                    sb.append(String.format("%d %d:", key, data.hasReceivedReward() ? 1 : 0));
                } else {
                    sb.append(String.format("%d %d %d %s:", key, data.getActiveTasks(), data.getCompletedTasks(), Long.toString(data.getQuestGiver())));
                }
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

    private static class QuestDataToSort {
        private final int key;
        private final PlayerQuestData questData;

        public QuestDataToSort(final int key, final PlayerQuestData questData) {
            this.key = key;
            this.questData = questData;
        }
    }
}
