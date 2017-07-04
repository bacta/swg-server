package com.ocdsoft.bacta.swg.login.network.controller;


import com.ocdsoft.bacta.soe.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.swg.login.message.LoginClusterStatusEx;
import com.ocdsoft.bacta.swg.login.message.RequestExtendedClusterInfo;
import com.ocdsoft.bacta.swg.login.object.ClusterData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Component
@MessageHandled(handles = RequestExtendedClusterInfo.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class RequestExtendedClusterInfoController implements GameNetworkMessageController<RequestExtendedClusterInfo> {

//    private final ClusterService clusterService;
//
//    @Inject
//    public RequestExtendedClusterInfoController(final ClusterService clusterService) {
//        this.clusterService = clusterService;
//    }


    @Override
    public void handleIncoming(SoeUdpConnection loginConnection, RequestExtendedClusterInfo message) throws Exception {
//        LoginClusterStatusEx loginClusterStatusEx = new LoginClusterStatusEx(
//                clusterService.getClusterEntries().stream()
//                .map(ClusterData::getExtendedClusterData)
//                .collect(Collectors.toSet())
//        );

        LoginClusterStatusEx loginClusterStatusEx = new LoginClusterStatusEx(new HashSet<>());
        loginConnection.sendMessage(loginClusterStatusEx);
    }
}

