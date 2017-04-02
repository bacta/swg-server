package com.ocdsoft.bacta.swg.login;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.swg.protocol.io.udp.BaseNetworkConfiguration;
import com.ocdsoft.bacta.swg.protocol.io.udp.NetworkConfiguration;

import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Singleton
public final class LoginNetworkConfiguration extends BaseNetworkConfiguration implements NetworkConfiguration {

    @Inject
    public LoginNetworkConfiguration(final BactaConfiguration configuration) throws UnknownHostException {
        super(configuration, "Bacta/LoginServer");
    }
}
