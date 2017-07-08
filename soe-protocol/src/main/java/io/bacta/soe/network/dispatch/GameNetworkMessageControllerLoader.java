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
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import io.bacta.soe.util.ClientString;
import io.bacta.soe.util.MessageHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;

/**
 * Created by kyle on 4/22/2016.
 */
@Slf4j
@Component
@Scope("prototype")
public final class GameNetworkMessageControllerLoader implements ApplicationContextAware {

    private ApplicationContext context;

    public TIntObjectMap<GameNetworkMessageControllerData> loadControllers() {

        final TIntObjectMap<GameNetworkMessageControllerData> controllers = new TIntObjectHashMap<>();
        String[] controllerBeanNames = context.getBeanNamesForType(GameNetworkMessageController.class);

        for (String controllerBeanName : controllerBeanNames) {
            GameNetworkMessageController controller = (GameNetworkMessageController) context.getBean(controllerBeanName);
            loadControllerClass(controllers, controller);
        }

        return controllers;
    }

    private <T> void loadControllerClass(final TIntObjectMap<GameNetworkMessageControllerData> controllers,
                                         final GameNetworkMessageController controller) {
        try {

            if (Modifier.isAbstract(controller.getClass().getModifiers()))
                return;

            MessageHandled controllerAnnotation = controller.getClass().getAnnotation(MessageHandled.class);

            if (controllerAnnotation == null) {
                LOGGER.warn("Missing @MessageHandled annotation, discarding: " + controller.getClass().getName());
                return;
            }

            final ConnectionRolesAllowed connectionRolesAllowed = controller.getClass().getAnnotation(ConnectionRolesAllowed.class);
            final ConnectionRole[] connectionRoles;
            if (connectionRolesAllowed == null) {
                connectionRoles = new ConnectionRole[]{ConnectionRole.AUTHENTICATED};
            } else {
                connectionRoles = connectionRolesAllowed.value();
            }

            final Class<? extends GameNetworkMessage>[] handledMessageClasses = controllerAnnotation.handles();

            for (final Class<? extends GameNetworkMessage> handledMessageClass : handledMessageClasses) {
                final int hash = MessageHashUtil.getHash(handledMessageClass);

                final String propertyName = Integer.toHexString(hash);

                final GameNetworkMessageControllerData newControllerData = new GameNetworkMessageControllerData(controller, connectionRoles);

                if (!controllers.containsKey(hash)) {
                    LOGGER.debug("Adding Controller {} '{}' 0x{}", controller.getClass().getName(), ClientString.get(propertyName), propertyName);
                    controllers.put(hash, newControllerData);
                } else {
                    final GameNetworkMessageControllerData existingController = controllers.get(hash);
                    LOGGER.error("Controller {} for message '{}'(0x{}) duplicates controller {}.",
                            controller.getClass().getName(),
                            ClientString.get(propertyName),
                            propertyName,
                            existingController.getController().getClass().getName());

                }
            }
        } catch (Throwable e) {
            LOGGER.error("Unable to add controller: " + controller.getClass().getName(), e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
