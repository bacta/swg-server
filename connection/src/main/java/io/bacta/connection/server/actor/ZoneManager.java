package io.bacta.connection.server.actor;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
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
public class ZoneManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ZoneManager.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final Set<Member> onlineZoneServers;

    public ZoneManager() {
        onlineZoneServers = new HashSet<>();
    }

    //subscribe to cluster changes
    @Override
    public void preStart() {
        log.info("Starting Up");
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                MemberEvent.class, UnreachableMember.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    log.info("Member is Up: {} with Roles {}", mUp.member(), mUp.member().getRoles());
                    if(mUp.member().hasRole(MemberConstants.ZONE_SERVER)) {
                        registerZoneServer(mUp.member());
                    }
                })
                .match(UnreachableMember.class, mDown -> {
                    log.info("Member is Unreachable: {} with Roles {}", mDown.member(), mDown.member().getRoles());
                    if(mDown.member().hasRole(MemberConstants.ZONE_SERVER)) {
                        unregisterZoneServer(mDown.member());
                    }
                })
                .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                    log.info("Member is removed: {} with Roles {}", mRemoved.member(), mRemoved.member().getRoles());
                    if(mRemoved.member().hasRole(MemberConstants.ZONE_SERVER)) {
                        unregisterZoneServer(mRemoved.member());
                    }
                })
                .build();
    }

    private void registerZoneServer(Member member) {
        onlineZoneServers.add(member);
    }

    private void unregisterZoneServer(Member member) {
        onlineZoneServers.remove(member);
    }
}