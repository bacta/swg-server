package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/27/2016.
 */
@Getter
@Priority(0x05)
@RequiredArgsConstructor
public final class GcwRegionsRsp extends GameNetworkMessage {
    //Map<string, Map<string, pair<pair<float, float>, float>>>
    //Map<RegionPlanet, Map<GcwScoreCategory->CategoryName, Pair<Pair<RegionCenterX, RegionCenterZ>, RegionRadius>>>>

    public GcwRegionsRsp(final ByteBuffer buffer) {
        final int keyCount = buffer.getInt();

        for (int i = 0; i < keyCount; ++i) {
            final String regionPlanet = BufferUtil.getAscii(buffer);

            final int subKeyCount = buffer.getInt();

            for (int j = 0; j < subKeyCount; ++j) {
                final String categoryName = BufferUtil.getAscii(buffer);

                final float regionCenterX = buffer.getFloat();
                final float regionCenterZ = buffer.getFloat();
                final float regionRadius = buffer.getFloat();
            }
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(0);
    }
}