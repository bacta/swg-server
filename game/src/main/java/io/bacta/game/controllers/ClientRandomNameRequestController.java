package io.bacta.game.controllers;

import io.bacta.game.message.ClientRandomNameRequest;
import io.bacta.game.message.ClientRandomNameResponse;
import io.bacta.game.name.NameErrors;
import io.bacta.game.name.NameGeneratorNotFoundException;
import io.bacta.game.name.NameService;
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
@MessageHandled(handles = ClientRandomNameRequest.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ClientRandomNameRequestController implements GameNetworkMessageController<SoeRequestContext, ClientRandomNameRequest> {
    private final NameService nameService;

    @Inject
    public ClientRandomNameRequestController(NameService nameService) {
        this.nameService = nameService;
    }

    @Override
    public void handleIncoming(SoeRequestContext context, ClientRandomNameRequest message) throws Exception {
        try {
            final String generatorType = nameService.getNameGeneratorTypeForTemplate(message.getCreatureTemplate());
            final String randomName = nameService.generateUniqueRandomName(generatorType);

            final ClientRandomNameResponse response = new ClientRandomNameResponse(
                    message.getCreatureTemplate(),
                    randomName,
                    NameErrors.APPROVED);

            context.sendMessage(response);
        } catch (NameGeneratorNotFoundException ex) {
            LOGGER.error("No name generator found for template {}", ex.getCreatureTemplate());

            final ClientRandomNameResponse response = new ClientRandomNameResponse(
                    message.getCreatureTemplate(), "", NameErrors.NO_TEMPLATE);
            context.sendMessage(response);
        }
    }
}

