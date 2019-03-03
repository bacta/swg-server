package io.bacta.login.server.service;

import io.bacta.galaxy.message.GalaxyServerStatus;
import io.bacta.login.server.model.Galaxy;
import io.bacta.login.server.model.GalaxyPopulationStatus;
import io.bacta.login.server.model.GalaxyStatus;
import io.bacta.soe.context.SoeRequestContext;

import java.security.NoSuchAlgorithmException;
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
 * mapper rules to your login server, then you can safely leave this option enabled.
 */
public interface GalaxyService {
    /**
     * Gets all galaxies in the galaxy cluster.
     *
     * @return A collection of galaxies in the cluster. If none exist, then an empty collection is returned. The
     * collection is immutable.
     */
    Collection<Galaxy> getGalaxies();

    /**
     * Gets a galaxy by its unique identifier.
     *
     * @param name The name of the galaxy.
     * @return Returns the galaxy with the given name.
     * @throws GalaxyNotFoundException If the galaxy was not found.
     */
    Galaxy getGalaxy(String name) throws GalaxyNotFoundException;

    /**
     * Dynamically calculates the availability status based on the current data for the galaxy.
     *
     * @param galaxy             The galaxy for which to determine status.
     * @param accountIsFreeTrial If the account is a free trial account.
     * @param clientIsPrivate    If the client being used is private (internal).
     * @return A status enum value.
     */
    GalaxyStatus determineGalaxyStatus(Galaxy galaxy, boolean accountIsFreeTrial, boolean clientIsPrivate);

    /**
     * Dynamically calculates the population status based on the current data for the galaxy.
     *
     * @param galaxy The galaxy for which to determine population status.
     * @return A population status enum value.
     */
    GalaxyPopulationStatus determineGalaxyPopulationStatus(Galaxy galaxy);

    /**
     * Adds a galaxy to the galaxy cluster. This makes the galaxy "trusted" which means that the login server will
     * communicate with the galaxy and offer connections to the galaxy when it is available. The galaxy is registered
     * with default settings, but it can update those settings by issuing a {@link io.bacta.galaxy.message.GalaxyServerStatus}
     * message to the login server after having identified with a {@link io.bacta.galaxy.message.GalaxyServerId} message.
     *
     * @param name     The name of the galaxy.
     * @param address  The address to contact the galaxy server.
     * @param port     The port to contact the galaxy server.
     * @param timeZone The timezone as an offset of GMT.
     * @return
     * @throws GalaxyNotFoundException           If the galaxy could not be found for loading after being registered.
     * @throws GalaxyRegistrationFailedException If a galaxy already exists with the same address and port.
     * @throws NoSuchAlgorithmException          If the required algorithms were not present for generating keys.
     */
    Galaxy registerGalaxy(String name, String address, int port, int timeZone)
            throws GalaxyNotFoundException, GalaxyRegistrationFailedException, NoSuchAlgorithmException;

    /**
     * Removes a galaxy from the galaxy cluster.
     *
     * @param name The name of the galaxy which should be removed.
     */
    void unregisterGalaxy(String name) throws GalaxyNotFoundException;

    /**
     * A status update from a galaxy.
     *
     * @param galaxyName The galaxy name to lookup. This may be different than what's in the update.
     * @param update     The update from the galaxy.
     * @throws GalaxyNotFoundException           If the galaxy was not found.
     * @throws GalaxyRegistrationFailedException If the galaxy name in the status update is already taken.
     */
    void updateGalaxyStatus(String galaxyName, GalaxyServerStatus update)
            throws GalaxyNotFoundException, GalaxyRegistrationFailedException;

    /**
     * Sends the enumeration of all known galaxies in the cluster, even if they are offline. This takes into account
     * the permissions of the connection. For example, secret galaxies will not be sent to a client that is not on
     * the local network.
     *
     * @param context The connection which will receive the message.
     */
    void sendClusterEnum(SoeRequestContext context);

    /**
     * Sends a list of any galaxies that have disabled character creation.
     *
     * @param context The connection which will receive the message.
     */
    void sendDisabledCharacterCreationServers(SoeRequestContext context);

    /**
     * Sends the status of all active galaxies to a specific connection.
     *
     * @param context The connection which will receive the message.
     */
    void sendClusterStatus(SoeRequestContext context);

    /**
     * Sends out the cluster status message to all connections that are currently connected to the login server. This
     * allows the clients real time updates on the available galaxies.
     */
    void broadcastClusterStatus();

    /**
     * Sends extended status of all active galaxies to specific connection.
     *
     * @param context The connection which will receive the message.
     */
    void sendExtendedClusterStatus(SoeRequestContext context);
}