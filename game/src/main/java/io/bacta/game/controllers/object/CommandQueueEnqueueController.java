package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.dispatch.CommandQueueDispatcher;
import io.bacta.game.message.object.CommandQueueEnqueue;
import io.bacta.game.object.ServerObject;
import io.bacta.soe.context.SoeRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.COMMAND_QUEUE_ENQUEUE)
public class CommandQueueEnqueueController implements MessageQueueController<CommandQueueEnqueue> {

	private final CommandQueueDispatcher dispatcher;

	@Inject
	public CommandQueueEnqueueController(final CommandQueueDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void handleIncoming(SoeRequestContext context, ServerObject actor, int flags, float value, CommandQueueEnqueue data) {
		dispatcher.dispatch(context, actor, data);
	}
}
