package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.network.tcp.TcpClient;
import com.ocdsoft.bacta.engine.network.tcp.TcpServer;
import com.ocdsoft.bacta.soe.event.ConnectEvent;
import com.ocdsoft.bacta.soe.event.DisconnectEvent;
import com.ocdsoft.bacta.soe.network.ServerStatus;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.service.PublisherService;
import com.ocdsoft.bacta.swg.login.GameClientTcpHandler;
import com.ocdsoft.bacta.swg.login.db.AccountDatabaseConnector;
import com.ocdsoft.bacta.swg.login.event.GameServerOnlineEvent;
import com.ocdsoft.bacta.swg.login.message.LoginEnumCluster;
import com.ocdsoft.bacta.swg.login.object.ClusterData;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by kburkhardt on 1/18/15.
 */
@Slf4j
public class ClusterService implements Observer {

    private transient final AccountDatabaseConnector dbConnector;
    private transient final Set<ClusterData> clusterServerSet;
    private transient final Set<TcpClient> tcpClientSet;
    private transient final Set<SoeUdpConnection> connectedClients;
    private transient final PublisherService publisherService;

    private final boolean allowDynamicRegistration;
    private final int timezone;

    @Inject
    public ClusterService(final AccountDatabaseConnector dbConnector,
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
        timezone = DateTimeZone.getDefault().getOffset(null) / 1000;
    }

    public void sendClusterData(SoeUdpConnection connection) {

        LoginEnumCluster cluster = new LoginEnumCluster(getEnumCluster(), timezone);
        connection.sendMessage(cluster);

        com.ocdsoft.bacta.swg.login.message.LoginClusterStatus status = new com.ocdsoft.bacta.swg.login.message.LoginClusterStatus(getClusterStatus());
        connection.sendMessage(status);
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
                connection.sendMessage(new com.ocdsoft.bacta.swg.login.message.LoginClusterStatus(clusterServerSet));
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
                connection.sendMessage(new com.ocdsoft.bacta.swg.login.message.LoginClusterStatus(clusterServerSet));
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
