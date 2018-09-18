package io.bacta.login.server.listener;

import io.bacta.login.server.LoginServerProperties;
import io.bacta.soe.network.udp.SoeTransceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
public class StartupListener implements ApplicationListener<ApplicationStartedEvent> {

    private final SoeTransceiver soeTransceiver;
    private final LoginServerProperties properties;

    @Inject
    public StartupListener(final SoeTransceiver soeTransceiver, final LoginServerProperties properties) {
        this.soeTransceiver = soeTransceiver;
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        soeTransceiver.start("Login", properties.getBindAddress(), properties.getBindPort());
    }
}
