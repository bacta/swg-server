package com.ocdsoft.bacta.swg.login;

import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.conf.NetworkConfigImpl;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.SoeNetworkConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Configuration
@ConfigurationProperties
public final class LoginNetworkConfiguration extends NetworkConfigImpl implements SoeNetworkConfiguration {

    @Inject
    public LoginNetworkConfiguration(final BactaConfiguration configuration) throws UnknownHostException {
        super(configuration, "Bacta/LoginServer");
    }
}
