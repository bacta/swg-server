package io.bacta.login.server.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents a galaxy in the login server's database.
 */
@Data
@RequiredArgsConstructor
public final class GalaxyRecord {
    /**
     * Unique identifier for this galaxy. No other galaxy should share this id.
     */
    private final int id;
    /**
     * The name of the galaxy. This may be changed by the galaxy itself when it identifies.
     */
    private String name;
    /**
     * The address from which this login server may expect the galaxy to identify.
     */
    private String address;
    /**
     * The port that goes with the address of the galaxy server.
     */
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
     * Whether or not to allow character creation for free trial accounts.
     */
    private boolean allowFreeTrialCharacterCreation;
}