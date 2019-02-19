package io.bacta.game.actor.universe;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.shared.MemberConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UniverseActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), UniverseActor.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SpringAkkaExtension ext;

    public UniverseActor(final SpringAkkaExtension ext) {
        this.ext = ext;
    }


    @Override
    public void preStart() throws Exception {
        log.info("Universe starting");

        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        super.preStart();

        getContext().actorOf(ext.props(GalaxySupervisor.class), ActorConstants.GALAXY_SUPERVISOR);
        getContext().actorOf(ext.props(ChatSupervisor.class), ActorConstants.CHAT_SUPERVISOR);
        getContext().actorOf(ext.props(ClientSupervisor.class), ActorConstants.CLIENT_SUPERVISOR);

        //TODO: Start udp/connection listener.
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

    private void memberUp(ClusterEvent.MemberUp message){
        log.info("Member is Up: {} with Roles {}", message.member(), message.member().getRoles());

        if (message.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

        }
    }

    private void unreachableMember(ClusterEvent.UnreachableMember message) {
        log.info("Member is Unreachable: {} with Roles {}", message.member(), message.member().getRoles());

        if (message.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

        }
    }

    private void memberRemoved(ClusterEvent.MemberRemoved message) {
        log.info("Member is removed: {} with Roles {}", message.member(), message.member().getRoles());

        if (message.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

        }
    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }
}
