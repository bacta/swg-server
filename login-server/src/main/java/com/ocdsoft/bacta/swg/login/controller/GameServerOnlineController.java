package com.ocdsoft.bacta.swg.login.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.protocol.ServerType;
import com.ocdsoft.bacta.soe.protocol.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.protocol.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.protocol.controller.MessageHandled;
import com.ocdsoft.bacta.soe.protocol.service.PublisherService;
import com.ocdsoft.bacta.swg.server.game.message.GameServerOnline;
import com.ocdsoft.bacta.swg.login.event.GameServerOnlineEvent;

/**
 * Created by kburkhardt on 1/31/15.
 */
@ConnectionRolesAllowed({ConnectionRole.WHITELISTED})
@MessageHandled(handles = GameServerOnline.class, type = ServerType.LOGIN)
public class GameServerOnlineController implements GameNetworkMessageController<GameServerOnline> {

    private final PublisherService publisherService;

    @Inject
    public GameServerOnlineController(final PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    public void handleIncoming(SoeUdpConnection connection, GameServerOnline message) throws Exception {
        publisherService.onEvent(new GameServerOnlineEvent(message.getClusterServer()));
    }
}
