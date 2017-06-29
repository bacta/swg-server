package com.ocdsoft.bacta.chat.service;

import com.ocdsoft.bacta.chat.ChatAvatarId;

/**
 * Created by crush on 6/25/2017.
 */
public interface PersistentMessageService {
    void sendPersistentMessage(ChatAvatarId from, ChatAvatarId to, String subject, String body, String outOfBand);
    void deletePersistentMessage(long targetNetworkId, int messageId);
    void deleteAllPersistentMessages(long sourceNetworkId, long targetNetworkId);
}
