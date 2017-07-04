package com.ocdsoft.bacta.chat.message;

import com.ocdsoft.bacta.chat.ChatAvatarId;
import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.network.message.game.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/28/2017.
 */
@Getter
@AllArgsConstructor
public final class ChatInstantMessageToCharacter extends GameNetworkMessage {
    private final ChatAvatarId characterName;
    private final String message; //unicode
    private final String outOfBand; //unicode
    private final int sequence;

    public ChatInstantMessageToCharacter(final ByteBuffer buffer) {
        this.characterName = new ChatAvatarId(buffer);
        this.message = BufferUtil.getUnicode(buffer);
        this.outOfBand = BufferUtil.getUnicode(buffer);
        this.sequence = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, characterName);
        BufferUtil.putUnicode(buffer, message);
        BufferUtil.putUnicode(buffer, outOfBand);
        BufferUtil.put(buffer, sequence);
    }
}
