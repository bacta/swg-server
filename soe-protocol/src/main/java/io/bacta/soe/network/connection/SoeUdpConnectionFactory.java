package io.bacta.soe.network.connection;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.management.*;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Random;

/**
 * This factory creates the underlying UDP connection
 */
@Component
@Slf4j
public class SoeUdpConnectionFactory {

    private final Random randomId;
    private final MBeanServer mBeanServer;

    private final SoeNetworkConfiguration networkConfiguration;
    private final GameNetworkMessageSerializer messageSerializer;

    /**
     * Constructs a factory with references the injected network configuration and message serializer
     */
    @Inject
    public SoeUdpConnectionFactory(final SoeNetworkConfiguration networkConfiguration,
                                   final GameNetworkMessageSerializer messageSerializer) {
        this.networkConfiguration = networkConfiguration;
        this.messageSerializer = messageSerializer;
        this.randomId = new SecureRandom();
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Creates a new {@link DefaultSoeUdpConnection} class this includes injecting
     * the serializer responsible for turning message classes into
     * {@link java.nio.ByteBuffer}
     * @return new {@link DefaultSoeUdpConnection} for the sender
     */
    public SoeUdpConnection newInstance(final InetSocketAddress sender) {

        final int connectionID = randomId.nextInt();
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("Bacta:type=SoeUdpConnection,id=" + connectionID);
        } catch (MalformedObjectNameException e) {
            LOGGER.error("Improper name", e);
            throw new RuntimeException(e);
        }

        SoeUdpConnection connection = new DefaultSoeUdpConnection (
                objectName,
                sender,
                connectionID,
                networkConfiguration,
                new DefaultIncomingMessageProcessor(networkConfiguration),
                new OutgoingMessageProcessor(networkConfiguration, messageSerializer)
        );

        try {
            mBeanServer.registerMBean(connection, objectName);
        } catch (InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
            LOGGER.error("Unable to register MBean", e);
        }

        return connection;
    }
}
