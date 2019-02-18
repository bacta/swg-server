package io.bacta.game.listener;

import io.bacta.game.GameServerProperties;
import io.bacta.game.ping.PingTransceiver;
import io.bacta.soe.network.udp.SoeTransceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Slf4j
@Profile("Galaxy")
public class StartupListener implements ApplicationListener<ApplicationStartedEvent> {

    private final SoeTransceiver soeTransceiver;
    private final PingTransceiver pingTransceiver;
    private final GameServerProperties properties;

    @Inject
    public StartupListener(final SoeTransceiver soeTransceiver, final PingTransceiver pingTransceiver, final GameServerProperties properties) {
        this.soeTransceiver = soeTransceiver;
        this.pingTransceiver = pingTransceiver;
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        soeTransceiver.start("Game", properties.getBindAddress(), properties.getBindPort());
        pingTransceiver.start();
    }
}
