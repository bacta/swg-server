package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.login.GalaxyRegistrationFailedException;
import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;
import com.ocdsoft.bacta.swg.login.repository.ClusterRepository;
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
