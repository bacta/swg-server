package io.bacta.connection.server.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.message.ConnectionServerReady;
import io.bacta.engine.utils.SenderUtil;
import io.bacta.shared.MemberConstants;
import io.bacta.shared.message.SoeTransceiverStarted;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Scope("prototype")
public class GalaxyDelegate extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Cluster cluster = Cluster.get(getContext().system());
    private Member galaxyServer;
    private InetSocketAddress transceiverAddress;


    public GalaxyDelegate() {

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
                        registerGalaxy(mUp.member());
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
                .match(SoeTransceiverStarted.class,  s -> SenderUtil.isPrivileged(getSender()), started -> {
                    this.transceiverAddress = started.getAddress();
                    messageGalaxyManager(new ConnectionServerReady(started.getAddress()));
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

    private void messageGalaxyManager(Object message) {
        Address address = galaxyServer.address();
        ActorSelection selection = getContext().getSystem().actorSelection(address.toString() + "/user/galaxyManager");
        selection.tell(message, getContext().self());
    }

    private void unregisterGalaxy(Member member) {
        galaxyServer = null;
    }

    private void registerGalaxy(Member member) {
        galaxyServer = member;
    }
}
