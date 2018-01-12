package io.bacta.login.server.service;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.SoeConnectionFactory;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.service.InternalMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by kyle on 7/7/2017.
 */

@Component
@Slf4j
public class LoginInternalMessageService implements InternalMessageService {

    private SoeConnectionCache connectionCache;
    private SoeConnectionFactory connectionProvider;

    @Inject
    public LoginInternalMessageService(final SoeNetworkConfiguration networkConfiguration) {

    }

    @Override
    public void setConnectionObjects(final SoeConnectionCache connectionCache, final SoeConnectionFactory connectionProvider) {
        this.connectionCache = connectionCache;
        this.connectionProvider = connectionProvider;
    }



    @Override
    public SoeUdpConnection getSessionServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getGalaxyServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getConnectionServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getLoginServerConnection() {
        return null;
    }


}
