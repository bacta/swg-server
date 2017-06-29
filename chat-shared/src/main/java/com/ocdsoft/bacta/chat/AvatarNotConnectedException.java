package com.ocdsoft.bacta.chat;

/**
 * Created by crush on 6/26/2017.
 */
public final class AvatarNotConnectedException extends RuntimeException {
    public AvatarNotConnectedException(final ChatAvatarId avatarId) {
        super(String.format("The avatar %s is not connected.", avatarId.getFullName()));
    }
}
