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

package io.bacta.soe.network.dispatch;

import gnu.trove.map.TIntObjectMap;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import io.bacta.soe.serialize.GameNetworkMessageTypeNotFoundException;
import io.bacta.soe.util.ClientString;
import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import io.bacta.soe.util.ObjectControllerNames;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Scope("prototype")
public class DefaultGameNetworkMessageDispatcher implements GameNetworkMessageDispatcher {

    private final static int OBJECT_CONTROLLER_MESSAGE = 0x80CE5E46;

    /**
     * Map of controller data to dispatch messages
     */
    private TIntObjectMap<GameNetworkMessageControllerData> controllers;

    /**
     * Creates the {@link GameNetworkMessage} to be passed to the appropriate controller
     */
    private final GameNetworkMessageSerializer gameNetworkMessageSerializer;

    /**
     * Generates missing {@link GameNetworkMessage} and {@link GameNetworkMessageController} classes for implementation
     * Writes files directly to the project structure
     */
    private final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter;


    private GameNetworkMessageControllerLoader controllerLoader;

    @Inject
    public DefaultGameNetworkMessageDispatcher(final GameNetworkMessageControllerLoader controllerLoader,
                                               final GameNetworkMessageSerializer gameNetworkMessageSerializer,
                                               final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter) {

        this.controllerLoader = controllerLoader;
        this.gameNetworkMessageSerializer = gameNetworkMessageSerializer;
        this.gameNetworkMessageTemplateWriter = gameNetworkMessageTemplateWriter;
    }

    @PostConstruct
    private void initialize() {
        controllers = controllerLoader.loadControllers();
    }

    @Override
    public void dispatch(short priority, int gameMessageType, SoeConnection connection, ByteBuffer buffer) {

        final GameNetworkMessageControllerData controllerData = controllers.get(gameMessageType);

        if (controllerData != null) {
            if (!controllerData.containsRoles(connection.getRoles())) {
                LOGGER.error("Controller security blocked access: {}", controllerData.getController().getClass().getName());
                LOGGER.error("Connection: {}", connection.toString());
                return;
            }

            if(!controllerData.containsConnectionType(connection)) {
                LOGGER.error("Controller security blocked access: {}", controllerData.getController().getClass().getName());
                LOGGER.error("Connection type mismatch: {} expected {}", connection.getClass().getSimpleName(), controllerData.getConnectionClass().getSimpleName());
                return;
            }

            try {
                final GameNetworkMessageController controller = controllerData.getController();
                final GameNetworkMessage incomingMessage = gameNetworkMessageSerializer.readFromBuffer(gameMessageType, buffer);

                connection.logReceivedMessage(incomingMessage);
                LOGGER.trace("received {}", incomingMessage.getClass().getSimpleName());

                LOGGER.debug("Routing to {}", controller.getClass().getSimpleName());
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
