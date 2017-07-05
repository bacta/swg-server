package com.ocdsoft.bacta.soe.network.message.login;


import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

@Priority(0x2)
public final class EnumerateCharacterId extends GameNetworkMessage {
    private final Set<CharacterData> characterData;

    public EnumerateCharacterId(final Set<CharacterData> characterData) {
        this.characterData = characterData;
    }

    public EnumerateCharacterId(final ByteBuffer buffer) {
        final int characterCount = buffer.getInt();

        this.characterData = new TreeSet<>();

        for (int i = 0; i < characterCount; ++i) {
            final CharacterData data = new CharacterData(buffer);
            characterData.add(data);
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

        buffer.putInt(characterData.size());
        for (final CharacterData info : characterData) {
            info.writeToBuffer(buffer);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CharacterData implements ByteBufferWritable {
        private final String name;
        private final int objectTemplateId;
        private final long networkId;
        private final int clusterId;
        private final CharacterType characterType;

        public CharacterData(ByteBuffer buffer) {
            this.name = BufferUtil.getUnicode(buffer);
            this.objectTemplateId = buffer.getInt();
            this.networkId = buffer.getLong();
            this.clusterId = buffer.getInt();
            this.characterType = CharacterType.from(buffer.getInt());
        }

        @Override
        public void writeToBuffer(ByteBuffer buffer) {
            BufferUtil.putUnicode(buffer, name);
            BufferUtil.put(buffer, objectTemplateId);
            BufferUtil.put(buffer, networkId);
            BufferUtil.put(buffer, clusterId);
            BufferUtil.put(buffer, characterType.getValue());
        }
    }
}
