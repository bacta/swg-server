package io.bacta.game.controllers.command;

import io.bacta.game.command.CommandQueueParameters;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.message.object.AttributeListMessage;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectService;
import io.bacta.game.object.attributes.AttributeListService;
import io.bacta.swg.object.AttributeList;
import io.bacta.swg.util.NetworkId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * When an object is examined or a container opened, the client requests the attributes for the items by sending this
 * command to the server. The parameters are a list of pairs of NetworkId and client revision numbers. The client revision
 * number is used by the client to determine if the attributes need to be updated or not.
 */
@Slf4j
@Component
@QueuesCommand("getattributesbatch")
public class GetAttributesBatchCommandController implements CommandQueueController {
    private final ServerObjectService serverObjectService;
    private final AttributeListService attributeListService;

    @Inject
    public GetAttributesBatchCommandController(ServerObjectService serverObjectService,
                                               AttributeListService attributeListService) {
        this.serverObjectService = serverObjectService;
        this.attributeListService = attributeListService;
    }

    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, CommandQueueParameters params) {
        //This command can contain a list of object ids, and their client revision number (cache).
        //We must loop through them until we run out of params.

        long networkId = params.nextNetworkId();

        while (networkId != NetworkId.INVALID) {
            final int clientRevision = params.nextInt();

            final ServerObject obj = serverObjectService.get(networkId);

            //TODO: This should come from the objvars...
            //final int serverRevision = obj.getAttributeRevision();
            final int serverRevision = -127;

            final AttributeList attributeList = attributeListService.request(actor, obj);
            context.sendMessage(new AttributeListMessage(networkId, attributeList, serverRevision));

            //Get the next network id.
            networkId = params.nextNetworkId();
        }
    }
}
