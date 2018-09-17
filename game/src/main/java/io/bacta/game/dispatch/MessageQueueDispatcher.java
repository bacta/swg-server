package io.bacta.game.dispatch;


import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.ObjControllerMessage;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.MessageQueueController;
import io.bacta.game.object.ServerObject;
import io.bacta.game.service.object.ServerObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by crush on 5/29/2016.
 */
@Component
@Slf4j
public final class MessageQueueDispatcher {

    private final ApplicationContext context;
    private final ServerObjectService serverObjectService;
    private final Map<GameControllerMessageType, MessageQueueController> controllers;

    @Inject
    public MessageQueueDispatcher(final ApplicationContext context,
                                  final ServerObjectService serverObjectService) {
        this.context = context;
        this.serverObjectService = serverObjectService;
        this.controllers = new HashMap<>();

        loadControllers();
    }

    private void loadControllers() {
        String[] controllerBeanNames = context.getBeanNamesForType(MessageQueueController.class);

        for (String controllerBeanName : controllerBeanNames) {
            MessageQueueController controller = (MessageQueueController) context.getBean(controllerBeanName);
            loadController(controller);
        }
    }

    private void loadController(final MessageQueueController controller) {
        final Class<? extends MessageQueueController> controllerClass = controller.getClass();
        final GameControllerMessage controllerAnnotation = controllerClass.getAnnotation(GameControllerMessage.class);

        for (final GameControllerMessageType type : controllerAnnotation.value()) {

            if (controllers.containsKey(type)) {
                LOGGER.error("Controller {} is already handling type {}. Cannot load controller {} to also handle this type.",
                        controllers.get(type).getClass().getName(),
                        type,
                        controllerClass.getName());
            } else {
                LOGGER.debug("Loaded controller {} to handle type {}.",
                        controllerClass.getName(),
                        type);
                controllers.put(type, controller);
            }
        }
    }

    public void dispatch(final GameRequestContext context, final ObjControllerMessage message) {
        final ServerObject actor = serverObjectService.get(message.getActorNetworkId());
        final GameControllerMessageType type = GameControllerMessageType.from(message.getMessageType());

        final MessageQueueController controller = controllers.get(type);

        if (controller != null) {
            controller.handleIncoming(context, actor, message.getFlags(), message.getValue(), message.getData());
        } else {
            LOGGER.error("No controller loaded to handle ObjControllerMessage of type {}.", type);
        }
    }
}
