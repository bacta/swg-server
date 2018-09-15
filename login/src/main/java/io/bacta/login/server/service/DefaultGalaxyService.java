package io.bacta.login.server.service;

import com.google.common.collect.ImmutableList;
import io.bacta.galaxy.message.GalaxyServerStatus;
import io.bacta.login.message.CharacterCreationDisabled;
import io.bacta.login.message.LoginClusterStatus;
import io.bacta.login.message.LoginClusterStatusEx;
import io.bacta.login.message.LoginEnumCluster;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.model.ConnectionServerEntry;
import io.bacta.login.server.model.Galaxy;
import io.bacta.login.server.model.GalaxyPopulationStatus;
import io.bacta.login.server.model.GalaxyStatus;
import io.bacta.login.server.repository.GalaxyRepository;
import io.bacta.soe.network.connection.ConnectionMap;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private static final int INITIAL_GALAXIES_CAPACITY = 100;
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 512;

    private final LoginServerProperties loginServerProperties;
    /**
     * Repository for saving and reading galaxies to data store.
     */
    private final GalaxyRepository galaxyRepository;
    /**
     * Galaxies here have their transient data set and continuously updated.
     */
    private final Map<String, Galaxy> loadedGalaxies;
    private final TaskScheduler taskScheduler;
    private final ConnectionMap loginConnections;

    @Inject
    public DefaultGalaxyService(final LoginServerProperties loginServerProperties,
                                final GalaxyRepository galaxyRepository,
                                final TaskScheduler taskScheduler,
                                @Qualifier("LoginConnectionMap") ConnectionMap loginConnections) {

        this.loginServerProperties = loginServerProperties;
        this.galaxyRepository = galaxyRepository;
        this.loadedGalaxies = new ConcurrentHashMap<>(INITIAL_GALAXIES_CAPACITY);
        this.taskScheduler = taskScheduler;
        this.loginConnections = loginConnections;

        scheduleMaintenanceTasks();
    }

    @Override
    public Collection<Galaxy> getGalaxies() {
        return ImmutableList.copyOf(loadedGalaxies.values());
    }

    @Override
    public Galaxy getGalaxy(String name) throws GalaxyNotFoundException {
        //If it hasn't been loaded, try to load it.
        if (!loadedGalaxies.containsKey(name))
            loadGalaxy(name);

        //We only want to return galaxies that have been loaded in memory at this point.
        //If load galaxy failed, then an exception should've been thrown.
        return loadedGalaxies.get(name);
    }

    @Override
    public Galaxy registerGalaxy(String name, String address, int port, int timeZone)
            throws GalaxyNotFoundException, GalaxyRegistrationFailedException, NoSuchAlgorithmException {

        //Check if any other galaxies exist with this name.
        Galaxy existingGalaxy = galaxyRepository.findByName(name);

        if (existingGalaxy != null) {
            LOGGER.error("Attempted to register galaxy, but found galaxy with same name already registered.");
            throw new GalaxyRegistrationFailedException(name, address, port, "A galaxy with this name is already registered.");
        }

        //Check if any other galaxies exist with the same address:port. If so, we can't register this one.
        existingGalaxy = galaxyRepository.findByAddressAndPort(address, port);

        if (existingGalaxy != null) {
            LOGGER.error("Attempted to register galaxy, but found galaxy with same address and port already registered.");
            throw new GalaxyRegistrationFailedException(name, address, port, "A galaxy with this address and port is already registered.");
        }

        //We need to generate a key pair.
        //TODO: Should we create an instance every time or keep one only?
        final KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keygen.initialize(KEY_SIZE);

        final KeyPair keyPair = keygen.generateKeyPair();

        Galaxy registeredGalaxy = new Galaxy(name, address, port, timeZone, keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
        registeredGalaxy = galaxyRepository.save(registeredGalaxy);

        //Load the galaxy into memory.
        loadGalaxy(registeredGalaxy.getName());

        return registeredGalaxy;
    }

    @Override
    public void unregisterGalaxy(String name) {
        final Galaxy galaxy = galaxyRepository.findByName(name);
        galaxyRepository.delete(galaxy);

        //Unload it from memory if it exists there.
        unloadGalaxy(name);
    }

    @Override
    public void updateGalaxyStatus(String galaxyName, GalaxyServerStatus update)
            throws GalaxyNotFoundException, GalaxyRegistrationFailedException {
        final Galaxy galaxy = getGalaxy(galaxyName);

        //TODO: Make this a common validation method as it is used in multiple places.
        //If we are changing the name of the galaxy, we need to:
        //- Ensure no other galaxy already has that name.
        //- Log that we are changing it.
        if (!galaxyName.equals(update.getName())) {
            final Galaxy existingGalaxy = this.galaxyRepository.findByName(update.getName());

            if (existingGalaxy != null) {
                LOGGER.error("Could not update galaxy because name is taken by another galaxy.");

                throw new GalaxyRegistrationFailedException(
                        update.getName(),
                        update.getAddress(),
                        update.getPort(),
                        "A galaxy with the requested name already exists.");
            }

            LOGGER.info("Updating galaxy {} with new name {}.",
                    galaxyName,
                    update.getName());
        }

        //Mark that the galaxy has updated itself.
        galaxy.setLastUpdate(Instant.now());

        //Meta information.
        galaxy.setName(update.getName());
        galaxy.setAddress(update.getAddress());
        galaxy.setPort(update.getPort());
        galaxy.setTimeZone(update.getTimeZone());
        galaxy.setBranch(update.getBranch());
        galaxy.setNetworkVersion(update.getNetworkVersion());
        galaxy.setChangeList(update.getChangeList());

        //Metrics information.
        galaxy.setMaxCharacters(update.getMaxCharacters());
        galaxy.setMaxCharactersPerAccount(update.getMaxCharactersPerAccount());
        galaxy.setOnlinePlayerLimit(update.getOnlinePlayerLimit());
        galaxy.setOnlineTutorialLimit(update.getOnlineTutorialLimit());
        galaxy.setOnlineFreeTrialLimit(update.getOnlineFreeTrialLimit());

        //Status information.
        galaxy.setCharacterCreationDisabled(update.isCharacterCreationDisabled());
        galaxy.setAcceptingConnections(update.isAcceptingConnections());
        galaxy.setSecret(update.isSecret());
        galaxy.setLocked(update.isLocked());

        //Connection servers.
        galaxy.updateConnectionServers(update.getConnectionServers());

        galaxyRepository.save(galaxy);
    }

    /**
     * Loads the galaxy into the loadedGalaxy map.
     *
     * @param name The name of the galaxy to load.
     */
    private void loadGalaxy(String name) throws GalaxyNotFoundException {
        final Galaxy galaxy = galaxyRepository.findByName(name);

        if (galaxy == null)
            throw new GalaxyNotFoundException(name);

        this.loadedGalaxies.put(name, galaxy);
    }

    /**
     * Unloads a galaxy from the loadedGalaxy map.
     *
     * @param name The name of the galaxy to unload.
     */
    private void unloadGalaxy(String name) {
        this.loadedGalaxies.remove(name);
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
                .filter(Galaxy::isCharacterCreationDisabled)
                .map(Galaxy::getName)
                .collect(Collectors.toSet());

        final CharacterCreationDisabled message = new CharacterCreationDisabled(disabledGalaxies);
        connection.sendMessage(message);
    }

    private LoginClusterStatus createLoginClusterStatusMessage() {
        final boolean privateClient = false; //TODO: Determine if the client is on the same local network.
        final boolean freeTrialAccount = false; //TODO: Determine if the account is a free trial account.

        final Collection<Galaxy> galaxies = getGalaxies();
        final Set<LoginClusterStatus.ClusterData> clusterData = new TreeSet<>();

        for (final Galaxy galaxy : galaxies) {
            final SortedSet<ConnectionServerEntry> connectionServers = galaxy.getConnectionServers();

            //Only send status for galaxies that have identified and have at least one connection server.
            if (galaxy.isIdentified(loginServerProperties.getGalaxyLinkDeadThreshold()) &&
                    !connectionServers.isEmpty() &&
                    (privateClient || !galaxy.isSecret())) {

                //This will use the configured comparator to select the connection server for this connection to use.
                final ConnectionServerEntry connectionServer = connectionServers.first();

                final LoginClusterStatus.ClusterData data = new LoginClusterStatus.ClusterData(
                        galaxy.getId(),
                        connectionServer.getClientServiceAddress(),
                        connectionServer.getClientServicePortPublic(),
                        connectionServer.getPingPort(),

                        privateClient ? galaxy.getOnlinePlayers() : -1,
                        determineGalaxyPopulationStatus(galaxy).toClusterStatusPopulation(),
                        galaxy.getMaxCharactersPerAccount(),
                        galaxy.getTimeZone(),
                        determineGalaxyStatus(galaxy, freeTrialAccount, privateClient).toClusterDataStatus(),

                        false, //TODO: Discuss what should make a galaxy recommended or not.
                        //(record.isNotRecommendedCentral() || record.isNotRecommendedDatabase()),
                        galaxy.getOnlinePlayerLimit(),
                        galaxy.getOnlineFreeTrialLimit()
                );

                LOGGER.debug("Sending cluster status for {}({}) with connection server {}:{}. Online players {}/{} with status {}.",
                        galaxy.getName(),
                        galaxy.getId(),
                        connectionServer.getClientServiceAddress(),
                        connectionServer.getClientServicePortPublic(),
                        galaxy.getOnlinePlayers(),
                        galaxy.getOnlinePlayerLimit(),
                        data.getStatus());

                clusterData.add(data);
            }
        }

        final LoginClusterStatus message = new LoginClusterStatus(clusterData);
        return message;
    }

    @Override
    public void sendClusterStatus(SoeConnection connection) {
        LOGGER.debug("Sending cluster status to client {}.", connection.getSoeUdpConnection().getRemoteAddress());

        final LoginClusterStatus clusterStatusMessage = createLoginClusterStatusMessage();
        connection.sendMessage(clusterStatusMessage);
    }

    @Override
    public void broadcastClusterStatus() {
        LOGGER.trace("Broadcasting cluster status to connected clients.");

        final LoginClusterStatus clusterStatusMessage = createLoginClusterStatusMessage();
        loginConnections.broadcast(clusterStatusMessage);
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

    @Override
    public GalaxyPopulationStatus determineGalaxyPopulationStatus(Galaxy galaxy) {
        final GalaxyPopulationStatus populationStatus;

        //If the limit is 0 or less, then the server is full by default.
        if (galaxy.getOnlinePlayerLimit() <= 0)
            return GalaxyPopulationStatus.FULL;

        final int percentFull = galaxy.getOnlinePlayers() * 100 / galaxy.getOnlinePlayerLimit();

        if (percentFull >= 100) {
            populationStatus = GalaxyPopulationStatus.FULL;
        } else if (percentFull >= loginServerProperties.getPopulationExtremelyHeavyThresholdPercent()) {
            populationStatus = GalaxyPopulationStatus.EXTREMELY_HEAVY;
        } else if (percentFull >= loginServerProperties.getPopulationVeryHeavyThresholdPercent()) {
            populationStatus = GalaxyPopulationStatus.VERY_HEAVY;
        } else if (percentFull >= loginServerProperties.getPopulationHeavyThresholdPercent()) {
            populationStatus = GalaxyPopulationStatus.HEAVY;
        } else if (percentFull >= loginServerProperties.getPopulationMediumThresholdPercent()) {
            populationStatus = GalaxyPopulationStatus.MEDIUM;
        } else if (percentFull >= loginServerProperties.getPopulationLightThresholdPercent()) {
            populationStatus = GalaxyPopulationStatus.LIGHT;
        } else {
            populationStatus = GalaxyPopulationStatus.VERY_LIGHT;
        }

        return populationStatus;
    }

    @Override
    public GalaxyStatus determineGalaxyStatus(Galaxy galaxy, boolean accountIsFreeTrial, boolean clientIsPrivate) {
        GalaxyStatus status;

        if (!galaxy.isIdentified(loginServerProperties.getGalaxyLinkDeadThreshold()))
            return GalaxyStatus.DOWN;

        if (galaxy.isAcceptingConnections()) {
            status = GalaxyStatus.UP;

            //If we've exceeded the allowed number of players in the tutorial, then the galaxy is restricted to
            //existing characters.
            if (galaxy.getOnlineTutorialPlayers() >= galaxy.getOnlineTutorialLimit())
                status = GalaxyStatus.RESTRICTED;

            //If this account is a free trial account and the galaxy doesn't allow free trial accounts, then
            //restrict to existing characters.
            if (accountIsFreeTrial && !galaxy.isCharacterCreationDisabled())
                status = GalaxyStatus.RESTRICTED;

            //If the galaxy is full or this account is a free trial account and the galaxy has its maximum number of
            //free trial accounts connected, then show galaxy as full.
            if (galaxy.getOnlinePlayers() >= galaxy.getOnlinePlayerLimit()
                    || (accountIsFreeTrial && galaxy.getOnlineFreeTrialPlayers() >= galaxy.getOnlineFreeTrialLimit()))
                status = GalaxyStatus.FULL;

        } else {
            status = GalaxyStatus.LOADING;
        }

        //Locked takes precedence over up or loading.
        if (galaxy.isLocked() && !clientIsPrivate) {
            status = GalaxyStatus.LOCKED;
        }

        return status;
    }

    /**
     * This method runs on a schedule and looks for new galaxies in the data store that aren't already in memory. If a
     * new galaxy was somehow added directly to the data store, then it would get loaded this way. At startup, the server
     * will load all trusted galaxies and attempt to poll them for their status.
     */
    //@Scheduled(initialDelay = 0, fixedRate = GALAXY_MAINTENANCE_INTERVAL)
    private void performGalaxyMaintenance() {
        LOGGER.trace("Refreshing galaxy server list.");

        final Iterable<Galaxy> records = galaxyRepository.findAll();

        for (final Galaxy record : records) {
            try {
                if (this.loadedGalaxies.containsKey(record.getName())) {
                    continue;
                }

                loadGalaxy(record.getName());
            } catch (Exception ex) {
                LOGGER.error("Error loading galaxy.", record.getName());
            }
        }

        this.broadcastClusterStatus();
    }

    private void scheduleMaintenanceTasks() {
        LOGGER.trace("Scheduling galaxy service maintenance tasks.");

        //Schedule the galaxy maintenance task based on our login server configuration.
        this.taskScheduler.scheduleAtFixedRate(
                this::performGalaxyMaintenance,
                Duration.ofMillis(loginServerProperties.getGalaxyMaintenanceInterval()));
    }
}