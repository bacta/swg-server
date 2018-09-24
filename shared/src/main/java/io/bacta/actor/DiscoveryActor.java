package io.bacta.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.shared.MemberConstants;

import javax.inject.Inject;
import java.util.Optional;

public class DiscoveryActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), DiscoveryActor.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SpringAkkaExtension ext;

    @Inject
    public DiscoveryActor(final SpringAkkaExtension ext) {
        this.ext = ext;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Discovery starting");
        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        super.preStart();
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
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    log.info("Member is Up: {} with Roles {}", mUp.member(), mUp.member().getRoles());
                    if(mUp.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                    if(mUp.member().hasRole(MemberConstants.LOGIN_SERVER)) {
                        ActorRef login = getContext().actorFor("akka.tcp://Galaxy@0.0.0.0:2561/user/login");
                        login.tell("Hey there Login, it's Game Server", getSelf());
                    }
                })
                .match(ClusterEvent.UnreachableMember.class, mDown -> {
                    log.info("Member is Unreachable: {} with Roles {}", mDown.member(), mDown.member().getRoles());
                    if(mDown.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                })
                .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                    log.info("Member is removed: {} with Roles {}", mRemoved.member(), mRemoved.member().getRoles());
                    if(mRemoved.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                })

                .match(String.class, s -> {
                    log.info("Received STRING message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
