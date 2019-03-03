package io.bacta.shared.object;

/**
 * Created by crush on 5/30/2016.
 */
public enum ConversationStarter {
    PLAYER((byte) 0),
    NPC((byte) 1);

    public final byte value;

    ConversationStarter(final byte value) {
        this.value = value;
    }

    public static ConversationStarter from(final byte value) {
        return value == 1 ? NPC : PLAYER;
    }
}
