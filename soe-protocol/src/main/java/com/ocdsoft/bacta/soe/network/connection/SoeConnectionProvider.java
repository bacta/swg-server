package com.ocdsoft.bacta.soe.network.connection;

import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
public class SoeConnectionProvider {

    private final SoeNetworkConfiguration networkConfiguration;
    private final Collection<String> whitelistedAddresses;
    private final GameNetworkMessageSerializer messageSerializer;

    public SoeConnectionProvider(final SoeNetworkConfiguration networkConfiguration,
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
            LOGGER.debug("Whitelisted address connected: " + sender.getHostString());
        }

        return connection;
    }
}
