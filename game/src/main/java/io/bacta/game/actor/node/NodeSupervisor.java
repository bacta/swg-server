package io.bacta.game.actor.node;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.game.actor.scene.SceneActor;
import io.bacta.game.scene.SceneService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NodeSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), NodeSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final SceneService sceneService;
    private final List<Address> availableMembers;
    private final List<Address> availableGalaxyServers;


    @Inject
    public NodeSupervisor(final SpringAkkaExtension ext, final GameServerProperties properties, final SceneService sceneService) {
        this.ext = ext;
        this.properties = properties;
        this.sceneService = sceneService;
        this.availableMembers = new ArrayList<>();
        this.availableGalaxyServers = new ArrayList<>();
    }

    @Override
    public void preStart() throws Exception {

        log.info("Scene Supervisor starting");
        super.preStart();

        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        //List<GameServerProperties.Scene> sceneList = properties.getScenes();

        properties.getScenes()
                .parallelStream()
                .forEach(this::createSceneActor);
//
//        for(GameServerProperties.Scene server : sceneList) {
//            ActorRef sceneActor = getContext().actorOf(ext.props(SceneActor.class), server.getName());
//            sceneActor.tell(server, getSelf());
//            sceneService.addScene(server.getName(), sceneActor);
//        }
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        super.preRestart(reason, message);

        List<GameServerProperties.Scene> sceneList = properties.getScenes();

        for(GameServerProperties.Scene server : sceneList) {
            sceneService.removeScene(server.getName());
        }
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, this::memberUp)
                .match(ClusterEvent.UnreachableMember.class, this::memberDown)
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

    private ActorRef createSceneActor(GameServerProperties.Scene scene) {
        final ActorRef sceneActor = getContext().actorOf(ext.props(SceneActor.class), scene.getName());
        sceneActor.tell(scene, getSelf());
        sceneService.addScene(scene.getName(), sceneActor);
        return sceneActor;
    }

    private void memberUp(ClusterEvent.MemberUp mUp) {
        Address mAddress = mUp.member().address();
        log.debug("SceneSupervisor detected new MemberUp: {}", mAddress);
        if(mUp.member().hasRole("GalaxyServer")) {
            log.debug("SceneSupervisor detected new GalaxyServer: {}", mAddress);
            availableGalaxyServers.add(mAddress);
            ActorRef newSceneSupervisorRef = getContext().actorFor(mAddress.toString() + "/user/" + ActorConstants.GALAXY_SCENE_SUPERVISOR);
            newSceneSupervisorRef.tell(new NodeSceneList(properties.getScenes()), getSelf());
        } else {
            log.debug("SceneSupervisor detected new node: {}", mAddress);
            availableMembers.add(mAddress);
        }
    }

    private void memberDown(ClusterEvent.UnreachableMember mDown) {
        Address mAddress = mDown.member().address();
        log.debug("SceneSupervisor detected new MemberDown: {}", mAddress);
        if(mDown.member().hasRole("GalaxyServer")) {
            availableGalaxyServers.remove(mAddress);
        } else {
            availableMembers.remove(mAddress);
        }
    }
}
