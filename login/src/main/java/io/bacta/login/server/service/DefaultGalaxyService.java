package io.bacta.login.server.service;

import com.google.common.collect.ImmutableList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.galaxy.message.GalaxyServerId;
import io.bacta.galaxy.message.GalaxyServerStatus;
import io.bacta.login.message.*;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.data.ConnectionServerEntry;
import io.bacta.login.server.data.GalaxyRecord;
import io.bacta.login.server.mapper.GalaxyRecordMapper;
import io.bacta.login.server.repository.GalaxyRepository;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.crypto.KeyShare;
import io.bacta.soe.network.connection.ConnectionMap;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The default implementation of the galaxy service.
 * <p>
 * Manages galaxies by loading them into memory on a schedule. If a galaxy is added to the database by some external
 * process, it will be read via this scheduled load. When the login server first loads a galaxy from its database, it
 * will send it a {@link LoginServerOnline} message so that the galaxy knows the login server is waiting for it to
 * identify itself with the {@link GalaxyServerId} message.
 */
@Slf4j
@Service
public final class DefaultGalaxyService implements GalaxyService {
    private static final long GALAXY_LIST_REFRESH_INTERVAL = 10000; //10 seconds

    private final LoginServerProperties loginServerProperties;
    private final GalaxyRepository galaxyRepository;
    private final ConnectionMap connectionMap;
    private final KeyShare keyShare;
    /**
     * This is a map of galaxies that are currently "online" meaning that they have identified with the login server
     * via the {@link io.bacta.galaxy.message.GalaxyServerId} message.
     */
    private final TIntObjectMap<GalaxyRecord> galaxies;

    public DefaultGalaxyService(LoginServerProperties loginServerProperties,
                                GalaxyRepository galaxyRepository,
                                @Qualifier("LoginConnectionMap") ConnectionMap connectionMap,
                                KeyShare keyShare) {
        this.loginServerProperties = loginServerProperties;
        this.galaxyRepository = galaxyRepository;
        this.connectionMap = connectionMap;
        this.keyShare = keyShare;

        this.galaxies = new TIntObjectHashMap<>();
    }


    @Override
    public Collection<GalaxyRecord> getGalaxies() {
        return ImmutableList.copyOf(galaxies.valueCollection());
    }

    @Override
    public GalaxyRecord getGalaxyById(int id) {
        return galaxies.get(id);
    }

    @Override
    public GalaxyRecord registerGalaxy(String name, String address, int port, int timeZone) throws GalaxyRegistrationFailedException {
        //We need to check if any other galaxies exist with the same address:port. If so, we can't register this one.
        GalaxyRecord existingGalaxy = galaxyRepository.findByAddressAndPort(address, port);

        if (existingGalaxy != null) {
            LOGGER.error("Attempted to register galaxy, but found galaxy with same address and port already registered.");
            throw new GalaxyRegistrationFailedException(name, address, port, "A galaxy with this address and port is already registered.");
        }

        GalaxyRecord galaxyRecord = new GalaxyRecord(name, address, port, timeZone);
        galaxyRecord = galaxyRepository.save(galaxyRecord);

        return galaxyRecord;
    }

    @Override
    public void unregisterGalaxy(int id) {
        galaxyRepository.delete(id);
    }

