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
    /**
     * The unique galaxy id that the login server responded with in {@link io.bacta.login.message.GalaxyServerIdAck}.
     */
    private final int id;
    /**
     * The name of the galaxy. This may change, but it is not recommended to change it in this way.
     */
    private final String name;
    /**
     * The timezone in which the galaxy server is located. This value is used by the client to determine how close it
     * is to the client's timezone.
     */
    private final int timeZone;
    private final int maxCharacters;
    private final int maxCharactersPerAccount;
    private final int onlinePlayerLimit;
    private final int onlineTutorialLimit;
    private final int onlineFreeTrialLimit;
    private final boolean allowCharacterCreation;
    private final boolean allowFreeTrialCharacterCreation;

    //Status booleans

    /**
     * The galaxy is ready for clients to start connecting. If it is also in a locked state, then only privileged
     * clients can connect.
     */
    private final boolean acceptingConnections;
    /**
     * The galaxy will not be available to clients that are outside of the local network.
     */
    private final boolean secret;
    /**
     * The galaxy is accepting connections, but is in a locked state. Only privileged clients may connect.
     */
    private final boolean locked;

    public GalaxyServerStatus(final ByteBuffer buffer) {
        this.id = buffer.getInt();
        this.name = BufferUtil.getAscii(buffer);
        this.timeZone = buffer.getInt();
        this.maxCharacters = buffer.getInt();
        this.maxCharactersPerAccount = buffer.getInt();
        this.onlinePlayerLimit = buffer.getInt();
        this.onlineTutorialLimit = buffer.getInt();
        this.onlineFreeTrialLimit = buffer.getInt();
        this.allowCharacterCreation = BufferUtil.getBoolean(buffer);
        this.allowFreeTrialCharacterCreation = BufferUtil.getBoolean(buffer);
        this.acceptingConnections = BufferUtil.getBoolean(buffer);
        this.secret = BufferUtil.getBoolean(buffer);
        this.locked = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, id);
        BufferUtil.putAscii(buffer, name);
        BufferUtil.put(buffer, timeZone);
        BufferUtil.put(buffer, maxCharacters);
        BufferUtil.put(buffer, maxCharactersPerAccount);
        BufferUtil.put(buffer, onlinePlayerLimit);
        BufferUtil.put(buffer, onlineTutorialLimit);
        BufferUtil.put(buffer, onlineFreeTrialLimit);
        BufferUtil.put(buffer, allowCharacterCreation);
        BufferUtil.put(buffer, allowFreeTrialCharacterCreation);
        BufferUtil.put(buffer, acceptingConnections);
        BufferUtil.put(buffer, secret);
        BufferUtil.put(buffer, locked);
    }
}
