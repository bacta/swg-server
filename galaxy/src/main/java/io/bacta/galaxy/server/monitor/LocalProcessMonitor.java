package io.bacta.galaxy.server.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Starts a new process and continually monitors it.  Checks the process regularly to make sure it is still alive
 * and restarts it if it is no longer running.
 */
@Slf4j
@Component
@Scope("prototype")
public class LocalProcessMonitor implements ProcessMonitor {

    // Process that will be monitored
    private Process process;

    private String[] commands;

    private AtomicInteger startCount;

    private boolean stopped;

    @Inject
    public LocalProcessMonitor() {
        startCount = new AtomicInteger();
        stopped = true;
    }

    @Override
    public void start(String... args) throws IOException {
        commands = args;
        restart();
    }

    private void restart() throws IOException {
        LOGGER.info("Starting process");
        ProcessBuilder build = new ProcessBuilder(commands);

        process = build.redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start();

        stopped = false;
        startCount.incrementAndGet();
    }

    @Override
    @PreDestroy
    public void stop() {
        stopped = true;
        process.destroy();
    }

    @Scheduled(fixedRate = 2000)
    private void checkProcess() throws IOException {

        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking is process is alive");
        }

        if(!stopped && !process.isAlive()) {
            stopped = true;
            LOGGER.error("Process is not running, restarting");
            restart();
        }
    }

    @Override
    public boolean isRunning() {
        return process.isAlive();
    }

    @Override
    public int getStartCount() {
        return startCount.get();
    }

}
