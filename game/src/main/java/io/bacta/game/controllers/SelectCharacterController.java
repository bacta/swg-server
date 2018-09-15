package io.bacta.game.controllers;

import io.bacta.game.message.*;
import io.bacta.shared.tre.math.Vector;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = SelectCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class SelectCharacterController implements GameNetworkMessageController<SelectCharacter> {
    @Override
    public void handleIncoming(SoeConnection connection, SelectCharacter message) throws Exception {
        // Verify Account Ownership

        // Start Scene
        final CmdStartScene start = new CmdStartScene(
                false,
                1000000, //character.getNetworkId(),
                "terrain/tatooine.trn", //scene.getTerrainFileName(),
                new Vector(), //character.getTransformObjectToWorld().getPositionInParent(),
                0, //character.getObjectFrameKInWorld().theta(),
                "object/creature/player/shared_human_male.iff", //character.getSharedTemplate().getResourceName(),
                0,
                0);

        connection.sendMessage(start);

        // Send Server Time
        final ServerTimeMessage serverTimeMessage = new ServerTimeMessage(0);
        connection.sendMessage(serverTimeMessage);

        //TODO: Read the weather update interval from either the config, or a weather service directly.
        //This message just tells the client how often to check for new weather.
        final ParametersMessage parametersMessage = new ParametersMessage(900); //seconds
        connection.sendMessage(parametersMessage);

        // Send Guild Updates

        // Send Character Baselines
        final SceneCreateObjectByCrc msg = new SceneCreateObjectByCrc(1000000, "object/creature/player/shared_human_male.iff");
        connection.sendMessage(msg);

        // Ghost object?
        final SceneCreateObjectByCrc msg2 = new SceneCreateObjectByCrc(1000001,  "object/player/shared_player.iff");
        connection.sendMessage(msg2);

        final UpdateContainmentMessage ucm = new UpdateContainmentMessage(1000000, 1000001, 4);
        connection.sendMessage(ucm);

        final SceneEndBaselines endBaselines2 = new SceneEndBaselines(1000001);
        connection.sendMessage(endBaselines2);

        final SceneEndBaselines endBaselines = new SceneEndBaselines(1000000);
        connection.sendMessage(endBaselines);
    }
}

