/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.soe.network.connection;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
@Component
@Scope("prototype")
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
                networkConfiguration,
                sender,
                messageSerializer);

        if(whitelistedAddresses != null && whitelistedAddresses.contains(sender.getHostString())) {
            connection.addRole(ConnectionRole.WHITELISTED);
            LOGGER.debug("Whitelisted address connected: " + sender.getHostString());
        }

        return connection;
    }
}
