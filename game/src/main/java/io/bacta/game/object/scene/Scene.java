package io.bacta.game.object.scene;

import io.bacta.shared.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
@Slf4j
public final class Scene {

    private String name;
    private String iffPath;
    private final TreeFile treeFile;

    @Inject
    public Scene(final TreeFile treeFile) {
        this.treeFile = treeFile;
    }

    public void configure(String name, String iffPath) {
        if(this.name == null && this.iffPath == null) {
            this.name = name;
            this.iffPath = iffPath;
        } else {
            throw new SceneAlreadyConfiguredException("The scene '" + this.name + "' is already configured.  New Attempt for scene '" + name + "'");
        }
    }

    public void restart() {
        checkConfigured();
        stop();
        start();
    }

    public void start() {
        checkConfigured();
        LOGGER.info("Starting zone {} with iff path {}", name, iffPath);
    }

    public void stop() {
        checkConfigured();
        LOGGER.info("Stopping zone {} with iff path {}", name, iffPath);
    }

    private void checkConfigured() {
        if(name == null || iffPath == null) {
            LOGGER.error("Scene not configured");
            throw new SceneNotConfiguredException();
        }
    }


}
