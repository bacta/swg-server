package io.bacta.galaxy.server.monitor;

import io.bacta.galaxy.server.GalaxyServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@Scope("prototype")
public class ConnectionServerMonitor {

    @Inject
    private GalaxyServerProperties properties;

    @Inject
    private ApplicationContext applicationContext;
    private final Set<ProcessMonitor> connectionServerMonitorSet;

    @Inject
    public ConnectionServerMonitor() {
        connectionServerMonitorSet = new HashSet<>();
    }

    @PostConstruct
    private void start() {
        startConnectionServers();
    }

    private void startConnectionServers() {
        properties.getConnectionServers().forEach(connectionServer -> {
            if(connectionServer.getType().equalsIgnoreCase(GalaxyServerProperties.ConnectionServer.LOCAL)) {
                startLocalServer(connectionServer);
            } else if(connectionServer.getType().equalsIgnoreCase(GalaxyServerProperties.ConnectionServer.LOCAL)) {
                startRemoteServer(connectionServer);
            } else {
                startInternalServer(connectionServer);
            }
        });
    }

    private void startInternalServer(GalaxyServerProperties.ConnectionServer connectionServer) {
        try {

            InternalProcessMonitor pm = applicationContext.getBean(InternalProcessMonitor.class);
            pm.start(connectionServer.getPort());

            connectionServerMonitorSet.add(pm);

        } catch (IOException e) {
            LOGGER.error("Unable to start internal connection server", e);
        }
    }

    private void startLocalServer(GalaxyServerProperties.ConnectionServer connectionServer) {
        try {

            LocalProcessMonitor pm = applicationContext.getBean(LocalProcessMonitor.class);
            connectionServerMonitorSet.add(pm);
            pm.start("");

            connectionServerMonitorSet.add(pm);

        } catch (IOException e) {
            LOGGER.error("Unable to start local connection server", e);
        }
    }

    private void startRemoteServer(GalaxyServerProperties.ConnectionServer connectionServer) {
        throw new NotImplementedException();
    }

    @PreDestroy
    private void stop() {
        connectionServerMonitorSet.clear();
    }
}
