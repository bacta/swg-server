package io.bacta.login.server.actor;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.shared.MemberConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope("prototype")
public class LoginSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), LoginSupervisor.class.getSimpleName());
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SpringAkkaExtension ext;
    private final LoginServerProperties properties;
    private final ApplicationContext context;

    @Inject
    public LoginSupervisor(final SpringAkkaExtension ext, final LoginServerProperties properties, final ApplicationContext context) {
        this.ext = ext;
        this.properties = properties;
        this.context = context;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Login starting");
        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);
        super.preStart();

        context().actorOf(ext.props(LoginTransceiverActor.class), "transceiver");
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
                    if (mUp.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                })
                .match(ClusterEvent.UnreachableMember.class, mDown -> {
                    log.info("Member is Unreachable: {} with Roles {}", mDown.member(), mDown.member().getRoles());
                    if (mDown.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                })
                .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                    log.info("Member is removed: {} with Roles {}", mRemoved.member(), mRemoved.member().getRoles());
                    if (mRemoved.member().hasRole(MemberConstants.CONNECTION_SERVER)) {

                    }
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
