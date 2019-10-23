package io.bacta.game.controllers;

import io.bacta.game.message.SelectCharacter;
import io.bacta.game.player.CharacterSelectionService;
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
@MessageHandled(handles = SelectCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class SelectCharacterController implements GameNetworkMessageController<SoeRequestContext, SelectCharacter> {
    private final CharacterSelectionService service;

    @Inject
    public SelectCharacterController(CharacterSelectionService service) {
        this.service = service;
    }

    @Override
    public void handleIncoming(SoeRequestContext context, SelectCharacter message) throws Exception {
        this.service.selectCharacter(context, message.getId());
    }
}

