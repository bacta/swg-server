package com.ocdsoft.bacta.chat;

/**
 * Created by crush on 6/29/2017.
 * <p>
 * Default implementation of the {@link ChatInterface} interface. Rather than contacting a remote server authority, it
 * keeps everything local.
 * <p>
 * This is the recommended implementation for local development and LAN servers with a single galaxy cluster.
 */
public final class LocalChatInterface implements ChatInterface {

    @Override
    public ChatResult loginAvatar(String characterName, long networkId) {
        return ChatResult.SUCCESS;
    }

    @Override
    public ChatResult logoutAvatar(ChatAvatarId avatarId) {
        return ChatResult.SUCCESS;
    }

    @Override
    public ChatResult destroyAvatar(ChatAvatarId avatarId) {
        return ChatResult.SUCCESS;
    }
}
