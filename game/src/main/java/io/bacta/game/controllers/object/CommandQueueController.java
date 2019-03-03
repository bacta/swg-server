package io.bacta.game.controllers.object;


import io.bacta.game.object.ServerObject;
import io.bacta.soe.context.SoeRequestContext;

public interface CommandQueueController {
    void handleCommand(SoeRequestContext context, ServerObject actor, ServerObject target, String params);
}
