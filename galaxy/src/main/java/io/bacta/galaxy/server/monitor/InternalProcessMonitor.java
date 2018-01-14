package io.bacta.galaxy.server.monitor;

import io.bacta.connection.server.ConnectionServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Scope("prototype")
public class InternalProcessMonitor implements ProcessMonitor {

    private ConfigurableApplicationContext applicationContext;
    private boolean stopped;
    private String port;
    private AtomicInteger startCount;

    @Inject
    public InternalProcessMonitor() {
        this.stopped = true;
        startCount = new AtomicInteger();
    }

    @Override
    public void start(String... args) throws IOException {
        this.port = args[0];
        restart();
    }

    private void restart() {
        System.setProperty("io.bacta.connection.server.bindPort", this.port);
        applicationContext = new SpringApplicationBuilder(ConnectionServerApplication.class).run();
        this.stopped = false;
        startCount.incrementAndGet();
    }

    @Override
    public void stop() {
        applicationContext.stop();
        this.stopped = true;
    }

    @Scheduled(fixedRate = 2000)
    private void checkProcess() throws IOException {

        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking is process is alive");
        }

        if(!stopped && !isRunning()) {
            stopped = true;
            LOGGER.error("Process is not running, restarting");
            restart();
        }
    }

    @Override
    public boolean isRunning() {
        return applicationContext.isRunning();
    }

    @Override
    public int getStartCount() {
        return startCount.get();
    }
}