    @Override
    public void identifyGalaxy(SoeConnection galaxyConnection, String name, int timeZone) {
        final InetSocketAddress galaxyAddress = galaxyConnection.getSoeUdpConnection().getRemoteAddress();

        final String address = galaxyAddress.getHostName();
        final int port = galaxyAddress.getPort();

        LOGGER.debug("Received identification request from galaxy at {}:{}.", address, port);

        GalaxyRecord galaxyRecord = galaxyRepository.findByAddressAndPort(address, port);

        boolean trusted = false; //This value is used to determine if we should send back the `GalaxyServerIdAck` message.

        if (galaxyRecord == null) {
            LOGGER.debug("Identifying galaxy {}:{} is not known, and therefore not trusted.", address, port);

            if (loginServerProperties.isAutoGalaxyRegistrationEnabled()) {
                LOGGER.info("Automatically registering untrusted galaxy because AutoGalaxyRegistration is enabled.");

                try {
                    galaxyRecord = registerGalaxy(name, address, port, timeZone);
                    trusted = true;
                } catch (GalaxyRegistrationFailedException ex) {
                    LOGGER.error("Tried to automatically register galaxy, but failed because address and port is already used by another galaxy.", ex);
                }
            }
        } else {
            LOGGER.trace("Identifying galaxy {}:{} is trusted.", address, port);
            trusted = true;
        }

        if (trusted) {
            galaxyRecord.setIdentified(true);

            final GalaxyServerIdAck ack = new GalaxyServerIdAck(galaxyRecord.getId());
            galaxyConnection.sendMessage(ack);
        }
    }


    @Override
    public void updateGalaxyStatus(SoeConnection galaxyConnection, GalaxyServerStatus message) {
        final InetSocketAddress galaxyAddress = galaxyConnection.getSoeUdpConnection().getRemoteAddress();
        final GalaxyRecord galaxyRecord = galaxies.get(message.getId());

        final String address = galaxyAddress.getHostName();
        final int port = galaxyAddress.getPort();

        //If no record was found, then this is not a trusted galaxy that this login server services.
        if (galaxyRecord == null) {
            LOGGER.error("Received status update from untrusted galaxy {}:{} using id {}. Ignoring.",
                    address, port, message.getId());
            return;
        }

        //Ensure that the address and port on file matches the connection.
        if (!galaxyRecord.getAddress().equalsIgnoreCase(address) || galaxyRecord.getPort() != port) {
            LOGGER.error("Received status update from galaxy {}:{} using id {}. Address and port does not match what is on file. Ignoring.",
                    address, port, message.getId());
            return;
        }

        GalaxyRecordMapper.map(galaxyRecord, message);

        //Save the status changes to the database.
        galaxyRepository.save(galaxyRecord);
    }

