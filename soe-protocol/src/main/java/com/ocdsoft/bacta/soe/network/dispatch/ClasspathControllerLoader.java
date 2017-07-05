package com.ocdsoft.bacta.soe.network.dispatch;

import com.ocdsoft.bacta.soe.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.soe.util.ClientString;
import com.ocdsoft.bacta.soe.util.MessageHashUtil;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.shared.GameNetworkMessage;
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
