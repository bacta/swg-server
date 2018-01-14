package io.bacta.login.server.service;

import com.google.common.collect.ImmutableList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.galaxy.message.GalaxyServerId;
import io.bacta.login.message.LoginServerOnline;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.data.GalaxyRecord;
import io.bacta.login.server.repository.GalaxyRepository;
import io.bacta.soe.network.connection.ConnectionMap;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Curates a list of galaxies that this LoginServer can service.
 * <p>
 * By default, the login server will only service galaxies that have been registered in its database. This establishes
 * a trust relationship between the login server and the galaxy server. Without this trust relationship, it would be
 * impossible to verify that a galaxy server belongs to the galaxy cluster.
 * <p>
 * <h2>Galaxy Registration</h2>
 * <p>
 * Galaxy Registration is the process of a galaxy server establishing a trust relationship with the login server. When
 * a galaxy server comes online, it is expected to announce its online state to the login server. This is how the login
 * server knows to offer connections to that specific galaxy server. In response to a galaxy server announcing itself
 * to the login server, the login server will send a response with the galaxy server's unique identifier in the login
 * server's galaxy cluster. If the galaxy server is not recognized, or untrusted, then the login server will ignore the
 * request from the galaxy server.
 * <p>
 * In order for a galaxy to be recognized, or trusted, it must first be entered into the login server's galaxy cluster
 * database. Required information must be present for a galaxy server to be trusted. First, a unique, immutable id must
 * exist for the galaxy server. Additionally, the name, address, and port are required. Other information may be set by
 * the galaxy server when it successfully identifies with the login server by announcing its online status.
 * <p>
 * Although there have been discussions about requiring a cryptographic key to be involved in the identification process,
 * currently there are no plans to implement this added security measure. Therefore, it is advisable that galaxy servers
 * are located on the same network as the login server. If a galaxy server that you want included in your login server
 * exists outside of the network of the login server, you should seek to create a VPN between the login server and the
 * galaxy server so that the network may be trusted. This recommendation can help safeguard against attempts to hijack
 * a galaxy server by assuming its identity with address and port spoofing.
 * <p>
 * <h2>Automatic Galaxy Registration</h2>
 * <p>
 * A configuration setting exists that enables automatic galaxy registration. This means that when a galaxy announces
 * itself for the first time, the login server will automatically add it to the galaxy cluster database, assigning it
 * a unique identifier and recording its name, address, and port.
 * <p>
 * <b>This mechanism is meant for local development servers only because it allows any galaxy server to register itself
 * without approval.</b> It is highly advisable to disable this setting in a production environment or run the risk of
 * untrusted galaxies registering with your galaxy cluster. If you are running a local LAN server with no external port
 * mapping rules to your login server, then you can safely leave this option enabled.
 */
@Slf4j
@Service
public final class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final LoginServerProperties loginServerProperties;
    private final ConnectionMap connectionMap;
    /**
     * This is a map of galaxies that are currently "online" meaning that they have identified with the login server
     * via the {@link io.bacta.galaxy.message.GalaxyServerId} message.
     */
    private final TIntObjectMap<GalaxyRecord> galaxies;

    public GalaxyService(GalaxyRepository galaxyRepository,
                         LoginServerProperties loginServerProperties,
                         @Qualifier("LoginConnectionMap") ConnectionMap connectionMap) {
        this.galaxyRepository = galaxyRepository;
        this.loginServerProperties = loginServerProperties;
        this.galaxies = new TIntObjectHashMap<>();
        this.connectionMap = connectionMap;
    }

    /**
     * This method runs on a schedule and looks for new galaxies that aren't in memory. If a new galaxy was somehow
     * added directly to the database, then it would get loaded this way. At startup, it will load all trusted
     * galaxies and send each a {@link LoginServerOnline} message to tell them that the login server is online and
     * waiting for their {@link GalaxyServerId} message.
     */
    @Scheduled(initialDelay = 0, fixedRate = 10000)
    private void refreshGalaxyServerList() {
        final Iterable<GalaxyRecord> records = galaxyRepository.findAll();

        for (GalaxyRecord record : records) {
            //We want to look if this galaxy is already in our map. If so, then ignore it.
            if (galaxies.containsKey(record.getId())) {
                continue;
            }

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

            //Add the galaxy to our memory map.
            galaxies.put(record.getId(), record);
        }
    }

    public Collection<GalaxyRecord> getGalaxies() {
        return ImmutableList.copyOf(galaxies.valueCollection());
    }

    public GalaxyRecord getGalaxyById(int id) {
        return galaxies.get(id);
    }

    /**
     * First time registration with the login server by a galaxy. This method shouldn't be called directly. It gets
     * called as a result of other processes. For example, if a galaxy sends a {@link GalaxyServerId} message, the
     * login server doesn't yet trust the galaxy, but AutoGalaxyRegistration is enabled, then this method would get
     * called.
     *
     * Likewise, it may get called by the Rest service when an authorized user requests to create a galaxy there.
     *
     * @param name The name of the galaxy that is registering.
     * @param address The host or ip address where the galaxy server may be reached.
     * @param port The port the galaxy server is operating on at the provided address.
     * @param timeZone The timezone in which the galaxy server resides. This is the offset from GMT.
     * @return A new galaxy record with a galaxy id if it was successfully registered.
     * @throws GalaxyRegistrationFailedException If a galaxy with the same address and port is already registered.
     */
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
}