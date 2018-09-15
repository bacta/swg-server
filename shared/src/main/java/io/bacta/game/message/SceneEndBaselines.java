package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Priority(0x2)
public final class SceneEndBaselines extends GameNetworkMessage {

	private final long objectId;

	public SceneEndBaselines(ByteBuffer buffer) {
        objectId = buffer.getLong();
	}

	@Override
	public void writeToBuffer(ByteBuffer buffer) {
        buffer.putLong(objectId);
	}
}
