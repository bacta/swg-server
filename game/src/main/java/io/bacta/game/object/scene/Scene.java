package io.bacta.game.object.scene;

import io.bacta.game.GameServerProperties;
import io.bacta.shared.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
@Slf4j
public final class Scene {

    private GameServerProperties.Scene config;
    private final TreeFile treeFile;

    @Inject
    public Scene(final TreeFile treeFile) {
        this.treeFile = treeFile;
    }

    public void configure(final GameServerProperties.Scene config) {
        if(this.config == null) {
            this.config = config;
        } else {
            throw new SceneAlreadyConfiguredException("The scene '" + this.config.getName() + "' is already configured.  New Attempt for scene '" + config.getName() + "'");
        }
    }

    public void start() {
        if(config == null) {
            throw new SceneNotConfiguredException();
        }

        LOGGER.info("Starting scene {} with iff path {}", config.getName(), config.getIffPath());
    }

    public void stop() {
        if(config != null) {
            LOGGER.info("Stopping scene {} with iff path {}", config.getName(), config.getIffPath());
        }
    }
}
