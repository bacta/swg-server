package io.bacta.engine.actor;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Slf4j
public class DefaultActorMessageControllerFactory implements ActorMessageControllerFactory {
    private final Map<Class<?>, Class<? extends ActorMessageController>> controllerMap;

    public DefaultActorMessageControllerFactory(Map<Class<?>, Class<? extends ActorMessageController>> controllerMap) {
        this.controllerMap = controllerMap;
    }

    @SuppressWarnings("unchecked")
    public <T> void handleMessage(T msg) {
        LOGGER.trace("Handling message of type {}", msg.getClass());

        final Class<? extends ActorMessageController> type = controllerMap.get(msg.getClass());

        //TODO: We should make a message controller factory that can inject beans...
        final ActorMessageController<T> controller;
        try {
            controller = (ActorMessageController<T>) type.getDeclaredConstructor().newInstance();
            controller.receive(msg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
           LOGGER.error("Unable to create instance of {}", type.getName(), e);
        }

    }
}