package com.ocdsoft.bacta.swg.login;

import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.conf.NetworkConfigurationImpl;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfigurationImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Configuration
@ConfigurationProperties
public final class LoginNetworkConfiguration extends NetworkConfigurationImpl implements SoeNetworkConfigurationImpl {

    @Inject
    public LoginNetworkConfiguration(final BactaConfiguration configuration) throws UnknownHostException {
        super(configuration, "Bacta/LoginServer");
    }
}
