package io.bacta.game.controllers.command;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.object.ServerObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@QueuesCommand("getattributesbatch")
public class GetAttributesBatchCommandController implements CommandQueueController {
    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, String params) {
        LOGGER.warn("Not yet implemented.");
    }
}
