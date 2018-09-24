package io.bacta.game.actor.object.zone;

import io.bacta.swg.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
@Slf4j
public final class Zone {

    private String name;
    private String iffPath;
    private final TreeFile treeFile;

    @Inject
    public Zone(final TreeFile treeFile) {
        this.treeFile = treeFile;
    }

    public void configure(String name, String iffPath) {
        this.name = name;
        this.iffPath = iffPath;
    }

    public void restart() {
        //checkConfigured();
        stop();
        start();
    }

    public void start() {
        //checkConfigured();
        LOGGER.info("Starting zone {} with iff path {}", name, iffPath);
    }

    public void stop() {
        //checkConfigured();
        LOGGER.info("Stopping zone {} with iff path {}", name, iffPath);
    }

    private void checkConfigured() {
        if (name == null || iffPath == null) {
            throw new ZoneNotConfiguredException();
        }
    }


}
