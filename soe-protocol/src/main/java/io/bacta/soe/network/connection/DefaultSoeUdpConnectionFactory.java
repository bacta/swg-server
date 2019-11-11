package io.bacta.soe.network.connection;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.interceptor.SoeUdpConnectionOrderedMessageInterceptor;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This factory creates the underlying UDP connection representation
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@ConfigurationProperties("io.bacta.soe.network.connection")
public class DefaultSoeUdpConnectionFactory {

    private final Random randomId;

    private final SoeNetworkConfiguration networkConfiguration;
    private final GameNetworkMessageSerializer messageSerializer;

    private final List<SoeUdpConnectionOrderedMessageInterceptor> interceptors;

    /**
     * Constructs a factory with references the injected network configuration and message serializer
     */
    @Inject
    public DefaultSoeUdpConnectionFactory(final SoeNetworkConfiguration networkConfiguration,
                                          final GameNetworkMessageSerializer messageSerializer,
                                          @Value("#{'${io.bacta.soe.network.connection.interceptorClasses}'.split(',')}") final String[] injectorClasses) {
        this.networkConfiguration = networkConfiguration;
        this.messageSerializer = messageSerializer;
        this.randomId = new SecureRandom();

        this.interceptors = loadInterceptorClasses(injectorClasses);
    }

    private List<SoeUdpConnectionOrderedMessageInterceptor> loadInterceptorClasses(String[] injectorClasses) {
        List<SoeUdpConnectionOrderedMessageInterceptor> injectors = new ArrayList<>();
        for(String className : injectorClasses) {
            try {
                Class<SoeUdpConnectionOrderedMessageInterceptor> clazz = (Class<SoeUdpConnectionOrderedMessageInterceptor>) Class.forName(className);
                if(clazz != null && SoeUdpConnectionOrderedMessageInterceptor.class.isAssignableFrom(clazz)) {
                    injectors.add(clazz.getDeclaredConstructor().newInstance());
                } else {
                    LOGGER.warn("Not a injector candidate {}", className);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.warn("Unable to load class {} as an injector", className);
            }
        }
        return injectors;
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
                new OutgoingMessageProcessor(networkConfiguration, messageSerializer),
                interceptors
        );

        return connection;
    }
}
