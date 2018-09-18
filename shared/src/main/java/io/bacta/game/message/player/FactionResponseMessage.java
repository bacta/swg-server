package io.bacta.game.message.player;

import gnu.trove.list.TFloatList;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.List;

@Getter
@Priority(0x02)
@AllArgsConstructor
public final class FactionResponseMessage extends GameNetworkMessage {
    private final int rebelFaction;
    private final int imperialFaction;
    private final int criminalFaction;
    private final List<String> npcFactionNames;
    private final TFloatList npcFactionValues;

    public FactionResponseMessage(final ByteBuffer buffer) {
        rebelFaction = buffer.getInt();
        imperialFaction = buffer.getInt();
        criminalFaction = buffer.getInt();
        npcFactionNames = BufferUtil.getArrayList(buffer, BufferUtil::getAscii);
        npcFactionValues = BufferUtil.getTFloatArrayList(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, rebelFaction);
        BufferUtil.put(buffer, imperialFaction);
        BufferUtil.put(buffer, criminalFaction);
        BufferUtil.put(buffer, npcFactionNames, BufferUtil::putAscii);
        BufferUtil.put(buffer, npcFactionValues);
    }
}
