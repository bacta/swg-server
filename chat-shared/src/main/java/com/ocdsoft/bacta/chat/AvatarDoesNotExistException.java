package com.ocdsoft.bacta.chat;

/**
 * Created by crush on 6/26/2017.
 */
public final class AvatarDoesNotExistException extends RuntimeException {
    public AvatarDoesNotExistException(long networkId) {
        super(String.format("Avatar with network id %d does not exist.", networkId));
    }
}
