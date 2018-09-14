package io.bacta.engine.actor;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DefaultActorMessageControllerFactory implements ActorMessageControllerFactory {
    private final Map<Class<?>, Class<? extends ActorMessageController>> controllerMap;

    public DefaultActorMessageControllerFactory(Map<Class<?>, Class<? extends ActorMessageController>> controllerMap) {
        this.controllerMap = controllerMap;
    }

    @SuppressWarnings("unchecked")
    public <T> void handleMessage(T msg) throws IllegalAccessException, InstantiationException {
        LOGGER.trace("Handling message of type {}", msg.getClass());

        final Class<? extends ActorMessageController> type = controllerMap.get(msg.getClass());

        //TODO: We should make a message controller factory that can inject beans...
        final ActorMessageController<T> controller = (ActorMessageController<T>) type.newInstance();
        controller.receive(msg);
    }
}