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

import bacta.io.soe.network.ServerState;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.controller.GameClientMessageController;
import gnu.trove.map.TIntObjectMap;
import io.bacta.shared.GameNetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by crush on 5/26/2016.
 */
public final class GameClientMessageDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameClientMessageDispatcher.class);

    private final TIntObjectMap<ControllerData<GameClientMessageController>> controllers;
    private final ServerState serverState;

    @Inject
    public GameClientMessageDispatcher(final ClasspathControllerLoader controllerLoader,
                                       final ServerState serverState) {

        LOGGER.debug("Loading");

        this.controllers = controllerLoader.getControllers(GameClientMessageController.class);
        this.serverState = serverState;

        controllers.forEachEntry((key, data) -> {
            LOGGER.debug("Loaded GameClientMessageController {} for messageType {}", data.getController().getClass().getSimpleName(), key);
            return true;
        });
    }

    public void dispatch(final long[] distributionList, final boolean reliable, final int messageType, final GameNetworkMessage message, final SoeUdpConnection connection) {
        final ControllerData<GameClientMessageController> controllerData = controllers.get(messageType);

        if (controllerData != null) {
            if (!controllerData.containsRoles(connection.getRoles())) {
                LOGGER.error("Controller security blocked access: {}", controllerData.getController().getClass().getName());
                LOGGER.error("Connection: " + connection.toString());
                return;
            }

            try {
                final GameClientMessageController controller = controllerData.getController();

                LOGGER.debug("Dispatching GameClientMessage with client com.ocdsoft.bacta.swg.login.message {} to {} clients.",
                        message.getClass().getSimpleName(),
                        distributionList.length);

                controller.handleIncoming(distributionList, reliable, message, connection);

            } catch (Exception e) {
                LOGGER.error("SWG Message Handling {}", controllerData.getClass(), e);
            }
        } else {
            LOGGER.trace("No loaded controller for GameClientMessage with messageType {}. Silently failing.", messageType);
        }
    }
}
