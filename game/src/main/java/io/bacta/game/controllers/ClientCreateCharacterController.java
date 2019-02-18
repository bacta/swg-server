package io.bacta.game.controllers;

import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.service.player.creation.CharacterCreationService;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@MessageHandled(handles = ClientCreateCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ClientCreateCharacterController implements GameNetworkMessageController<SoeRequestContext, ClientCreateCharacter> {
    private final CharacterCreationService creationService;

    @Inject
    public ClientCreateCharacterController(CharacterCreationService creationService) {
        this.creationService = creationService;
    }

    @Override
    public void handleIncoming(SoeRequestContext context, ClientCreateCharacter message) throws Exception {
        this.creationService.createCharacter(context, message);
    }
}

