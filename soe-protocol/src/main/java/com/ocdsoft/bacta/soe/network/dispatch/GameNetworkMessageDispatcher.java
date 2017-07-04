package com.ocdsoft.bacta.soe.network.dispatch;

import com.ocdsoft.bacta.engine.network.dispatch.MessageDispatcher;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.network.message.game.GameNetworkMessage;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageSerializer;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageTypeNotFoundException;
import com.ocdsoft.bacta.soe.util.ClientString;
import com.ocdsoft.bacta.soe.util.GameNetworkMessageTemplateWriter;
import com.ocdsoft.bacta.soe.util.ObjectControllerNames;
import com.ocdsoft.bacta.soe.util.SoeMessageUtil;
import gnu.trove.map.TIntObjectMap;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.nio.ByteBuffer;

/**
 * GameNetworkMessageDispatcher receives and dispatches {@link GameNetworkMessage} instances.  It is able to process
 * messages based the the incoming priority and then create and dispatch the com.ocdsoft.bacta.swg.login.message to be handled by the
 * {@link GameNetworkMessageController} instances.  GameNetworkMessageDispatcher also will generate controllers and
 * messages if they are not recognized to assist in development
 *
 * @author Kyle Burkhardt
 * @since 1.0
 */

@Slf4j
public class GameNetworkMessageDispatcher implements MessageDispatcher {

    private final static int OBJECT_CONTROLLER_MESSAGE = 0x80CE5E46;

    /**
     * Map of controller data to dispatch messages
     */
    private final TIntObjectMap<ControllerData<GameNetworkMessageController>> controllers;

    /**
     * Creates the {@link GameNetworkMessage} to be passed to the appropriate controller
     */
    private final GameNetworkMessageSerializer gameNetworkMessageSerializer;

    /**
     * Generates missing {@link GameNetworkMessage} and {@link GameNetworkMessageController} classes for implementation
     * Writes files directly to the project structure
     */
    private final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter;


    @Inject
    public GameNetworkMessageDispatcher(final ClasspathControllerLoader controllerLoader,
                                        final GameNetworkMessageSerializer gameNetworkMessageSerializer,
                                        final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter) {

        this.gameNetworkMessageSerializer = gameNetworkMessageSerializer;
        this.gameNetworkMessageTemplateWriter = gameNetworkMessageTemplateWriter;

        controllers = controllerLoader.getControllers(GameNetworkMessageController.class);
    }

    public void dispatch(short priority, int gameMessageType, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.increaseGameNetworkMessageReceived();

        final ControllerData<GameNetworkMessageController> controllerData = controllers.get(gameMessageType);

        if (controllerData != null) {
            if (!controllerData.containsRoles(connection.getRoles())) {
                LOGGER.error("Controller security blocked access: {}", controllerData.getController().getClass().getName());
                LOGGER.error("Connection: " + connection.toString());
                return;
            }

            try {
                final GameNetworkMessageController controller = controllerData.getController();
                final GameNetworkMessage incomingMessage = gameNetworkMessageSerializer.readFromBuffer(gameMessageType, buffer);

                LOGGER.trace("received {}", incomingMessage.getClass().getSimpleName());

                LOGGER.debug("Routing to " + controller.getClass().getSimpleName());
                controller.handleIncoming(connection, incomingMessage); //Can't fix this one yet.


            } catch (GameNetworkMessageTypeNotFoundException e) {
                handleMissingController(priority, gameMessageType, buffer);
            } catch (Exception e) {
                LOGGER.error("SWG Message Handling {}", controllerData.getClass(), e);
            }

        } else {
            handleMissingController(priority, gameMessageType, buffer);
        }
    }

    private void handleMissingController(short priority, int gameMessageType, ByteBuffer buffer) {

        if(gameNetworkMessageTemplateWriter == null) {
            final String propertyName = Integer.toHexString(gameMessageType);
            LOGGER.error("Unhandled SWG Message: '{}' 0x{}", ClientString.get(propertyName), propertyName);
            return;
        }

        if (gameMessageType == OBJECT_CONTROLLER_MESSAGE) {
            final int objcType = buffer.getInt(4);
            final String propertyName = Integer.toHexString(objcType);

            gameNetworkMessageTemplateWriter.createObjFiles(objcType, buffer);
            LOGGER.error("{} Unhandled ObjC Message: 0x{}", ObjectControllerNames.get(propertyName), propertyName);
            LOGGER.error(SoeMessageUtil.bytesToHex(buffer));
        } else {

            final String propertyName = Integer.toHexString(gameMessageType);
            gameNetworkMessageTemplateWriter.createGameNetworkMessageFiles(priority, gameMessageType, buffer);
            LOGGER.error("Unhandled SWG Message: '{}' 0x{}", ClientString.get(propertyName), propertyName);
            LOGGER.error(SoeMessageUtil.bytesToHex(buffer));
        }
    }
}
