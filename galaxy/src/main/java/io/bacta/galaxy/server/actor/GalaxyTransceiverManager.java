package io.bacta.galaxy.server.actor;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.soe.SimpleTransceiverManager;
import io.bacta.soe.network.udp.SoeTransceiver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class GalaxyTransceiverManager extends SimpleTransceiverManager {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyTransceiverManager.class.getSimpleName());

    @Inject
    public GalaxyTransceiverManager(SoeTransceiver soeTransceiver) {
        super(soeTransceiver);
    }

    @Override
    public Receive createReceive() {
        return getBasicBuilder()
                .build();
    }
}
