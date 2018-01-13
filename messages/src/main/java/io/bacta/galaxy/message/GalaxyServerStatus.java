package io.bacta.galaxy.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * This message is typically sent to the login server to announce the current status of the galaxy server and set
 * various information about the galaxy like characters and limits on player counts.
 */
@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class GalaxyServerStatus extends GameNetworkMessage {
    private final String name;
    private final int timeZone;
    private final int maxCharacters;
    private final int maxCharactersPerAccount;
    private final int onlinePlayerLimit;
    private final int onlineTutorialLimit;
    private final int onlineFreeTrialLimit;
    private final boolean allowFreeTrialCharacterCreation;

    public GalaxyServerStatus(final ByteBuffer buffer) {
        this.name = BufferUtil.getAscii(buffer);
        this.timeZone = buffer.getInt();
        this.maxCharacters = buffer.getInt();
        this.maxCharactersPerAccount = buffer.getInt();
        this.onlinePlayerLimit = buffer.getInt();
        this.onlineTutorialLimit = buffer.getInt();
        this.onlineFreeTrialLimit = buffer.getInt();
        this.allowFreeTrialCharacterCreation = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, name);
        BufferUtil.put(buffer, timeZone);
        BufferUtil.put(buffer, maxCharacters);
        BufferUtil.put(buffer, maxCharactersPerAccount);
        BufferUtil.put(buffer, onlinePlayerLimit);
        BufferUtil.put(buffer, onlineTutorialLimit);
        BufferUtil.put(buffer, onlineFreeTrialLimit);
        BufferUtil.put(buffer, allowFreeTrialCharacterCreation);
    }
}
