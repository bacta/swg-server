/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.soe.network.dispatch;

import bacta.io.network.dispatch.MessageDispatcher;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.controller.GameNetworkMessageController;
import bacta.io.soe.serialize.GameNetworkMessageSerializer;
import bacta.io.soe.serialize.GameNetworkMessageTypeNotFoundException;
import bacta.io.soe.util.ClientString;
import bacta.io.soe.util.GameNetworkMessageTemplateWriter;
import bacta.io.soe.util.ObjectControllerNames;
import bacta.io.soe.util.SoeMessageUtil;
import gnu.trove.map.TIntObjectMap;
import io.bacta.shared.GameNetworkMessage;
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
