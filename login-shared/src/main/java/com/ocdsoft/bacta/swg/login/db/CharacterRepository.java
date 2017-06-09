package com.ocdsoft.bacta.swg.login.db;

import com.ocdsoft.bacta.swg.login.object.CharacterInfo;

import java.util.List;

/**
 * Created by crush on 6/8/2017.
 * <p>
 * A repository of the characters of which the Login Server is aware.
 * <p>
 * The game server is responsible for sending updates concerning a character to the LoginServer. This includes name
 * and object template changes for the character as well as notification of deletion. Characters may also be flagged
 * as enabled or disabled by the game server.
 */
public interface CharacterRepository {
    /**
     * Creates or updates the character with the given id into the repository.
     *
     * @param character The character info to create or update.
     * @return The character that was passed in.
     */
    CharacterInfo put(CharacterInfo character);

    /**
     * Gets all the characters in the repository.
     *
     * @return A list of all the characters in the repository.
     */
    List<CharacterInfo> get();

    /**
     * Gets all the characters of a specific cluster from the repository.
     *
     * @param clusterId The id of the cluster from which to get characters.
     * @return A list of all the characters in the specified cluster.
     */
    List<CharacterInfo> getByClusterId(int clusterId);

    /**
     * Gets the specific character for the given character id.
     *
     * @param characterId The id of the character to retrieve.
     * @return The character to be retrieved, or null if it doesn't exist.
     */
    CharacterInfo get(long characterId);

    /**
     * Deletes all the characters in the repository.
     */
    void delete();

    /**
     * Deletes the given character from the repository.
     *
     * @param characterId The id of the character.
     */
    void delete(long characterId);

    /**
     * Deletes all characters on a specific cluster.
     *
     * @param clusterId The cluster which should have characters purged.
     */
    void deleteByClusterId(int clusterId);
}
