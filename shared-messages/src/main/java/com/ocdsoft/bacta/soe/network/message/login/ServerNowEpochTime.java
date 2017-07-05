package com.ocdsoft.bacta.soe.network.message.login;

import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 7/3/2017.
 */
@Getter
@Priority(0x3)
@AllArgsConstructor
public class ServerNowEpochTime extends GameNetworkMessage {
    private final int epochSeconds;

    public ServerNowEpochTime(ByteBuffer buffer) {
        this.epochSeconds = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(epochSeconds);
    }
}
