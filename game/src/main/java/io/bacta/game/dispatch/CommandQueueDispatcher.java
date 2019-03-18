package io.bacta.game.dispatch;


import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.engine.utils.SOECRC32;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.message.object.CommandQueueEnqueue;
import io.bacta.game.object.ServerObject;
import io.bacta.game.service.object.ServerObjectService;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.util.CommandNames;
import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

/**
 * Created by crush on 5/30/2016.
 * <p>
 * Dispatches commands to their respective controllers.
 */
@Component
@Slf4j
public final class CommandQueueDispatcher {

    private final ApplicationContext context;
    private final ServerObjectService serverObjectService;
    private final TIntObjectMap<String> knownCommandNames;
    private final TIntObjectMap<CommandQueueController> controllers;
    private final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter;

    @Inject
    public CommandQueueDispatcher(final ApplicationContext context,
                                  final ServerObjectService serverObjectService,
                                  final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter) {
        this.context = context;
        this.serverObjectService = serverObjectService;
        this.gameNetworkMessageTemplateWriter = gameNetworkMessageTemplateWriter;
        this.knownCommandNames = new TIntObjectHashMap<>();
        this.controllers = new TIntObjectHashMap<>();

        loadCommandNames();
        loadControllers();
    }

    private void loadCommandNames() {
        final ResourceBundle bundle = ResourceBundle.getBundle("commandnames");
        bundle.keySet().stream()
                .forEach(key -> knownCommandNames.put((int) Long.parseLong(key, 16), bundle.getString(key)));
    }

    private void loadControllers() {
        String[] controllerBeanNames = context.getBeanNamesForType(CommandQueueController.class);

        for (String controllerBeanName : controllerBeanNames) {
            CommandQueueController controller = (CommandQueueController) context.getBean(controllerBeanName);
            loadController(controller);
        }
    }

    private void loadController(final CommandQueueController controller) {

        final Class<? extends  CommandQueueController> controllerClass = controller.getClass();
        final QueuesCommand controllerAnnotation = controllerClass.getAnnotation(QueuesCommand.class);

        final String controllerClassName = controllerClass.getName();

        if (controllerAnnotation != null) {
            final String lowerCommandName = controllerAnnotation.value().toLowerCase();
            final int commandHash = SOECRC32.hashCode(lowerCommandName);

            if (controllers.containsKey(commandHash)) {
                LOGGER.error("Controller {} is already handling command '{}'(0x{}). Cannot load controller {} to also handle this type.",
                        controllers.get(commandHash).getClass().getName(),
                        lowerCommandName,
                        Integer.toHexString(commandHash),
                        controllerClassName);
            } else {
                LOGGER.debug("Loaded controller {} to handle type '{}'(0x{}).",
                        controllerClassName,
                        lowerCommandName,
                        Integer.toHexString(commandHash));

                controllers.put(commandHash, controller);
            }
        } else {
            LOGGER.error("Missing QueuesCommand annotation on {}", controllerClassName);
        }
    }

    public void dispatch(final SoeRequestContext context, final ServerObject actor, final CommandQueueEnqueue data) {
        final CommandQueueController controller = controllers.get(data.getCommandHash());

        //TODO: We need to do something with sequence Id...for example, actually queue the commands.

        if (controller != null) {
            final ServerObject target = serverObjectService.get(data.getTargetId());
            controller.handleCommand(context, actor, target, data.getParams());
        } else {
            generateMessageAndController(data);
            LOGGER.error("No controller loaded to handle CommandQueueController for command '{}'(0x{}).",
                    knownCommandNames.get(data.getCommandHash()),
                    Integer.toHexString(data.getCommandHash()));
        }
    }

    private void generateMessageAndController(CommandQueueEnqueue data) {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        data.writeToBuffer(buffer);
        gameNetworkMessageTemplateWriter.createCommandFiles(data, buffer);
        LOGGER.error("Unhandled Command Controller: '{}'", CommandNames.get(data.getCommandHash()));
        LOGGER.error(SoeMessageUtil.bytesToHex(buffer));
    }
}
