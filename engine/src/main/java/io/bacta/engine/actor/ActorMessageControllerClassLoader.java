package io.bacta.engine.actor;

import java.util.Map;

public interface ActorMessageControllerClassLoader {
    Map<Class<?>, Class<? extends ActorMessageController>> loadControllers(String... packages);
}
