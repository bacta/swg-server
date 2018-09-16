package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.ClientVerifyAndLockNameRequest;
import io.bacta.game.message.ClientVerifyAndLockNameResponse;
import io.bacta.game.name.NameErrors;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = ClientVerifyAndLockNameRequest.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ClientVerifyAndLockNameRequestController implements GameNetworkMessageController<GameRequestContext, ClientVerifyAndLockNameRequest> {
    @Override
    public void handleIncoming(GameRequestContext context, ClientVerifyAndLockNameRequest message) throws Exception {
        if (message.getTemplateName().isEmpty()) {
            LOGGER.error("Empty template name was sent with verify and lock name request.");

            final ClientVerifyAndLockNameResponse response = new ClientVerifyAndLockNameResponse(
                    message.getCharacterName(), NameErrors.NO_TEMPLATE);
            context.sendMessage(response);
            return;
        }

        //TODO: Check if the players game features allow them to create this species.
        //NameErrors.NOT_AUTHORIZED_FOR_SPECIES

        //TODO: Check if the avatar is allowed to create a character.
        //NameErrors.CANT_CREATE_AVATAR

        //TODO: Verify static tests: length, character set, etc.

        //TODO: Check if any existing names
        //NameErrors.IN_USE

        final ClientVerifyAndLockNameResponse response = new ClientVerifyAndLockNameResponse(
                message.getCharacterName(), NameErrors.APPROVED);

        context.sendMessage(response);
    }
}

