package io.bacta.game.controllers.command;

import io.bacta.game.command.CommandQueueParameters;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectService;
import io.bacta.swg.math.Vector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@QueuesCommand("teleport")
public class TeleportCommandController implements CommandQueueController {
    private final ServerObjectService serverObjectService;

    @Inject
    public TeleportCommandController(ServerObjectService serverObjectService) {
        this.serverObjectService = serverObjectService;
    }

    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, CommandQueueParameters params) {
        final Vector worldPosition = params.nextVector();
        final long targetCell = params.nextNetworkId();
        final Vector parentPosition = params.nextVector();

        //If they have no target selected, then we want to teleport the actor instead.
        final ServerObject teleportTarget = target != null ? target : actor;

        //TODO: Teleporting.
    }
}