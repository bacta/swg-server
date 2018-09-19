package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/27/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class GcwGroupsRsp extends GameNetworkMessage {
    //Contribution % is expressed as a value out of 1,000,000,000
    //Map<gcwGroup, Map<categoryName, contributionPercent>>
    //Map<string, Map<string, <int>>

    public GcwGroupsRsp(final ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(0);
    }
}