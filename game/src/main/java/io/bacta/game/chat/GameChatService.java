package io.bacta.game.chat;

import io.bacta.game.object.ServerObject;
import io.bacta.shared.localization.StringId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameChatService {
    public void sendSystemMessageSimple(ServerObject sender, StringId msg, ServerObject target) {
        LOGGER.warn("Not implemented.");
    }

    public void destroyAvatar(final String avatarName) {
        LOGGER.warn("Not implemented.");
    }
}
