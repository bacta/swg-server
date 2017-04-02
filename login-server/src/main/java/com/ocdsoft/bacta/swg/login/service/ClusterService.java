package com.ocdsoft.bacta.swg.login.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.network.client.ServerStatus;
import com.ocdsoft.bacta.engine.network.client.TcpClient;
import com.ocdsoft.bacta.engine.network.io.tcp.TcpServer;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.event.ConnectEvent;
import com.ocdsoft.bacta.swg.protocol.event.DisconnectEvent;
import com.ocdsoft.bacta.swg.protocol.service.PublisherService;
import com.ocdsoft.bacta.swg.db.LoginDatabaseConnector;
import com.ocdsoft.bacta.swg.server.login.GameClientTcpHandler;
import com.ocdsoft.bacta.swg.server.login.event.GameServerOnlineEvent;
import com.ocdsoft.bacta.swg.server.login.message.LoginClusterStatus;
import com.ocdsoft.bacta.swg.shared.object.ClusterData;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by kburkhardt on 1/18/15.
 */
@Singleton
public class ClusterService implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    private transient final LoginDatabaseConnector dbConnector;
    private transient final Set<ClusterData> clusterServerSet;
    private transient final Set<TcpClient> tcpClientSet;
    private transient final Set<SoeUdpConnection> connectedClients;
    private transient final PublisherService publisherService;

    private final boolean allowDynamicRegistration;

    @Inject
    public ClusterService(final LoginDatabaseConnector dbConnector,
                          final BactaConfiguration bactaConfiguration,
                          final PublisherService publisherService) throws Exception {

        this.tcpClientSet = new HashSet<>();
        this.dbConnector = dbConnector;
        this.allowDynamicRegistration = bactaConfiguration.getBooleanWithDefault("Bacta/LoginServer", "AllowDynamicRegistration", false);
        this.publisherService = publisherService;
        this.connectedClients = new HashSet<>();
        final Set<ClusterData> savedData = dbConnector.getObject("ClusterServerSet", CopyOnWriteArraySet.class);
        this.clusterServerSet = (savedData == null ? new CopyOnWriteArraySet<>() : savedData);

        this.publisherService.subscribe(ConnectEvent.class, this::addConnection);
        this.publisherService.subscribe(DisconnectEvent.class, this::removeConnection);
        this.publisherService.subscribe(GameServerOnlineEvent.class, this::handleGameServerOnline);
    }

    private void handleGameServerOnline(final GameServerOnlineEvent gameServerOnlineEvent) {

        final ClusterData clusterServer = gameServerOnlineEvent.getClusterServer();
        final InetSocketAddress tcpAddress = new InetSocketAddress(clusterServer.getRemoteAddress().getAddress(), clusterServer.getTcpPort());

        clusterServerSet.remove(clusterServer);
        clusterServerSet.add(clusterServer);

        if (!isTcpConnected(tcpAddress)) {

            final TcpClient tcpClient = new TcpClient(tcpAddress, new ChannelInitializer< SocketChannel >() { // (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addFirst(new IdleStateHandler(0, 25, 0));
                    ch.pipeline().addLast(new GameClientTcpHandler());
                }
            });

            tcpClient.addObserver(this);

            tcpClientSet.add(tcpClient);
            tcpClient.start();
        }
    }

    private boolean isTcpConnected(final InetSocketAddress tcpAddress) {
        for(final TcpClient tcpClient : tcpClientSet) {
            if(tcpClient.getRemoteAddress().equals(tcpAddress)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(final Observable o, final Object arg) {

        TcpServer.Status status = (TcpServer.Status) arg;
        if(status == TcpServer.Status.CONNECTED) {

            for(final SoeUdpConnection connection : connectedClients) {
                connection.sendMessage(new LoginClusterStatus(clusterServerSet));
            }

        } else if ( status == TcpServer.Status.DISCONNECTED) {
            TcpClient tcpClient = (TcpClient) o;
            tcpClientSet.remove(tcpClient);
            for(final ClusterData clusterServer : clusterServerSet) {
                if(clusterServer.getRemoteAddress().getAddress().equals(tcpClient.getRemoteAddress().getAddress())) {
                    clusterServer.getStatusClusterData().setStatus(ServerStatus.DOWN);
                    break;
                }
            }

            for(final SoeUdpConnection connection : connectedClients) {
                connection.sendMessage(new LoginClusterStatus(clusterServerSet));
            }
        }
    }

    public Set<ClusterData> getClusterEntries() {
        return clusterServerSet;
    }

    private void update() {
        dbConnector.updateObject("ClusterServerSet", clusterServerSet);
    }

    public void addConnection(final ConnectEvent connectEvent) {
        connectedClients.add(connectEvent.getConnection());
    }

    public void removeConnection(final DisconnectEvent disconnectEvent) {
        connectedClients.remove(disconnectEvent.getConnection());
    }
}
