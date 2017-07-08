package io.bacta.galaxy.server.service;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import io.bacta.network.ConnectionState;
import io.bacta.service.scheduler.Task;
import io.bacta.service.scheduler.TaskSchedulerService;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.SoeConnectionProvider;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.service.InternalMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by kyle on 7/7/2017.
 */

@Component
@Slf4j
public class GalaxyInternalMessageService implements InternalMessageService {

    private SoeUdpConnectionCache connectionCache;
    private SoeConnectionProvider connectionProvider;
    private final TaskSchedulerService taskSchedulerService;

    private SoeUdpConnection loginConnection;
    private boolean loginConnectionPending;

    @Inject
    public GalaxyInternalMessageService(final SoeNetworkConfiguration networkConfiguration,
                                        final TaskSchedulerService taskSchedulerService) {

        this.taskSchedulerService = taskSchedulerService;
        this.loginConnection = null;
        this.loginConnectionPending = false;
    }

    @Override
    public void setConnectionObjects(final SoeUdpConnectionCache connectionCache, final SoeConnectionProvider connectionProvider) {
        this.connectionCache = connectionCache;
        this.connectionProvider = connectionProvider;

        taskSchedulerService.scheduleAtFixedRate(new Task() {
            @Override
            public void run() {
                doConnectionMaintenance();
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void doConnectionMaintenance() {
        makeLoginConnection();
    }

    private void makeLoginConnection() {

        if(!loginConnectionPending && (loginConnection == null || loginConnection.getState() != ConnectionState.ONLINE)) {
            loginConnectionPending = true;
            loginConnection = null;
            InetAddress loginAddress = null;
            try {
                loginAddress = InetAddress.getByName("192.168.1.100");
            } catch (UnknownHostException e) {
                LOGGER.error("Unable to parse InetAddress '{}'", "");
            }

            SoeUdpConnection connection = connectionProvider.newInstance(new InetSocketAddress(loginAddress, 44454));
            connection.setState(ConnectionState.NEW);
            connectionCache.put(connection.getRemoteAddress(), connection);
            connection.connect(this::loginConnected);
        }
    }

    private void loginConnected(SoeUdpConnection connection) {
        this.loginConnection = connection;
        loginConnectionPending = false;
    }

    @Override
    public SoeUdpConnection getSessionServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getGalaxyServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getConnectionServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getLoginServerConnection() {

        while(loginConnection == null || loginConnection.getState() != ConnectionState.ONLINE) {
            try {
                makeLoginConnection();
                Strand.sleep(500);
            } catch (SuspendExecution | InterruptedException suspendExecution) {
                suspendExecution.printStackTrace();
            }
        }
        return loginConnection;
    }


}
