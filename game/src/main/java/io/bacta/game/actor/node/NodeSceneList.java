package io.bacta.game.actor.node;

import io.bacta.game.GameServerProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class NodeSceneList {
    private final List<GameServerProperties.Scene> scenes;
}
