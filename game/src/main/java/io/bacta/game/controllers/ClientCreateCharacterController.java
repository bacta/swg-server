package io.bacta.game.controllers;

import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.message.ClientCreateCharacterSuccess;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = ClientCreateCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ClientCreateCharacterController implements GameNetworkMessageController<ClientCreateCharacter> {
    @Override
    public void handleIncoming(SoeConnection connection, ClientCreateCharacter message) throws Exception {
        //Check if they are allowed to skip tutorial.
        //Check if they are allowed to create jedi.
        //Check if they are allowed to create regular character.
        //Check if they are already creating a character.
        //Check if they have created a character while this connection was active.
        //Check if their name is set to empty once again (after verifyandlock was done).

        //Truncate biography to 1024 characters.
        String biography = message.getBiography();

        if (biography.length() > 1024)
            biography = biography.substring(0, 1024);

        //Check with login server to make sure that they can still create a character:
        //that limits have not been exceeded.


        //character creation handle create char
        //check if they are already creating a character on their account NameErrors.RETRY
        //check if they are creating characters too rapidly NameErrors.TOO_FAST
        //check if starting location is valid. character_create_failed_bad_location

        //create the character
        //create the player
        //create the inventories and set all appearances

        //save to database

        //tell login server that the character was created
        //When login responds with acknowledgement, send the ClientCreateCharacterSuccess message.
        //This will cause the client to login with the new character?

        final ClientCreateCharacterSuccess success = new ClientCreateCharacterSuccess(1000000001165L);
        connection.sendMessage(success);
    }
}

