package com.ocdsoft.bacta.chat.service;

import com.ocdsoft.bacta.chat.ChatAvatarId;
import com.ocdsoft.bacta.chat.ChatError;

/**
 * Created by crush on 6/25/2017.
 */
public interface InstantMessageService {
    ChatError sendInstantMessage(ChatAvatarId from, ChatAvatarId to, String message, String outOfBand);
}
