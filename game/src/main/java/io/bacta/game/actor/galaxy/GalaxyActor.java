package io.bacta.game.actor.galaxy;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.shared.MemberConstants;
import lombok.Getter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GalaxyActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyActor.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;

    @Getter
    private String name;

    @Inject
    public GalaxyActor(
            final SpringAkkaExtension ext,
            final GameServerProperties properties) {

        this.ext = ext;
        this.properties = properties;

        this.name = properties.getGalaxyName();
    }

    @Override
    public void preStart() throws Exception {
        log.info("Galaxy starting");

        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        super.preStart();

        getContext().actorOf(ext.props(SceneSupervisor.class), ActorConstants.GALAXY_SCENE_SUPERVISOR);
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        super.preRestart(reason, message);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, this::memberUp)
                .match(ClusterEvent.UnreachableMember.class, this::unreachableMember)
                .match(ClusterEvent.MemberRemoved.class, this::memberRemoved)
                .match(String.class, this::stringReceived)
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

    private void stringReceived(String s) {
        log.info("Received String message: {}", s);
    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }
}
