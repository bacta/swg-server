package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.localization.StringId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class ClientRandomNameResponse extends GameNetworkMessage {
    private final String creatureTemplate;
    private final String name;
    private final StringId errorMessage;

    public ClientRandomNameResponse(ByteBuffer buffer) {
        this.creatureTemplate = BufferUtil.getAscii(buffer);
        this.name = BufferUtil.getUnicode(buffer);
        this.errorMessage = new StringId(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, creatureTemplate);
        BufferUtil.putUnicode(buffer, name);
        BufferUtil.put(buffer, errorMessage);
    }
}
