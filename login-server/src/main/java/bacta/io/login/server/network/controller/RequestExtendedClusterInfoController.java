package bacta.io.login.server.network.controller;


import bacta.io.login.server.service.ClusterService;
import bacta.io.soe.network.connection.ConnectionRole;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.controller.ConnectionRolesAllowed;
import bacta.io.soe.network.controller.GameNetworkMessageController;
import bacta.io.soe.network.controller.MessageHandled;
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

