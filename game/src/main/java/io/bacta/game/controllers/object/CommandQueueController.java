package io.bacta.game.controllers.object;


import io.bacta.game.command.CommandQueueParameters;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.object.ServerObject;

public interface CommandQueueController {
    void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, CommandQueueParameters params);
}
