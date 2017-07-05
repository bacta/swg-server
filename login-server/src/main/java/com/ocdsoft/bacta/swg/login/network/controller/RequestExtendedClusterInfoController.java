package com.ocdsoft.bacta.swg.login.network.controller;


import com.ocdsoft.bacta.soe.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.swg.login.service.ClusterService;
import io.bacta.login.message.RequestExtendedClusterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
@MessageHandled(handles = RequestExtendedClusterInfo.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class RequestExtendedClusterInfoController implements GameNetworkMessageController<RequestExtendedClusterInfo> {

    private final ClusterService clusterService;

    @Inject
    public RequestExtendedClusterInfoController(final ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    public void handleIncoming(SoeUdpConnection loginConnection, RequestExtendedClusterInfo message) throws Exception {
        clusterService.sendExtendedClusterStatus(loginConnection);
    }
}

