package io.bacta.soe;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import io.bacta.shared.message.SoeTransceiverStart;
import io.bacta.shared.message.SoeTransceiverStarted;
import io.bacta.shared.message.SoeTransceiverStop;
import io.bacta.shared.message.SoeTransceiverStopped;
import io.bacta.soe.network.udp.SoeTransceiver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class SimpleTransceiverManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SimpleTransceiverManager.class.getSimpleName());
    protected final SoeTransceiver soeTransceiver;

    @Inject
    public SimpleTransceiverManager(final SoeTransceiver soeTransceiver) {
        this.soeTransceiver = soeTransceiver;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Starting Up");

        super.preStart();
    }

    @Override
    public Receive createReceive() {
        return getBasicBuilder().build();
    }

    protected ReceiveBuilder getBasicBuilder() {
        return receiveBuilder()
                .match(SoeTransceiverStart.class, start -> {
                    log.info("Starting Transceiver");
                    soeTransceiver.start(start.getName(), start.getBindAddress(), start.getBindPort());
                    getSender().tell(new SoeTransceiverStarted(soeTransceiver.getAddress()), getSelf());
                })
                .match(SoeTransceiverStop.class, start -> {
                    log.info("Stopping Transceiver");
                    soeTransceiver.stop();
                    getSender().tell(new SoeTransceiverStopped(), getSelf());
                });
    }

}
