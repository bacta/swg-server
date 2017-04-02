package com.ocdsoft.bacta.soe.protocol.dispatch;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ocdsoft.bacta.soe.protocol.ServerType;
import com.ocdsoft.bacta.soe.protocol.ServerState;
import com.ocdsoft.bacta.soe.protocol.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.protocol.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.protocol.controller.MessageHandled;
import com.ocdsoft.bacta.soe.protocol.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.protocol.util.ClientString;
import com.ocdsoft.bacta.soe.protocol.util.MessageHashUtil;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by kyle on 4/22/2016.
 */
public final class ClasspathControllerLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathControllerLoader.class);

    private final Injector injector;
    private final ServerState serverState;

    @Inject
    public ClasspathControllerLoader(final Injector injector,
                                     final ServerState serverState) {

        this.injector = injector;
        this.serverState = serverState;
    }

    public <T> TIntObjectMap<ControllerData<T>> getControllers(Class<T> clazz) {

        final TIntObjectMap<ControllerData<T>> controllers = new TIntObjectHashMap<>();
        final Reflections reflections = new Reflections();
        final Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(clazz);

        for (final Class<? extends T> controllerClass : subTypes)
            loadControllerClass(controllers, injector, controllerClass, serverState);

        return controllers;
    }

    private <T> void loadControllerClass(final TIntObjectMap<ControllerData<T>> controllers,
                                         final Injector injector,
                                         final Class<? extends T> controllerClass,
                                         final ServerState serverState) {
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
                final List<ServerType> serverTypes = new ArrayList<>();
                Collections.addAll(serverTypes, controllerAnnotation.type());

                final String propertyName = Integer.toHexString(hash);

                if (serverTypes.contains(serverState.getServerType())) {

                    final T controller = injector.getInstance(controllerClass);
                    final ControllerData<T> newControllerData = new ControllerData<>(controller, connectionRoles);

                    if (!controllers.containsKey(hash)) {
                        LOGGER.debug("{} Adding Controller {} '{}' 0x{}", serverState.getServerType().name(), controllerClass.getName(), ClientString.get(propertyName), propertyName);
                        controllers.put(hash, newControllerData);
                    } else {
                        final ControllerData existingController = controllers.get(hash);
                        LOGGER.error("{} Controller {} for message '{}'(0x{}) duplicates controller {}.",
                                serverState.getServerType().name(),
                                controllerClass.getName(),
                                ClientString.get(propertyName),
                                propertyName,
                                existingController.getController().getClass().getName());

                    }

                } else {
                    LOGGER.debug("{} Ignoring Controller {} '{}' 0x{}", serverState.getServerType().name(), controllerClass.getName(), ClientString.get(propertyName), propertyName);
                }
            }

        } catch (Throwable e) {
            LOGGER.error("Unable to add controller: " + controllerClass.getName(), e);
        }
    }
}
