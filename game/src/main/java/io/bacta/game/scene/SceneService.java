package io.bacta.game.scene;

import akka.actor.ActorRef;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of scene actor refs for all the scenes
 */
@Service
public final class SceneService {

    private final Map<String, ActorRef> localSceneMap = new ConcurrentHashMap<>();

    public ActorRef getScene(String sceneId) {
        return localSceneMap.get(sceneId);
    }

    public void addScene(String sceneId, ActorRef sceneActor) {
        localSceneMap.put(sceneId, sceneActor);
    }

    public ActorRef removeScene(String name) {
        return localSceneMap.remove(name);
    }

    public Set<Map.Entry<String, ActorRef>> getSceneEntries() {
        return localSceneMap.entrySet();
    }
}
