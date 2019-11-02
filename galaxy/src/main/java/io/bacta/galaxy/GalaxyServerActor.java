package io.bacta.galaxy;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GalaxyServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyServerActor.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SpringAkkaExtension ext;

    @Inject
    public GalaxyServerActor(final SpringAkkaExtension ext) {
        this.ext = ext;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Galaxy Server starting");

        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        super.preStart();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, this::memberUp)
                .match(ClusterEvent.UnreachableMember.class, this::unreachableMember)
                .match(ClusterEvent.MemberRemoved.class, this::memberRemoved)
                .matchAny(this::unhandledMessage)
                .build();
    }

    private void memberUp(ClusterEvent.MemberUp message) {
        log.info("Member is Up: {} with Roles {}", message.member(), message.member().getRoles());
    }

    private void unreachableMember(ClusterEvent.UnreachableMember message) {
        log.info("Member is Unreachable: {} with Roles {}", message.member(), message.member().getRoles());
    }

    private void memberRemoved(ClusterEvent.MemberRemoved message) {
        log.info("Member is removed: {} with Roles {}", message.member(), message.member().getRoles());
    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }

}
