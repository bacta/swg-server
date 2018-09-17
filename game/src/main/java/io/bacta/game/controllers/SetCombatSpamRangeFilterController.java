package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.SetCombatSpamRangeFilter;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = SetCombatSpamRangeFilter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class SetCombatSpamRangeFilterController implements GameNetworkMessageController<GameRequestContext, SetCombatSpamRangeFilter> {
    @Override
    public void handleIncoming(GameRequestContext context, SetCombatSpamRangeFilter message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

