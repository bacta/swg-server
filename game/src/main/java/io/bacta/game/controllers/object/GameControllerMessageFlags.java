package io.bacta.game.controllers.object;

/**
 * Created by kyle on 4/10/2016.
 */
public final class GameControllerMessageFlags {
    public static final int NONE = 0x0;
    public static final int SEND = 0x1;
    public static final int RELIABLE = 0x2;
    public static final int SOURCE_REMOTE_SERVER = 0x4;
    public static final int DEST_AUTH_CLIENT = 0x08;
    public static final int DEST_PROXY_CLIENT = 0x10;
    public static final int DEST_AUTH_SERVER = 0x20;
    public static final int DEST_PROXY_SERVER = 0x40;
    public static final int SOURCE_REMOTE_CLIENT = 0x100;
}
