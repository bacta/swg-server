/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.server.controller;


import io.bacta.login.message.RequestExtendedClusterInfo;
import io.bacta.login.server.service.GalaxyService;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
@MessageHandled(handles = RequestExtendedClusterInfo.class)
@ConnectionRolesAllowed({})
public class RequestExtendedClusterInfoController implements GameNetworkMessageController<SoeConnection, RequestExtendedClusterInfo> {

    private final GalaxyService galaxyService;

    @Inject
    public RequestExtendedClusterInfoController(final GalaxyService clusterService) {
        this.galaxyService = clusterService;
    }

    @Override
    public void handleIncoming(SoeConnection connection, RequestExtendedClusterInfo message) throws Exception {
    }

//    @Override
//    public void handleIncoming(SoeUdpConnection loginConnection, RequestExtendedClusterInfo message) throws Exception {
//        //clusterService.sendExtendedClusterStatus(loginConnection);
//    }
}

