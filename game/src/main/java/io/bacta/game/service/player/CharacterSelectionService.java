package io.bacta.game.service.player;

import io.bacta.game.message.CmdStartScene;
import io.bacta.game.message.ParametersMessage;
import io.bacta.game.message.ServerTimeMessage;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.scene.SceneService;
import io.bacta.game.service.guild.GuildService;
import io.bacta.game.service.object.ServerObjectService;
import io.bacta.soe.context.SoeRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Collections;

@Slf4j
@Component
@Scope("prototype")
public final class CharacterSelectionService {

    private final GuildService guildService;
    private final ServerObjectService serverObjectService;
    private final SceneService sceneService;

    @Inject
    public CharacterSelectionService(GuildService guildService,
                                     ServerObjectService serverObjectService,
                                     SceneService sceneService) {
        this.guildService = guildService;
        this.serverObjectService = serverObjectService;
        this.sceneService = sceneService;
    }

    public void selectCharacter(SoeRequestContext context, long networkId) {
        LOGGER.info("Selecting character with network id {}", networkId);
        //Send appropriate messages.

        final CreatureObject playerCreature = serverObjectService.get(networkId);

        // Verify Account Ownership
        //TODO: What if nothing with that id exists?
        //TODO: What if that creature doesn't belong to the account that signed in?

        //ActorRef scene = sceneService.getScene(playerCreature.getSceneId());
        //scene.tell(new LoadPlayerCharacter(networkId), ActorRef.noSender());

        // Start Scene
        final CmdStartScene start = new CmdStartScene(
                playerCreature.getNetworkId(),
                "terrain/naboo.trn", //scene.getTerrainFileName(),
                playerCreature.getTransformObjectToWorld().getPositionInParent(),
                playerCreature.getObjectFrameKInWorld().theta(),
                playerCreature.getSharedTemplate().getResourceName(),
                Instant.now().toEpochMilli(),
                (int) Instant.now().getEpochSecond(),
                false);

        context.sendMessage(start);

        // Send Server Time
        final ServerTimeMessage serverTimeMessage = new ServerTimeMessage(0);
        context.sendMessage(serverTimeMessage);

        //TODO: Read the weather update interval from either the config, or a weather service directly.
        //This message just tells the client how often to check for new weather.
        final ParametersMessage parametersMessage = new ParametersMessage(900); //seconds
        context.sendMessage(parametersMessage);

        //Send guild object to client.
        guildService.sendTo(context);

        //Send creates and baselines for all the creatures objects.
        playerCreature.sendCreateAndBaselinesTo(Collections.singleton(context));
    }


    /**
     *         //Check if they are allowed to skip tutorial.
     *         //Check if they are allowed to create jedi.
     *         //Check if they are allowed to create regular character.
     *         //Check if they are already creating a character.
     *         //Check if they have created a character while this connection was active.
     *         //Check if their name is set to empty once again (after verifyandlock was done).
     *
     *         //Truncate biography to 1024 characters.
     *         String biography = message.getBiography();
     *
     *         if (biography.length() > 1024)
     *             biography = biography.substring(0, 1024);
     *
     *         //Check with login server to make sure that they can still create a character:
     *         //that limits have not been exceeded.
     *
     *
     *         //character creation handle create char
     *         //check if they are already creating a character on their account NameErrors.RETRY
     *         //check if they are creating characters too rapidly NameErrors.TOO_FAST
     *         //check if starting location is valid. character_create_failed_bad_location
     *
     *         //create the character
     *         //create the player
     *         //create the inventories and set all appearances
     *
     *         //save to database
     *
     *         //tell login server that the character was created
     *         //When login responds with acknowledgement, send the ClientCreateCharacterSuccess message.
     *         //This will cause the client to login with the new character?
     */
}
