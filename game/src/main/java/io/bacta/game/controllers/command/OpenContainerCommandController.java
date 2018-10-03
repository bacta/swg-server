package io.bacta.game.controllers.command;

import io.bacta.game.command.CommandQueueParameters;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.object.ServerObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@QueuesCommand("opencontainer")
public class OpenContainerCommandController implements CommandQueueController {
    @Inject
    public OpenContainerCommandController() {
    }

    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, CommandQueueParameters params) {
        LOGGER.warn("Not implemented");
    }
}