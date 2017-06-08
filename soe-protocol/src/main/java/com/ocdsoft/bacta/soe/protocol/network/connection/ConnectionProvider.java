package com.ocdsoft.bacta.soe.protocol.network.connection;

import com.ocdsoft.bacta.soe.protocol.network.io.udp.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
public class ConnectionProvider {

    private final SoeNetworkConfiguration networkConfiguration;
    private final Collection<String> whitelistedAddresses;
    private final GameNetworkMessageSerializer messageSerializer;

    @Inject
    public ConnectionProvider(final SoeNetworkConfiguration networkConfiguration,
                              final GameNetworkMessageSerializer messageSerializer) {
        this.networkConfiguration = networkConfiguration;
        this.messageSerializer = messageSerializer;
        this.whitelistedAddresses = networkConfiguration.getTrustedClients();
    }

    public SoeUdpConnection newInstance(final InetSocketAddress sender) {

        SoeUdpConnection connection = new SoeUdpConnection(
                networkConfiguration, sender,
                messageSerializer,
                null);

        if(whitelistedAddresses != null && whitelistedAddresses.contains(sender.getHostString())) {
            connection.addRole(ConnectionRole.WHITELISTED);
            log.debug("Whitelisted address connected: " + sender.getHostString());
        }

        return connection;
    }
}
