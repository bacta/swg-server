package com.ocdsoft.bacta.swg.protocol.serialize;

/**
 * Created by kyle on 4/21/2016.
 */
public class GameNetworkMessageTypeNotFoundException extends RuntimeException {

    public GameNetworkMessageTypeNotFoundException(int gameMessageType) {
        super("Unable to create message with key: 0x" + Integer.toHexString(gameMessageType));
    }
}
