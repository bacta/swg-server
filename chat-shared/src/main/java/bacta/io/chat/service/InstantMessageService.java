package bacta.io.chat.service;

import bacta.io.chat.ChatAvatarId;
import bacta.io.chat.ChatResult;

/**
 * Created by crush on 6/25/2017.
 */
public interface InstantMessageService {
    ChatResult sendInstantMessage(ChatAvatarId from, ChatAvatarId to, String message, String outOfBand);
}
