package io.bacta.engine.actor;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassGraphActorMessageControllerClassLoader implements ActorMessageControllerClassLoader {
    private static final String CONTROLLER_INTERFACE_NAME = ActorMessageController.class.getName();

    @Override
    @SuppressWarnings("unchecked")
    public Map<Class<?>, Class<? extends ActorMessageController>> loadControllers(String... packageNames) {
        //Not caching this since we will likely only call this at startup, and then don't want to persist this map.
        try (final ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .whitelistPackages(packageNames)
                .scan()) {

            final ClassInfoList controllerClasses = scanResult.getSubclasses(CONTROLLER_INTERFACE_NAME);
            final List<Class<?>> controllerClassRefs = controllerClasses.loadClasses();

            //If none are found, return an empty map.
            if (controllerClassRefs.isEmpty()) {
                return Collections.emptyMap();
            }

            final Map<Class<?>, Class<? extends ActorMessageController>> controllers = new HashMap<>(controllerClassRefs.size());

            for (final Class<?> controllerRef : controllerClassRefs) {
                //Get the type argument of the controller class. This is the message type for the controller.
                //The message type will serve as the key for the controller type.
                final Class<?> messageType = controllerRef.getTypeParameters()[0].getGenericDeclaration();

                //We are trusting the ClassGraph library to give us what we asked for here.
                final Class<? extends ActorMessageController> castControllerRef = (Class<? extends ActorMessageController>) controllerRef;

                controllers.put(messageType, castControllerRef);
            }

            return controllers;

        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }
}
