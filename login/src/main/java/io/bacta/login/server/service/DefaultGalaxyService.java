package io.bacta.login.server.service;

import com.google.common.collect.ImmutableList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.login.message.CharacterCreationDisabled;
import io.bacta.login.message.LoginClusterStatus;
import io.bacta.login.message.LoginClusterStatusEx;
import io.bacta.login.message.LoginEnumCluster;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.model.Galaxy;
import io.bacta.login.server.model.GalaxyStatusUpdate;
import io.bacta.login.server.repository.GalaxyRepository;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The default implementation of the galaxy service.
 * <p>
 * Manages galaxies by loading them into memory on a schedule. If a galaxy is added to the database by some external
 * process, it will be read via this scheduled load. When the login server first loads a galaxy from its database,
 * it will report it as offline until the login server polls it, or the galaxy server pushes a status update to the
 * login server.
 */
@Slf4j
@Service
public final class DefaultGalaxyService implements GalaxyService {
    private static final long GALAXY_LIST_STATUS_POLL_INTERVAL = 10000; //10 seconds
    private static final int INITIAL_GALAXIES_CAPACITY = 100;

    private final LoginServerProperties loginServerProperties;
    /**
     * Repository for saving and reading galaxies to data store.
     */
    private final GalaxyRepository galaxyRepository;
    /**
     * Galaxies here have their transient data set and continuously updated.
     */
    private final TIntObjectMap<Galaxy> loadedGalaxies;

    public DefaultGalaxyService(final LoginServerProperties loginServerProperties,
                                final GalaxyRepository galaxyRepository) {

        this.loginServerProperties = loginServerProperties;
        this.galaxyRepository = galaxyRepository;
        this.loadedGalaxies = new TIntObjectHashMap<>(INITIAL_GALAXIES_CAPACITY);
    }

    @Override
    public Collection<Galaxy> getGalaxies() {
        return ImmutableList.copyOf(loadedGalaxies.valueCollection());
    }

    @Override
    public Galaxy getGalaxyById(int id) {
        if (!this.loadedGalaxies.containsKey(id)) {
            this.loadGalaxy(id);
        }

        return this.loadedGalaxies.get(id);
    }

    @Override
    public Galaxy registerGalaxy(String name, String address, int port, int timeZone) throws GalaxyRegistrationFailedException {
        //We need to check if any other galaxies exist with the same address:port. If so, we can't register this one.
        final Galaxy existingGalaxy = galaxyRepository.findByAddressAndPort(address, port);

        if (existingGalaxy != null) {
            LOGGER.error("Attempted to register galaxy, but found galaxy with same address and port already registered.");
            throw new GalaxyRegistrationFailedException(name, address, port, "A galaxy with this address and port is already registered.");
        }

        Galaxy registeredGalaxy = new Galaxy(name, address, port, timeZone);
        registeredGalaxy = galaxyRepository.save(registeredGalaxy);

        //Attempt to load the galaxy.
        loadGalaxy(registeredGalaxy.getId());

        return registeredGalaxy;
    }

    @Override
    public void unregisterGalaxy(int id) {
        galaxyRepository.deleteById(id);

        unloadGalaxy(id);
    }

    @Override
    public void handleGalaxyStatusUpdate(GalaxyStatusUpdate statusUpdate) {

    }

    @Override
    public void requestGalaxyStatusUpdate(int galaxyId) {
        final Galaxy galaxy = getGalaxyById(galaxyId);
    }

    /**
     * Loads the galaxy into the loadedGalaxy map, and queues up a request to fetch its latest status information.
     *
     * @param galaxyId The galaxy id of the galaxy to load.
     */
    private void loadGalaxy(int galaxyId) {
        final Galaxy galaxy = galaxyRepository.findById(galaxyId).get();
        this.loadedGalaxies.put(galaxyId, galaxy);

        //Send message to galaxy to get its status info.
    }

    /**
     * Unloads a galaxy from the loadedGalaxy map.
     *
     * @param galaxyId The galaxy id of the galaxy to unload.
     */
    private void unloadGalaxy(int galaxyId) {
        this.loadedGalaxies.remove(galaxyId);
    }

