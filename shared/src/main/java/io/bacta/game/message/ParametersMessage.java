package io.bacta.game.message;

import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/19/2016.
 *
 * Configures parameters on the client. Currently only configures how often the client should check for weather updates.
 */
@AllArgsConstructor
public class ParametersMessage extends GameNetworkMessage {

    private final int weatherUpdateInterval; //in seconds

    public ParametersMessage(final ByteBuffer buffer) {
        weatherUpdateInterval = buffer.getInt();
    }


    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(weatherUpdateInterval);
    }
}
