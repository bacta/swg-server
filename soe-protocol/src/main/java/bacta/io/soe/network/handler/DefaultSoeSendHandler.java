package bacta.io.soe.network.handler;

import bacta.io.network.ConnectionState;
import bacta.io.network.udp.UdpEmitter;
import bacta.io.soe.config.SoeNetworkConfiguration;
import bacta.io.soe.event.DisconnectEvent;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.connection.SoeUdpConnectionCache;
import bacta.io.soe.service.PublisherService;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kyle on 7/3/2017.
 */

@Slf4j
@Component
@Scope("prototype")
public class DefaultSoeSendHandler implements SoeUdpSendHandler, Runnable {

    private final SoeNetworkConfiguration networkConfiguration;
    private final PublisherService publisherService;
    private final MetricRegistry metricRegistry;

    private UdpEmitter emitter;
    private SoeUdpConnectionCache connectionCache;

    private Histogram sendQueueSizes;
    private Gauge<Integer> sizeGauge;

    private final Thread sendThread;

    @Inject
    public DefaultSoeSendHandler(final SoeNetworkConfiguration networkConfiguration,
                                 final PublisherService publisherService,
                                 final MetricRegistry metricRegistry) {

        this.networkConfiguration = networkConfiguration;
        this.publisherService = publisherService;
        this.metricRegistry = metricRegistry;

        sendThread = new Thread(this);
    }

    @Override
    public void start(final String metricPrefix, final SoeUdpConnectionCache connectionCache, final UdpEmitter udpEmitter) {
        if(!sendThread.isAlive()) {
            sendThread.setName("SendHandler");
            sendQueueSizes = metricRegistry.histogram(metricPrefix + ".outgoing-queue");
            metricRegistry.register(metricPrefix, (Gauge<Integer>) () -> connectionCache.getConnectionCount());
            this.connectionCache = connectionCache;
            this.emitter = udpEmitter;
            sendThread.start();
        }
    }

    @Override
    public void run() {
        long nextIteration = 0;

        try {

            while (true) {

                long currentTime = System.currentTimeMillis();

                if (nextIteration > currentTime) {
                    Thread.sleep(nextIteration - currentTime);
                }

                try {

                    nextIteration = currentTime + networkConfiguration.getNetworkThreadSleepTimeMs();
                    sendPendingMessages();

                } catch (Exception e) {
                    LOGGER.error("Unknown", e);
                }
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Send thread interrupted", e);
        }
    }

    private void sendPendingMessages() {

        final Set<InetSocketAddress> connectionList = connectionCache.keySet();
        final List<InetSocketAddress> deadClients = new ArrayList<>();

        for (InetSocketAddress inetSocketAddress : connectionList) {
            final SoeUdpConnection connection = connectionCache.get(inetSocketAddress);

            if (connection == null) {
                continue;
            }

            if (connection.getState() == ConnectionState.DISCONNECTED) {
                deadClients.add(inetSocketAddress);
            }

            final List<ByteBuffer> messages = connection.getPendingMessages();
            if(messages.size() > 0) {
                sendQueueSizes.update(messages.size());
            }

            for (final ByteBuffer message : messages) {
                emitter.sendMessage(connection, message);
            }
        }

        for (InetSocketAddress inetSocketAddress : deadClients) {
            final SoeUdpConnection connection = connectionCache.remove(inetSocketAddress);
            if(connection != null) {
                publisherService.onEvent(new DisconnectEvent(connection));

                if (networkConfiguration.isReportUdpDisconnects()) {
                    LOGGER.info("Client disconnected: {}  Connection: {}  Reason: {}", connection.getRemoteAddress(), connection.getId(), connection.getTerminateReason().getReason());
                    LOGGER.info("Clients still connected: {}", connectionCache.getConnectionCount());
                }
            }
        }
    }
}
