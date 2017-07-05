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

package bacta.io.login.server.service;

import bacta.io.login.server.GalaxyRegistrationFailedException;
import bacta.io.login.server.object.ClusterListEntry;
import bacta.io.login.server.repository.ClusterRepository;
import bacta.io.soe.network.connection.SoeUdpConnection;
import io.bacta.shared.GameNetworkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 7/3/2017.
 */
@Slf4j
@Service
public final class DefaultClusterService implements ClusterService {
    private final ClusterRepository clusterRepository;
    private final List<ClusterListEntry> clusters;

    @Inject
    public DefaultClusterService(ClusterRepository clusterRepository){
        this.clusterRepository = clusterRepository;

        this.clusters = new ArrayList<>();
    }

    @Override
    public ClusterListEntry findClusterById(final int clusterId) {
        return clusters.stream()
                .filter(entry -> entry.getId() == clusterId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ClusterListEntry findClusterByName(String clusterName) {
        return clusters.stream()
                .filter(entry -> entry.getName().equalsIgnoreCase(clusterName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void refreshClusterList() {

    }

    @Override
    public ClusterListEntry registerCluster(String clusterName, String address, short port) throws GalaxyRegistrationFailedException {
        return null;
    }

    @Override
    public void disconnectCluster(ClusterListEntry clusterListEntry, boolean forceDisconnect, boolean reconnect) {

    }

    @Override
    public void sendClusterStatus(SoeUdpConnection connection) {

    }

    @Override
    public void sendClusterStatusToAll() {

    }

    @Override
    public void sendExtendedClusterStatus(SoeUdpConnection connection) {

    }

    @Override
    public void sendToCluster(int clusterId, GameNetworkMessage message) {

    }
}
