package io.bacta.soe.context;

import akka.actor.ActorRef;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.message.SwgTerminateMessage;
import io.bacta.soe.network.message.TerminateReason;
import io.bacta.soe.network.relay.GameClientMessage;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import static org.checkerframework.com.github.javaparser.utils.Utils.assertNotNull;

@Getter
public class AkkaSoeSessionContext implements SoeSessionContext {

    private final int connectionId;
    private final ActorRef connectionServerRef;
    private ActorRef sceneServerRef;
    private final Set<ConnectionRole> roles;

    public AkkaSoeSessionContext(final int connectionId, final ActorRef connectionServerRef) {
        assertNotNull(connectionServerRef);
        this.connectionId = connectionId;
        this.connectionServerRef = connectionServerRef;
        this.sceneServerRef = null;
        this.roles = new HashSet<>();
    }

    @Override
    public void sendMessage(GameNetworkMessage message) {
        connectionServerRef.tell(new GameClientMessage(connectionId, message), connectionServerRef);
    }

    @Override
    public void addRole(ConnectionRole authenticated) {
        roles.add(authenticated);
    }

    @Override
    public void terminate(TerminateReason reason, boolean silent) {
        connectionServerRef.tell(new SwgTerminateMessage(reason, silent, connectionId), connectionServerRef);
    }

    public void setSceneServerRef(final ActorRef sceneActor) {
        this.sceneServerRef = sceneActor;
    }
}
