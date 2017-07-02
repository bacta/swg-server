package com.ocdsoft.bacta.soe.network.udp;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

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
//            SoeUdpConnection soe = new SoeUdpConnection(networkConfiguration, address, ConnectionState.LINKDEAD, messageSerializer, connectCallback);
//            soe.setId(random.nextInt());
//
//            if(whitelistedAddresses != null && whitelistedAddresses.contains(soe.getRemoteAddress().getAddress().getHostAddress())) {
//                soe.addRole(ConnectionRole.WHITELISTED);
//                LOGGER.debug("Whitelisted address connected: " + soe.getRemoteAddress().getAddress().getHostAddress());
//            }
//
//            accountCache.put(soe.getRemoteAddress(), soe);
//
//            LOGGER.debug("{} soe to {} now has {} total connected clients.",
//                    soe.getClass().getSimpleName(),
//                    soe.getRemoteAddress(),
//                    accountCache.getConnectionCount());
//
//            if(!networkConfiguration.isDisableInstrumentation()) {
//                mBeanServer.registerMBean(soe, soe.getBeanName());
//            }
//
//            return soe;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
