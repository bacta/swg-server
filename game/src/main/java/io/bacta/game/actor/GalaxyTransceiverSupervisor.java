package io.bacta.game.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.game.config.GameServerConfiguration;
import io.bacta.game.config.GameServerProperties;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.udp.SoeTransceiver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class GalaxyTransceiverSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyTransceiverSupervisor.class.getSimpleName());
    private final SoeTransceiver soeTransceiver;
    private final GameServerProperties gameServerProperties;

    @Inject
    public GalaxyTransceiverSupervisor(final SoeTransceiver soeTransceiver, final GameServerProperties gameServerConfiguration) {
        this.soeTransceiver = soeTransceiver;
        this.gameServerProperties = gameServerConfiguration;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Starting Up");
        soeTransceiver.start("GalaxyTransceiver", gameServerProperties.getBindAddress(), gameServerProperties.getBindPort());
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        soeTransceiver.stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
