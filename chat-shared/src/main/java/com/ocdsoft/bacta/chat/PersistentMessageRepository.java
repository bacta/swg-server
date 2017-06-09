package com.ocdsoft.bacta.chat;

import java.util.List;

/**
 * Created by crush on 6/7/2017.
 */
public interface PersistentMessageRepository {
    PersistentMessage create(PersistentMessage message);

    PersistentMessage update(PersistentMessage message);

    int delete(int messageId);

    /**
     * Deletes all the messages for a given network id.
     * @param networkId The network id to which the messages belong.
     */
    void deleteAll(long networkId);

    /**
     * Gets all persistent messages that belong to the given network id.
     * @param networkId The id to which the persistent messages belong.
     * @return A list of network ids, or an empty list if none exist.
     * @throws ArrayIndexOutOfBoundsException If the network id is unknown.
     */
    List<PersistentMessage> getAll(long networkId);

    /**
     * Gets a single persistent message with the corresponding message id.
     * @param messageId The id of the message to get.
     * @return The persistent message if it exists. Otherwise, null.
     */
    PersistentMessage get(int messageId);
}