    @Override
    public void sendClusterEnum(SoeConnection connection) {
        LOGGER.debug("Sending cluster enumeration to client {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final boolean privateClient = false; //TODO: Determine if the client is on the same local network.

        final Set<LoginEnumCluster.ClusterData> clusters = new TreeSet<>();

        //Do we want to cache this operation at some point so its not done every time a connection requests it?
        for (final GalaxyRecord record : galaxies.valueCollection()) {
            final String galaxyName = record.getName();

            //If the galaxy has a name.
            //If it's a secret galaxy, then we need to be a private client in order to access it.
            if (galaxyName != null && !galaxyName.isEmpty() && (privateClient || !record.isSecret())) {
                final LoginEnumCluster.ClusterData data = new LoginEnumCluster.ClusterData(
                        record.getId(),
                        galaxyName,
                        record.getTimeZone());

                clusters.add(data);
            }
        }

        final LoginEnumCluster message = new LoginEnumCluster(clusters, loginServerProperties.getMaxCharactersPerAccount());
        connection.sendMessage(message);
    }

    @Override
    public void sendDisabledCharacterCreationServers(SoeConnection connection) {
        //Should we curate a set rather than keep it on the record and look it up?
        //This method allows for database character creation disabling.
        final Set<String> disabledGalaxies = galaxies.valueCollection().stream()
                .filter(GalaxyRecord::isAllowingCharacterCreation)
                .map(GalaxyRecord::getName)
                .collect(Collectors.toSet());

        final CharacterCreationDisabled message = new CharacterCreationDisabled(disabledGalaxies);
        connection.sendMessage(message);
    }

    @Override
    public void sendClusterStatus(SoeConnection connection) {
        LOGGER.debug("Sending cluster status to client {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final boolean privateClient = false; //TODO: Determine if the client is on the same local network.
        final boolean freeTrialAccount = false; //TODO: Determine if the account is a free trial account.

        final Set<LoginClusterStatus.ClusterData> clusterData = new TreeSet<>();

        for (final GalaxyRecord record : galaxies.valueCollection()) {
            final SortedSet<ConnectionServerEntry> connectionServers = record.getConnectionServers();

            //Only send status for galaxies that have identified and have at least one connection server.
            if (record.isIdentified()
                    && !connectionServers.isEmpty()
                    && (privateClient || !record.isSecret())) {

                //This will use the configured comparator to select the connection server for this connection to use.
                final ConnectionServerEntry connectionServer = connectionServers.first();

                final LoginClusterStatus.ClusterData data = new LoginClusterStatus.ClusterData(
                        record.getId(),
                        connectionServer.getClientServiceAddress(),
                        connectionServer.getClientServicePortPublic(),
                        connectionServer.getPingPort(),
                        privateClient ? record.getOnlinePlayers() : -1,
                        determineGalaxyPopulationStatus(record),
                        record.getMaxCharactersPerAccount(),
                        record.getTimeZone(),
                        determineGalaxyStatus(record, freeTrialAccount, privateClient),
                        true, //TODO: Discuss what should make a galaxy recommended or not.
                        //(record.isNotRecommendedCentral() || record.isNotRecommendedDatabase()),
                        record.getOnlinePlayerLimit(),
                        record.getOnlineFreeTrialLimit()
                );

                LOGGER.debug("Sending cluster status for {}({}) with connection server {}:{}. Online players {}/{} with status {}.",
                        record.getName(),
                        record.getId(),
                        connectionServer.getClientServiceAddress(),
                        connectionServer.getClientServicePortPublic(),
                        record.getOnlinePlayers(),
                        record.getOnlinePlayerLimit(),
                        data.getStatus());

                clusterData.add(data);
            }
        }

        final LoginClusterStatus message = new LoginClusterStatus(clusterData);
        connection.sendMessage(message);
    }

    @Override
    public void broadcastClusterStatus() {
        //TODO: Iterate the connected clients, and send the cluster status to each.
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void sendExtendedClusterStatus(SoeConnection connection) {
        LOGGER.info("Sending extended cluster info to {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final Set<LoginClusterStatusEx.ClusterData> clusterDataEx = new TreeSet<>();

        for (final GalaxyRecord galaxy : galaxies.valueCollection()) {
            final LoginClusterStatusEx.ClusterData data = new LoginClusterStatusEx.ClusterData(
                    galaxy.getId(),
                    galaxy.getBranch(),
                    galaxy.getNetworkVersion(),
                    galaxy.getChangeList()
            );

            clusterDataEx.add(data);
        }

        final LoginClusterStatusEx message = new LoginClusterStatusEx(clusterDataEx);
        connection.sendMessage(message);
    }

    @Override
    public void sendToGalaxy(int galaxyId, GameNetworkMessage message) {
        final GalaxyRecord galaxy = galaxies.get(galaxyId);

        if (galaxy == null)
            throw new IllegalArgumentException("Galaxy is not loaded.");

        sendToGalaxy(galaxy, message);
    }

    private void sendToGalaxy(GalaxyRecord galaxy, GameNetworkMessage message) {
        final InetSocketAddress galaxyAddress = new InetSocketAddress(galaxy.getAddress(), galaxy.getPort());
        final SoeConnection galaxyConnection = connectionMap.getOrCreate(galaxyAddress);

        galaxyConnection.sendMessage(message);
    }

    @Override
    public void sendToAllGalaxies(GameNetworkMessage message) {
        for (final GalaxyRecord galaxy : galaxies.valueCollection()) {
            sendToGalaxy(galaxy, message);
        }
    }

    private LoginClusterStatus.ClusterData.PopulationStatus determineGalaxyPopulationStatus(GalaxyRecord record) {
        final LoginClusterStatus.ClusterData.PopulationStatus populationStatus;

        final int percentFull = record.getOnlinePlayers() * 100 / record.getOnlinePlayerLimit();

        if (percentFull >= 100) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.FULL;
        } else if (percentFull >= loginServerProperties.getPopulationExtremelyHeavyThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.EXTREMELY_HEAVY;
        } else if (percentFull >= loginServerProperties.getPopulationVeryHeavyThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.VERY_HEAVY;
        } else if (percentFull >= loginServerProperties.getPopulationHeavyThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.HEAVY;
        } else if (percentFull >= loginServerProperties.getPopulationMediumThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.MEDIUM;
        } else if (percentFull >= loginServerProperties.getPopulationLightThresholdPercent()) {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.LIGHT;
        } else {
            populationStatus = LoginClusterStatus.ClusterData.PopulationStatus.VERY_LIGHT;
        }

        return populationStatus;
    }

    private LoginClusterStatus.ClusterData.Status determineGalaxyStatus(GalaxyRecord galaxy, boolean accountIsFreeTrial, boolean clientIsPrivate) {
        LoginClusterStatus.ClusterData.Status status;

        if (galaxy.isAcceptingConnections()) {
            status = LoginClusterStatus.ClusterData.Status.UP;

            //If we've exceeded the allowed number of players in the tutorial, then the galaxy is restricted to
            //existing characters.
            if (galaxy.getOnlineTutorialPlayers() >= galaxy.getOnlineTutorialLimit())
                status = LoginClusterStatus.ClusterData.Status.RESTRICTED;

            //If this account is a free trial account and the galaxy doesn't allow free trial accounts, then
            //restrict to existing characters.
            if (accountIsFreeTrial && !galaxy.isAllowingFreeTrialCharacterCreation())
                status = LoginClusterStatus.ClusterData.Status.RESTRICTED;

            //If the galaxy is full or this account is a free trial account and the galaxy has its maximum number of
            //free trial accounts connected, then show galaxy as full.
            if (galaxy.getOnlinePlayerLimit() >= galaxy.getOnlinePlayerLimit()
                    || (accountIsFreeTrial && galaxy.getOnlineFreeTrialPlayers() >= galaxy.getOnlineFreeTrialLimit()))
                status = LoginClusterStatus.ClusterData.Status.FULL;

        } else {
            status = LoginClusterStatus.ClusterData.Status.LOADING;
        }

        //Locked takes precedence over up or loading.
        if (galaxy.isLocked() && !clientIsPrivate) {
            status = LoginClusterStatus.ClusterData.Status.LOCKED;
        }

        return status;
        //return LoginClusterStatus.ClusterData.Status.UP;
    }


    /**
     * This method runs on a schedule and looks for new galaxies that aren't in memory. If a new galaxy was somehow
     * added directly to the database, then it would get loaded this way. At startup, it will load all trusted
     * galaxies and send each a {@link LoginServerOnline} message to tell them that the login server is online and
     * waiting for their {@link GalaxyServerId} message.
     */
    @Scheduled(initialDelay = 0, fixedRate = GALAXY_LIST_REFRESH_INTERVAL)
    private void refreshGalaxyServerList() {
        final Iterable<GalaxyRecord> records = galaxyRepository.findAll();

        for (final GalaxyRecord record : records) {
            //We want to look if this galaxy is already in our map. If so, then ignore it.
            if (galaxies.containsKey(record.getId()))
                continue;

            LOGGER.debug("Loading galaxy {}({}) with address {}:{}.",
                    record.getName(),
                    record.getId(),
                    record.getAddress(),
                    record.getPort());

            //We want to send this galaxy the LoginServerOnline message so that it knows we are online and waiting
            //for it to identify with us. First, we need to get a connection to it.
            final InetSocketAddress galaxyAddress = new InetSocketAddress(record.getAddress(), record.getPort());
            final SoeConnection galaxyConnection = connectionMap.getOrCreate(galaxyAddress);

            if (galaxyConnection != null) {
                final LoginServerOnline onlineMessage = new LoginServerOnline();
                galaxyConnection.sendMessage(onlineMessage);
            }

            //Create a key for this galaxy.

            //Add the galaxy to our memory map.
            galaxies.put(record.getId(), record);
        }
    }
}