package io.bacta.galaxy.server.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.message.ConnectionServerReady;
import io.bacta.connection.server.ConnectionServerApplication;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.engine.utils.SenderUtil;
import io.bacta.galaxy.message.GalaxyServerOnline;
import io.bacta.galaxy.server.config.GalaxyServerProperties;
import io.bacta.login.server.LoginServerApplication;
import io.bacta.shared.message.SoeTransceiverStart;
import io.bacta.shared.message.SoeTransceiverStarted;
import io.bacta.zone.server.ZoneServerApplication;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class GalaxyManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyManager.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GalaxyServerProperties properties;
    private ActorRef transRef;

    @Inject
    public GalaxyManager(final SpringAkkaExtension ext, final GalaxyServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Starting up");
        getContext().actorOf(ext.props(ClusterManager.class), "clusterManager");
        transRef = getContext().actorOf(ext.props(GalaxyTransceiverManager.class), "transceiverManager");
        transRef.tell(new SoeTransceiverStart(properties.getGalaxyName(), properties.getBindAddress(), properties.getBindPort()), getSelf());

        if(properties.isConnectionServer()) {
            new SpringApplicationBuilder(ConnectionServerApplication.class)
                    .bannerMode(Banner.Mode.CONSOLE)
                    .logStartupInfo(true)
                    .web(false)
                    .properties("spring.config.name=connection")
                    .run();
        }

        if(properties.isLoginServer()) {
            new SpringApplicationBuilder(LoginServerApplication.class)
                    .bannerMode(Banner.Mode.CONSOLE)
                    .logStartupInfo(false)
                    .properties("spring.config.name=login")
                    .run();
        }

        if(properties.getZoneServers() != null) {
            properties.getZoneServers().forEach(zoneServer -> {
                new SpringApplicationBuilder(ZoneServerApplication.class)
                        .bannerMode(Banner.Mode.CONSOLE)
                        .logStartupInfo(false)
                        .properties("spring.config.name=" + zoneServer.getName())
                        .run();
            });
        }

        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // When Transceiver is ready to receive messages
                .match(SoeTransceiverStarted.class, s -> SenderUtil.isPrivileged(getSender()), transceiverStarted -> {
                    transRef.tell(new GalaxyServerOnline(transceiverStarted.getAddress()), getSelf());
                })
                // When a connection server has registered and is ready to receive traffic
                .match(ConnectionServerReady.class, s -> SenderUtil.isPrivileged(getSender()), connReady -> {
                    //this.transceiverAddress = started.getAddress();
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
