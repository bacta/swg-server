package io.bacta.game.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.galaxy.message.GalaxyServerOnline;
import io.bacta.game.config.GameServerProperties;
import io.bacta.shared.MemberConstants;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.Optional;

@Component
@Scope("prototype")
public class GalaxySupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxySupervisor.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;

    @Inject
    public GalaxySupervisor(final SpringAkkaExtension ext, final GameServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Galaxy starting");
        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        super.preStart();
        getContext().actorOf(ext.props(GameDataSupervisor.class), "data");
        getContext().actorOf(ext.props(ObjectSupervisor.class), "object");
        getContext().actorOf(ext.props(ZoneSupervisor.class), "zone");
        getContext().actorOf(ext.props(ConnectionSupervisor.class), "connection");
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
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    log.info("Member is Up: {} with Roles {}", mUp.member(), mUp.member().getRoles());
                    if(mUp.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                    if(mUp.member().hasRole(MemberConstants.LOGIN_SERVER)) {
                        ActorRef login = getContext().actorFor("akka.tcp://Galaxy@0.0.0.0:2561/user/login");
                        login.tell(new GalaxyServerOnline(new InetSocketAddress(properties.getBindAddress(), properties.getBindPort())), getSelf());
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
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
