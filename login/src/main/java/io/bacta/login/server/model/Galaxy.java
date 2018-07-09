package io.bacta.login.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Entity
@Table(name = "galaxies")
public class Galaxy {
    /**
     * Unique identifier for this galaxy. No other galaxy should share this id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The name of the galaxy. This may be changed by the galaxy itself when it identifies.
     */
    @Column(unique = true, nullable = false)
    private String name;
    /**
     * The address from which this login server may expect the galaxy to identify.
     */
    @Column(nullable = false)
    private String address;
    /**
     * The port that goes with the address of the galaxy server.
     */
    @Column(nullable = false)
    private int port;
    /**
     * The timezone within which this galaxy is located. This is the offset from GMT.
     */
    private int timeZone;
    /**
     * The maximum number of characters that may be created on this galaxy before character creation
     * is disabled for everyone.
     */
    private int maxCharacters;
    /**
     * The maximum number of characters that may be created per account for this galaxy.
     */
    private int maxCharactersPerAccount;
    /**
     * The total number of players who are allowed to play on this galaxy concurrently.
     */
    private int onlinePlayerLimit;
    /**
     * The total number of players who may be in the tutorial area while online. If this number is exceeded,
     * then new players attempting to create a character that would end up in the tutorial are told the must wait.
     */
    private int onlineTutorialLimit;
    /**
     * The total number of players who may be online with a free trial account.
     */
    private int onlineFreeTrialLimit;
    /**
     * Whether or not to allow character creation for any accounts.
     */
    private boolean characterCreationEnabled;

    private String branch;
    private String networkVersion;
    private int changeList;



    //Transient Data - set after read from database.

    /**
     * Signals that this galaxy has identified. This means it is essentially online, or connected.
     */
    private transient boolean identified;
    /**
     * The galaxy is ready for clients to start connecting. If it is also in a locked state, then only privileged
     * clients can connect.
     */
    private transient boolean acceptingConnections;
    /**
     * The galaxy will not be available to clients that are outside of the local network.
     */
    private transient boolean secret;
    /**
     * The galaxy is accepting connections, but is in a locked state. Only privileged clients may connect.
     */
    private transient boolean locked;
    /**
     * Connection servers for this galaxy. To get the total number of players for this server, will get need tally up
     * all these connection servers player totals.
     */
    private final transient SortedSet<ConnectionServerEntry> connectionServers
            = new TreeSet<>(new ConnectionServerEntry.LeastPopulationComparator());
    /**
     * How many players were last reported by the galaxy server. Notice, this may be different than the totals from the
     * connection servers for the same galaxy. It is more of a rough estimate, and is not updated on every successful
     * connection.
     */
    private transient int onlinePlayers;
    /**
     * How many players were last reported in the tutorial area of the galaxy server.
     */
    private transient int onlineTutorialPlayers;
    /**
     * How many players were last reported as being free trial players.
     */
    private transient int onlineFreeTrialPlayers;

    public Galaxy(final String name, final String address, int port, int timeZone) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.timeZone = timeZone;
    }
}
