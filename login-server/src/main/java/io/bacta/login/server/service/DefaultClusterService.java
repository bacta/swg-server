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

package io.bacta.login.server.service;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.login.message.CharacterCreationDisabled;
import io.bacta.login.message.LoginClusterStatus;
import io.bacta.login.message.LoginClusterStatusEx;
import io.bacta.login.message.LoginEnumCluster;
import io.bacta.login.server.GalaxyRegistrationFailedException;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.entity.ClusterEntity;
import io.bacta.login.server.object.ClusterListEntry;
import io.bacta.login.server.object.ConnectionServerEntry;
import io.bacta.login.server.repository.ClusterRepository;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by crush on 7/3/2017.
 */
@Slf4j
@Service
public final class DefaultClusterService implements ClusterService {
    private final ClusterRepository clusterRepository;
    private final LoginServerProperties properties;
    private final TIntObjectMap<ClusterListEntry> clusters;
    private final Set<String> disabledCharacterCreationClusters;

    @Inject
    public DefaultClusterService(ClusterRepository clusterRepository,
                                 LoginServerProperties properties) {
        this.clusterRepository = clusterRepository;
        this.properties = properties;

        clusters = new TIntObjectHashMap<>();
        disabledCharacterCreationClusters = new HashSet<>();
    }

    @Override
    public ClusterListEntry findClusterById(final int clusterId) {
        return clusters.get(clusterId);
    }

