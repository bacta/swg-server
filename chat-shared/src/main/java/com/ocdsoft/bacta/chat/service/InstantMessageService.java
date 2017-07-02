package com.ocdsoft.bacta.chat.service;

import com.ocdsoft.bacta.chat.ChatAvatarId;
import com.ocdsoft.bacta.chat.ChatResult;

/**
 * Created by crush on 6/25/2017.
 */
public interface InstantMessageService {
    ChatResult sendInstantMessage(ChatAvatarId from, ChatAvatarId to, String message, String outOfBand);
}
