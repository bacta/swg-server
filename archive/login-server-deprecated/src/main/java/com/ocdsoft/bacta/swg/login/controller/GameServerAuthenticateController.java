package com.ocdsoft.bacta.swg.login.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.protocol.ServerType;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.swg.login.message.GameServerAuthenticate;
import com.ocdsoft.bacta.swg.login.service.ClusterService;
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
//            soe.addRole(ConnectionRole.GAMESERVER);
//            soe.sendMessage(new GameServerAuthenticateSuccess());
//        } else {
//            soe.sendMessage(new GameServerAuthenticateFailure());
//        }
    }

}