    @Override
    public ClusterListEntry findClusterByName(String clusterName) {
        return clusters.valueCollection().stream()
                .filter(entry -> entry.getName().equalsIgnoreCase(clusterName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void refreshClusterList() {
        final Iterable<ClusterEntity> clusterEntities = clusterRepository.findAll();

        //Rather than clear and rebuild the list, we try and do an add/update/remove because the ClusterListEntry objects
        //have information on them about the central server connection as well as the connection servers for that cluster
        //which have connected. We want to preserve that information.

        //Add new clusters.
        //Update existing clusters.
        for (final ClusterEntity entity : clusterEntities) {
            ClusterListEntry cle = clusters.get(entity.getId());

            if (cle == null) {
                //Cluster is new, so add a new entry.
                cle = new ClusterListEntry(entity.getId(), entity.getName());
                clusters.put(entity.getId(), cle);
            }

            ClusterEntity.map(entity, cle);
        }

        //TODO: Remove non-registered clusters.
    }

    @Override
    public ClusterListEntry registerCluster(String clusterName, String address, short port) throws GalaxyRegistrationFailedException {
        return null;
    }

    @Override
    public void disconnectCluster(ClusterListEntry clusterListEntry, boolean forceDisconnect, boolean reconnect) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void sendClusterEnum(SoeUdpConnection connection) {
        LOGGER.info("Sending cluster enumeration to {}.", connection.getRemoteAddress());
        final boolean clientIsPrivate = false; //TODO

        final Set<LoginEnumCluster.ClusterData> clusterData = new TreeSet<>();

        for (final ClusterListEntry cle : clusters.valueCollection()) {
            final String clusterName = cle.getName();

            if (clusterName != null && !clusterName.isEmpty()
                    && (clientIsPrivate || !cle.isSecret())) {

                final LoginEnumCluster.ClusterData data = new LoginEnumCluster.ClusterData(
                        cle.getId(),
                        clusterName,
                        cle.getTimeZone()
                );

                clusterData.add(data);
            }
        }

        final LoginEnumCluster message = new LoginEnumCluster(clusterData, properties.getMaxCharactersPerAccount());
        connection.sendMessage(message);
    }

    @Override
    public void sendDisabledCharacterCreationServers(SoeUdpConnection connection) {
        final CharacterCreationDisabled message = new CharacterCreationDisabled(disabledCharacterCreationClusters);
        connection.sendMessage(message);
    }

    @Override
    public void sendClusterStatus(SoeUdpConnection connection) {
        LOGGER.info("Sending cluster status to {}.", connection.getRemoteAddress());

        final boolean clientIsPrivate = false; //TODO
        final boolean accountIsFreeTrial = false; //TODO

        final Set<LoginClusterStatus.ClusterData> clusterData = new TreeSet<>();

        for (final ClusterListEntry cle : clusters.valueCollection()) {
            final SortedSet<ConnectionServerEntry> connectionServers = cle.getConnectionServers();

            //Only send for known clusters that are connected and have a connection server.
            if (cle.isConnected()
                    && !connectionServers.isEmpty()
                    && (clientIsPrivate || !cle.isSecret())) {

                final ConnectionServerEntry connectionServer = connectionServers.first();

                final LoginClusterStatus.ClusterData data = new LoginClusterStatus.ClusterData(
                        cle.getId(),
                        connectionServer.getClientServiceAddress(),
                        connectionServer.getClientServicePortPublic(),
                        connectionServer.getPingPort(),
                        clientIsPrivate ? cle.getNumPlayers() : -1,
                        determineGalaxyPopulationStatus(cle),
                        cle.getMaxCharactersPerAccount(),
                        cle.getTimeZone(),
                        determineGalaxyStatus(cle, accountIsFreeTrial, clientIsPrivate),
                        (cle.isNotRecommendedCentral() || cle.isNotRecommendedDatabase()),
                        cle.getOnlinePlayerLimit(),
                        cle.getOnlineFreeTrialLimit()
                );

                LOGGER.debug("Sending cluster status for {}({}) with connection server {}:{}. Online players {}/{} with status {}.",
                        cle.getName(),
                        cle.getId(),
                        connectionServer.getClientServiceAddress(),
                        connectionServer.getClientServicePortPublic(),
                        cle.getNumPlayers(),
                        cle.getOnlinePlayerLimit(),
                        data.getStatus());

                clusterData.add(data);
            }
        }

        final LoginClusterStatus message = new LoginClusterStatus(clusterData);
        connection.sendMessage(message);
    }

    private LoginClusterStatus.ClusterData.PopulationStatus determineGalaxyPopulationStatus(ClusterListEntry cle) {
        final LoginClusterStatus.ClusterData.PopulationStatus populationStatus;

        final int percentFull = cle.getNumPlayers() * 100 / cle.getOnlinePlayerLimit();

        if (percentFull >= 100) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.FULL;
        } else if (percentFull >= properties.getPopulationExtremelyHeavyThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.EXTREMELY_HEAVY;
        } else if (percentFull >= properties.getPopulationVeryHeavyThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.VERY_HEAVY;
        } else if (percentFull >= properties.getPopulationHeavyThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.HEAVY;
        } else if (percentFull >= properties.getPopulationMediumThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.MEDIUM;
        } else if (percentFull >= properties.getPopulationLightThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.LIGHT;
        } else {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.VERY_LIGHT;
        }

        return populationStatus;
    }

    private LoginClusterStatus.ClusterData.Status determineGalaxyStatus(ClusterListEntry cle, boolean accountIsFreeTrial, boolean clientIsPrivate) {
        LoginClusterStatus.ClusterData.Status status;

        if (cle.isReadyForPlayers()) {
            status = LoginClusterStatus.ClusterData.Status.UP;

            //If we've exceeded the allowed number of players in the tutorial, then the galaxy is restricted to
            //existing characters.
            if (cle.getNumTutorialPlayers() >= cle.getOnlineTutorialLimit())
                status = LoginClusterStatus.ClusterData.Status.RESTRICTED;

            //If this account is a free trial account and the galaxy doesn't allow free trial accounts, then
            //restrict to existing characters.
            if (accountIsFreeTrial && !cle.isFreeTrialCanCreateChar())
                status = LoginClusterStatus.ClusterData.Status.RESTRICTED;

            //If the galaxy is full or this account is a free trial account and the galaxy has its maximum number of
            //free trial accounts connected, then show galaxy as full.
            if (cle.getNumPlayers() >= cle.getOnlinePlayerLimit()
                    || (accountIsFreeTrial && cle.getNumFreeTrialPlayers() >= cle.getOnlineFreeTrialLimit()))
                status = LoginClusterStatus.ClusterData.Status.FULL;

        } else {
            status = LoginClusterStatus.ClusterData.Status.LOADING;
        }

        //Locked takes precedence over up or loading.
        if (cle.isLocked() && !clientIsPrivate) {
            status = LoginClusterStatus.ClusterData.Status.LOCKED;
        }

        return LoginClusterStatus.ClusterData.Status.UP;
    }

    @Override
    public void sendClusterStatusToAll() {
        //TODO: Iterate the connected clients, and send the cluster status to each.
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void sendExtendedClusterStatus(SoeUdpConnection connection) {
        LOGGER.info("Sending extended cluster info to {}.", connection.getRemoteAddress());

        final Set<LoginClusterStatusEx.ClusterData> clusterDataEx = new TreeSet<>();

        for (final ClusterListEntry cle : clusters.valueCollection()) {
            final LoginClusterStatusEx.ClusterData data = new LoginClusterStatusEx.ClusterData(
                    cle.getId(),
                    cle.getBranch(),
                    cle.getNetworkVersion(),
                    cle.getChangeList()
            );

            clusterDataEx.add(data);
        }

        final LoginClusterStatusEx message = new LoginClusterStatusEx(clusterDataEx);
        connection.sendMessage(message);
    }

    @Override
    public void sendToCluster(int clusterId, GameNetworkMessage message) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
