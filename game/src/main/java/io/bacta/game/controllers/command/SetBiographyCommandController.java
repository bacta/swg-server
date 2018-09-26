package io.bacta.game.controllers.command;

import io.bacta.game.command.CommandQueueParameters;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.object.ServerObject;
import io.bacta.game.player.BiographyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@QueuesCommand("setbiography")
public class SetBiographyCommandController implements CommandQueueController {
    private final BiographyService biographyService;

    @Inject
    public SetBiographyCommandController(BiographyService biographyService) {
        this.biographyService = biographyService;
    }

    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, CommandQueueParameters params) {
        biographyService.setBiography(target, params.toString());
    }
}
