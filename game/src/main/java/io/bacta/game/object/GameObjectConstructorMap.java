package io.bacta.game.object;

import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.object.GameObject;
import io.bacta.swg.template.ObjectTemplateList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kyle on 6/5/2016.
 */
@Slf4j
@Service
public final class GameObjectConstructorMap {

    private final Map<Class, Constructor> constructorMap;

    @Inject
    public GameObjectConstructorMap() {
        constructorMap = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends GameObject> Constructor<T> get(final Class<T> clazz) {

        Constructor<T> constructor = constructorMap.get(clazz);

        if (constructor == null) {
            try {
                constructor = clazz.getConstructor(ObjectTemplateList.class, SlotIdManager.class, ServerObjectTemplate.class);
                constructorMap.put(clazz, constructor);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return constructor;
    }
}