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

package io.bacta.soe.network.handler;

import gnu.trove.map.TIntObjectMap;
import io.bacta.engine.utils.SOECRC32;
import io.bacta.game.MessageId;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.context.SoeSessionContext;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerData;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

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
@Component
public final class DefaultGameNetworkMessageHandler implements GameNetworkMessageHandler {
    /**
     * Map of controller data to dispatch messages
     */
    private final TIntObjectMap<GameNetworkMessageControllerData> controllers;

    @Inject
    public DefaultGameNetworkMessageHandler(final GameNetworkMessageControllerLoader controllerLoader) {
        controllers = controllerLoader.loadControllers();
    }

    @Override
    public void handle(final SoeRequestContext context, final GameNetworkMessage gameNetworkMessage) {

        final GameNetworkMessageControllerData controllerData = getControllerData(gameNetworkMessage);

        if (controllerData != null) {
            SoeSessionContext sessionContext = context.getSessionContext();
            if (!controllerData.containsRoles(sessionContext.getRoles())) {
                LOGGER.error("Controller security blocked access: {}", controllerData.getController().getClass().getName());
                LOGGER.error("Connection: {}", context.toString());
                throw new UnauthorizedControllerAccessException("Unauthorized Attempt to use controller " + controllerData.getController().getClass().getName() + " by " + sessionContext.getRemoteAddress());
            }

            try {
                final GameNetworkMessageController controller = controllerData.getController();

                LOGGER.trace("received {}", gameNetworkMessage.getClass().getSimpleName());

                LOGGER.debug("Routing to {}", controller.getClass().getSimpleName());

                controller.handleIncoming(context, gameNetworkMessage); //Can't fix this one yet.

            } catch (Exception e) {
                LOGGER.error("SWG Message Handling {}", controllerData.getClass(), e);
            }

        } else {
            LOGGER.error("Missing controller data for {}", gameNetworkMessage.getClass());
        }
    }

    /**
     * Get controller data, check CRC override
     * @param gameNetworkMessage Message to get controller for
     * @return GameControllerData
     */
    private GameNetworkMessageControllerData getControllerData(GameNetworkMessage gameNetworkMessage) {
        GameNetworkMessageControllerData controllerData = controllers.get(SOECRC32.hashCode(gameNetworkMessage.getClass().getSimpleName()));
        if(controllerData == null) {
            if(gameNetworkMessage.getClass().getAnnotation(MessageId.class) != null) {
                controllerData = controllers.get(gameNetworkMessage.getClass().getAnnotation(MessageId.class).value());
            }
        }
        return controllerData;
    }

}
