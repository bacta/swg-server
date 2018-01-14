package io.bacta.galaxy.server.monitor;

import javax.annotation.PreDestroy;
import java.io.IOException;

public interface ProcessMonitor {
    void start(String... args) throws IOException;

    @PreDestroy
    void stop();

    boolean isRunning();

    int getStartCount();
}
