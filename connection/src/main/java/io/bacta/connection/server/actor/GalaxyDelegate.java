package io.bacta.connection.server.actor;

import akka.actor.AbstractActor;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.shared.MemberConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Scope("prototype")
public class GalaxyDelegate extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final Set<Member> galaxyServers;

    public GalaxyDelegate() {
        galaxyServers = new HashSet<>();
    }

    //subscribe to cluster changes
    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    log.info("Member is Up: {} with Roles {}", mUp.member(), mUp.member().getRoles());
                    if(mUp.member().hasRole(MemberConstants.GALAXY_SERVER)) {
                        registerWithGalaxy(mUp.member());
                    }
                })
                .match(ClusterEvent.UnreachableMember.class, mDown -> {
                    log.info("Member is Unreachable: {} with Roles {}", mDown.member(), mDown.member().getRoles());
                    if(mDown.member().hasRole(MemberConstants.GALAXY_SERVER)) {
                        unregisterGalaxy(mDown.member());
                    }
                })
                .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                    log.info("Member is removed: {} with Roles {}", mRemoved.member(), mRemoved.member().getRoles());
                    if(mRemoved.member().hasRole(MemberConstants.GALAXY_SERVER)) {
                        unregisterGalaxy(mRemoved.member());
                    }
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

    private void unregisterGalaxy(Member member) {
        galaxyServers.remove(member);
    }

    private void registerWithGalaxy(Member member) {
        Address address = member.address();
        galaxyServers.add(member);
//        ActorSelection selection = getContext().getSystem().actorSelection(address.toString() + "/user/galaxyManager");
//        selection.tell("Hello", getContext().self());
    }
}
