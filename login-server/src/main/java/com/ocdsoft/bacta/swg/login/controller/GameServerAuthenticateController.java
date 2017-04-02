package com.ocdsoft.bacta.swg.login.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.swg.protocol.ServerType;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.swg.protocol.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.swg.protocol.controller.MessageHandled;
import com.ocdsoft.bacta.swg.server.login.message.GameServerAuthenticate;
import com.ocdsoft.bacta.swg.server.login.service.ClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@MessageHandled(handles = GameServerAuthenticate.class, type = ServerType.LOGIN)
@ConnectionRolesAllowed({})
public class GameServerAuthenticateController implements GameNetworkMessageController<GameServerAuthenticate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerAuthenticateController.class);

    private final ClusterService clusterService;

    @Inject
    public GameServerAuthenticateController(final ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    public void handleIncoming(SoeUdpConnection connection, GameServerAuthenticate message) {

//        if(clusterService.authenticateServer(serverName, serverKey)) {
//            connection.addRole(ConnectionRole.GAMESERVER);
//            connection.sendMessage(new GameServerAuthenticateSuccess());
//        } else {
//            connection.sendMessage(new GameServerAuthenticateFailure());
//        }
    }

}

