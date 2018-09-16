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

package io.bacta.game.dispatch;

import io.bacta.game.context.GameRequestContext;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.dispatch.DefaultGameNetworkMessageDispatcher;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerData;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import io.bacta.soe.serialize.GameNetworkMessageTypeNotFoundException;
import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

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
public class GameGameNetworkMessageDispatcher extends DefaultGameNetworkMessageDispatcher {

    @Inject
    public GameGameNetworkMessageDispatcher(final GameNetworkMessageControllerLoader controllerLoader,
                                            final GameNetworkMessageSerializer gameNetworkMessageSerializer,
                                            final GameNetworkMessageTemplateWriter gameNetworkMessageTemplateWriter) {
        super(controllerLoader, gameNetworkMessageSerializer, gameNetworkMessageTemplateWriter);
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

            try {
                final GameNetworkMessageController controller = controllerData.getController();
                final GameNetworkMessage incomingMessage = gameNetworkMessageSerializer.readFromBuffer(gameMessageType, buffer);

                connection.logReceivedMessage(incomingMessage);
                LOGGER.trace("received {}", incomingMessage.getClass().getSimpleName());

                LOGGER.debug("Routing to {}", controller.getClass().getSimpleName());

                GameRequestContext gameRequestContext = new GameRequestContext(connection);
                controller.handleIncoming(gameRequestContext, incomingMessage); //Can't fix this one yet.


            } catch (GameNetworkMessageTypeNotFoundException e) {
                handleMissingController(priority, gameMessageType, buffer);
            } catch (Exception e) {
                LOGGER.error("SWG Message Handling {}", controllerData.getClass(), e);
            }

        } else {
            handleMissingController(priority, gameMessageType, buffer);
        }
    }
}
