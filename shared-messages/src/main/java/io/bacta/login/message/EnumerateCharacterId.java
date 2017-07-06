/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.message;


import io.bacta.buffer.BufferUtil;
import io.bacta.buffer.ByteBufferWritable;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
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