    @Override
    public void sendClusterEnum(SoeConnection connection) {
        LOGGER.debug("Sending cluster enumeration to client {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final boolean privateClient = false; //TODO: Determine if the client is on the same local network.

        final Collection<Galaxy> galaxies = getGalaxies();
        final Set<LoginEnumCluster.ClusterData> clusters = new TreeSet<>();

        //Do we want to cache this operation at some point so its not done every time a connection requests it?
        for (final Galaxy galaxy : galaxies) {
            final String galaxyName = galaxy.getName();

            //If the galaxy has a name.
            //If it's a secret galaxy, then we need to be a private client in order to access it.
            if (galaxyName != null && !galaxyName.isEmpty() && (privateClient || !galaxy.isSecret())) {
                final LoginEnumCluster.ClusterData data = new LoginEnumCluster.ClusterData(
                        galaxy.getId(),
                        galaxyName,
                        galaxy.getTimeZone());

                clusters.add(data);
            }
        }

        final LoginEnumCluster message = new LoginEnumCluster(clusters, loginServerProperties.getMaxCharactersPerAccount());
        connection.sendMessage(message);
    }

    @Override
    public void sendDisabledCharacterCreationServers(SoeConnection connection) {
        final Collection<Galaxy> galaxies = getGalaxies();

        //Should we curate a set rather than keep it on the record and look it up?
        //This method allows for database character creation disabling.
        final Set<String> disabledGalaxies = galaxies.stream()
                .filter(galaxy -> !galaxy.isCharacterCreationEnabled())
                .map(Galaxy::getName)
                .collect(Collectors.toSet());

        final CharacterCreationDisabled message = new CharacterCreationDisabled(disabledGalaxies);
        connection.sendMessage(message);
    }

    @Override
    public void sendClusterStatus(SoeConnection connection) {
        LOGGER.debug("Sending cluster status to client {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final boolean privateClient = false; //TODO: Determine if the client is on the same local network.
        final boolean freeTrialAccount = false; //TODO: Determine if the account is a free trial account.

        final Collection<Galaxy> galaxies = getGalaxies();
        final Set<LoginClusterStatus.ClusterData> clusterData = new TreeSet<>();

        for (final Galaxy galaxy : galaxies) {
            //final SortedSet<ConnectionServerEntry> connectionServers = galaxy.getConnectionServers();

            //Only send status for galaxies that have identified and have at least one connection server.
            if (/*galaxy.isIdentified()*/
                //!connectionServers.isEmpty()
                    (privateClient || !galaxy.isSecret())) {

                //This will use the configured comparator to select the connection server for this connection to use.
                //final ConnectionServerEntry connectionServer = connectionServers.first();

                final LoginClusterStatus.ClusterData data = new LoginClusterStatus.ClusterData(
                        galaxy.getId(),
                        //connectionServer.getClientServiceAddress(),
                        //connectionServer.getClientServicePortPublic(),
                        //connectionServer.getPingPort(),

                        //TODO: Work this out when we start sending status from galaxy on identify.
                        galaxy.getAddress(),
                        (short) galaxy.getPort(),
                        (short) 44464,

                        privateClient ? galaxy.getOnlinePlayers() : -1,
                        determineGalaxyPopulationStatus(galaxy),
                        galaxy.getMaxCharactersPerAccount(),
                        galaxy.getTimeZone(),
                        determineGalaxyStatus(galaxy, freeTrialAccount, privateClient),

                        false, //TODO: Discuss what should make a galaxy recommended or not.
                        //(record.isNotRecommendedCentral() || record.isNotRecommendedDatabase()),
                        galaxy.getOnlinePlayerLimit(),
                        galaxy.getOnlineFreeTrialLimit()
                );

                LOGGER.debug("Sending cluster status for {}({}) with connection server {}:{}. Online players {}/{} with status {}.",
                        galaxy.getName(),
                        galaxy.getId(),

                        galaxy.getAddress(),
                        galaxy.getPort(),
                        //connectionServer.getClientServiceAddress(),
                        //connectionServer.getClientServicePortPublic(),

                        galaxy.getOnlinePlayers(),
                        galaxy.getOnlinePlayerLimit(),
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
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void sendExtendedClusterStatus(SoeConnection connection) {
        LOGGER.info("Sending extended cluster info to {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final Collection<Galaxy> galaxies = getGalaxies();
        final Set<LoginClusterStatusEx.ClusterData> clusterDataEx = new TreeSet<>();

        for (final Galaxy galaxy : galaxies) {
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


    private LoginClusterStatus.ClusterData.PopulationStatus determineGalaxyPopulationStatus(Galaxy galaxy) {
        final LoginClusterStatus.ClusterData.PopulationStatus populationStatus;

        //If the limit is 0 or less, then the server is full by default.
        if (galaxy.getOnlinePlayerLimit() <= 0)
            return LoginClusterStatus.ClusterData.PopulationStatus.FULL;

        final int percentFull = galaxy.getOnlinePlayers() * 100 / galaxy.getOnlinePlayerLimit();

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

    private LoginClusterStatus.ClusterData.Status determineGalaxyStatus(Galaxy galaxy, boolean accountIsFreeTrial, boolean clientIsPrivate) {
        LoginClusterStatus.ClusterData.Status status;

        if (galaxy.isAcceptingConnections()) {
            status = LoginClusterStatus.ClusterData.Status.UP;

            //If we've exceeded the allowed number of players in the tutorial, then the galaxy is restricted to
            //existing characters.
            if (galaxy.getOnlineTutorialPlayers() >= galaxy.getOnlineTutorialLimit())
                status = LoginClusterStatus.ClusterData.Status.RESTRICTED;

            //If this account is a free trial account and the galaxy doesn't allow free trial accounts, then
            //restrict to existing characters.
            if (accountIsFreeTrial && !galaxy.isCharacterCreationEnabled())
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
    }


    /**
     * This method runs on a schedule and looks for new galaxies in the data store that aren't already in memory. If a
     * new galaxy was somehow added directly to the data store, then it would get loaded this way. At startup, the server
     * will load all trusted galaxies and attempt to poll them for their status.
     */
    @Scheduled(initialDelay = 0, fixedRate = GALAXY_LIST_STATUS_POLL_INTERVAL)
    private void refreshGalaxyServerList() {
        LOGGER.trace("Refreshing galaxy server list.");

        final Iterable<Galaxy> records = galaxyRepository.findAll();

        for (final Galaxy record : records) {
            //Skip galaxies that are currently loaded.
            if (this.loadedGalaxies.containsKey(record.getId()))
                continue;

            loadGalaxy(record.getId());
        }
    }
}