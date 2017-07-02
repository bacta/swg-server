package com.ocdsoft.bacta.soe.serialize;

/**
 * Created by kyle on 4/21/2016.
 */
public class GameNetworkMessageTypeNotFoundException extends RuntimeException {

    public GameNetworkMessageTypeNotFoundException(int gameMessageType) {
        super("Unable to create com.ocdsoft.bacta.swg.login.message with key: 0x" + Integer.toHexString(gameMessageType));
    }
}
