package io.bacta.shared.object;


import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.shared.util.NetworkId;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/8/2016.
 */
public class PlayerQuestData implements ByteBufferWritable {
    @Getter
    private long questGiver;
    @Getter
    private boolean completed;
    @Getter
    private short activeTasks;
    @Getter
    private short completedTasks;
    @Getter
    private int relativeAgeIndex;
    private boolean hasReceivedReward;

    public PlayerQuestData() {
        questGiver = NetworkId.INVALID;
    }

    public PlayerQuestData(final long questGiver) {
        this.questGiver = questGiver;
    }

    public PlayerQuestData(final long questGiver, final short activeTasks, final short completedTasks, final boolean hasReceivedReward) {
        this.questGiver = questGiver;
        this.activeTasks = activeTasks;
        this.completedTasks = completedTasks;
        this.hasReceivedReward = hasReceivedReward;
    }

    public PlayerQuestData(final boolean completed, final boolean receivedReward) {
        this.completed = completed;
        this.hasReceivedReward = receivedReward;
    }

    public PlayerQuestData(final ByteBuffer buffer) {
        this.questGiver = buffer.getLong();
        this.activeTasks = buffer.getShort();
        this.completedTasks = buffer.getShort();
        this.completed = BufferUtil.getBoolean(buffer);
        this.relativeAgeIndex = buffer.getInt();
        this.hasReceivedReward = BufferUtil.getBoolean(buffer);
    }

    public boolean hasReceivedReward() {
        return hasReceivedReward;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putLong(questGiver);
        buffer.putShort(activeTasks);
        buffer.putShort(completedTasks);
        BufferUtil.put(buffer, completed);
        buffer.putInt(relativeAgeIndex);
        BufferUtil.put(buffer, hasReceivedReward);
    }
}
