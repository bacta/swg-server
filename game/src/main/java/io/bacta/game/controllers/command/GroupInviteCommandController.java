package io.bacta.game.controllers.command;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.group.GroupService;
import io.bacta.game.object.ServerObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@QueuesCommand("groupinvite")
public class GroupInviteCommandController implements CommandQueueController {
    private final GroupService group;

    @Inject
    public GroupInviteCommandController(GroupService group) {
        this.group = group;
    }

    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, String params) {
        LOGGER.warn("Not yet implemented.");
//        final CreatureObject creatureObject = actor.asCreatureObject();
//
//        if (creatureObject == null)
//            return;
//
//        //get the ship of the creo if they are in one...
//
//        final GroupObject groupObject = creatureObject.getGroup();
//
//        if (groupObject != null) {
//            if (groupObject.getGroupLeaderId() != creatureObject.getNetworkId()) {
//                sendProseMessage(creatureObject, 0, GroupStringId::SID_GROUP_MUST_BE_LEADER);
//            }
//        }
    }
}
