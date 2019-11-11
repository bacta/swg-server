package io.bacta.connection.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.config.ConnectionChannelProperties;
import io.bacta.connection.network.PingChannel;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.network.channel.SoeMessageChannel;
import io.bacta.soe.network.message.SwgTerminateMessage;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameClientMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ConnectionTransceiverActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ConnectionTransceiverActor.class.getSimpleName());
    private final SpringAkkaExtension ext;

    private final SoeMessageChannel soeMessageChannel;
    private final PingChannel pingChannel;
    private final ConnectionChannelProperties config;

    private final AkkaGameNetworkMessageRelay processor;

    @Inject
    public ConnectionTransceiverActor(final SpringAkkaExtension ext,
                                      @Qualifier("ServerChannel") final SoeMessageChannel soeMessageChannel,
                                      final PingChannel pingChannel,
                                      final ConnectionChannelProperties config,
                                      final AkkaGameNetworkMessageRelay processor) {
        this.ext = ext;
        this.soeMessageChannel = soeMessageChannel;
        this.pingChannel = pingChannel;
        this.processor = processor;
        this.config = config;
    }

    @Override
    public void preStart() {
        log.info("Transceiver starting");
        this.processor.setTransceiverRef(getSelf());
    }

    @Override
    public void postStop() {
        log.info("Transceiver stopping");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GameClientMessage.class, this::handleGameClientMessage)
                .match(SwgTerminateMessage.class, this::handleTerminate)
                .matchAny(this::unhandledMessage)
                .build();
    }

    private void handleGameClientMessage(GameClientMessage gameClientMessage) {

    }

    private void handleTerminate(SwgTerminateMessage terminateMessage) {

    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }

}
