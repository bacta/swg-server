package io.bacta.game.message.planet;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Sent when the planet map is opened on the client.
 * Expects a response of {@link GetMapLocationsResponseMessage}.
 */
@Getter
@Priority(0x5)
@RequiredArgsConstructor
public final class GetMapLocationsMessage extends GameNetworkMessage {
    private final String planetName;
    private final int staticCacheVersion;
    private final int dynamicCacheVersion;
    private final int persistentCacheVersion;

    public GetMapLocationsMessage(final ByteBuffer buffer) {
        planetName = BufferUtil.getAscii(buffer);
        staticCacheVersion = buffer.getInt();
        dynamicCacheVersion = buffer.getInt();
        persistentCacheVersion = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, planetName);
        BufferUtil.put(buffer, staticCacheVersion);
        BufferUtil.put(buffer, dynamicCacheVersion);
        BufferUtil.put(buffer, persistentCacheVersion);
    }
}
