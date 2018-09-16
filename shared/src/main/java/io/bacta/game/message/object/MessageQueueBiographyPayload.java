package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.shared.biography.BiographyPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/1/2016.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.BIOGRAPHY_RETRIEVED)
public final class MessageQueueBiographyPayload implements MessageQueueData {
    private final BiographyPayload biographyPayload;

    public MessageQueueBiographyPayload(final ByteBuffer buffer) {
        biographyPayload = new BiographyPayload(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, biographyPayload);
    }


}
