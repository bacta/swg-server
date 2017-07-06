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
import io.bacta.soe.network.controller.MessageHandled;
import io.bacta.soe.util.ClientString;
import io.bacta.soe.util.MessageHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Created by kyle on 4/22/2016.
 */
@Slf4j
public final class ClasspathControllerLoader {

    private final ApplicationContext context;

    public ClasspathControllerLoader(final ApplicationContext context) {
        this.context = context;
    }

    public <T> TIntObjectMap<ControllerData<T>> getControllers(Class<T> clazz) {

        final TIntObjectMap<ControllerData<T>> controllers = new TIntObjectHashMap<>();
        final Reflections reflections = new Reflections();
        final Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(clazz);

        for (final Class<? extends T> controllerClass : subTypes)
            loadControllerClass(controllers, controllerClass);

        return controllers;
    }

    private <T> void loadControllerClass(final TIntObjectMap<ControllerData<T>> controllers,
                                         final Class<? extends T> controllerClass) {
        try {

            if (Modifier.isAbstract(controllerClass.getModifiers()))
                return;

            MessageHandled controllerAnnotation = controllerClass.getAnnotation(MessageHandled.class);

            if (controllerAnnotation == null) {
                LOGGER.warn("Missing @MessageHandled annotation, discarding: " + controllerClass.getName());
                return;
            }

            final ConnectionRolesAllowed connectionRolesAllowed = controllerClass.getAnnotation(ConnectionRolesAllowed.class);
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

                final T controller = context.getBean(controllerClass);
                final ControllerData<T> newControllerData = new ControllerData<>(controller, connectionRoles);

                if (!controllers.containsKey(hash)) {
                    LOGGER.debug("Adding Controller {} '{}' 0x{}", controllerClass.getName(), ClientString.get(propertyName), propertyName);
                    controllers.put(hash, newControllerData);
                } else {
                    final ControllerData existingController = controllers.get(hash);
                    LOGGER.error("Controller {} for com.ocdsoft.bacta.swg.login.message '{}'(0x{}) duplicates controller {}.",
                            controllerClass.getName(),
                            ClientString.get(propertyName),
                            propertyName,
                            existingController.getController().getClass().getName());

                }
            }
        } catch (Throwable e) {
            LOGGER.error("Unable to add controller: " + controllerClass.getName(), e);
        }
    }
}
