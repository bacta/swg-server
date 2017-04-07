package com.ocdsoft.bacta.soe.protocol.network.io.udp;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by kburkhardt on 2/15/14.
 */

@Slf4j
public final class SoeProtocolPipeline {

    @Inject
    public SoeProtocolPipeline() {




    }

//    public final SoeUdpConnection createOutgoingConnection(final InetSocketAddress address, final Consumer<SoeUdpConnection> connectCallback) throws RuntimeException {
//
//        try {
//            SoeUdpConnection connection = new SoeUdpConnection(networkConfiguration, address, ConnectionState.LINKDEAD, messageSerializer, connectCallback);
//            connection.setId(random.nextInt());
//
//            if(whitelistedAddresses != null && whitelistedAddresses.contains(connection.getRemoteAddress().getAddress().getHostAddress())) {
//                connection.addRole(ConnectionRole.WHITELISTED);
//                LOGGER.debug("Whitelisted address connected: " + connection.getRemoteAddress().getAddress().getHostAddress());
//            }
//
//            accountCache.put(connection.getRemoteAddress(), connection);
//
//            LOGGER.debug("{} connection to {} now has {} total connected clients.",
//                    connection.getClass().getSimpleName(),
//                    connection.getRemoteAddress(),
//                    accountCache.getConnectionCount());
//
//            if(!networkConfiguration.isDisableInstrumentation()) {
//                mBeanServer.registerMBean(connection, connection.getBeanName());
//            }
//
//            return connection;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
