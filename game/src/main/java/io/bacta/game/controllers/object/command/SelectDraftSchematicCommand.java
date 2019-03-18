package io.bacta.game.controllers.object.command;

import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.object.ServerObject;
import io.bacta.soe.context.SoeRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@QueuesCommand("selectdraftschematic")
public class SelectDraftSchematicCommand implements CommandQueueController {
	@Override
	public void handleCommand(SoeRequestContext context, ServerObject actor, ServerObject target, String params) {
		LOGGER.warn("This command not implemented");
	}
}
