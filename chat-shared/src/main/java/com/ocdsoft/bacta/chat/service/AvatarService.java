package com.ocdsoft.bacta.chat.service;

import com.ocdsoft.bacta.chat.AvatarDoesNotExistException;
import com.ocdsoft.bacta.chat.AvatarNotConnectedException;
import com.ocdsoft.bacta.chat.ChatAvatarId;

/**
 * Created by crush on 6/25/2017.
 * <p>
 * The avatar service is responsible for maintaining a list of what players have connected to the chat service, as well
 * as maintaining a mapping between their GameServer character networkId and their chat server avatarId. Other chat
 * services rely on the AvatarService for resolving networkId and avatarId relationships.
 * <p>
 * When a new player logs in, they should connect with the chat service, registering that they are ready to participate
 * in the various chat services. Likewise, as their connection to the GameServer changes, the chat service should
 * potentially disconnect the player.
 */
public interface AvatarService {
    ChatAvatarId getSystemAvatarId();

    ChatAvatarId lookupAvatarId(long networkId);

    long lookupNetworkId(ChatAvatarId avatarId);

    int getConnectionId(ChatAvatarId avatarId);

    int getConnectionId(long networkId);

    /**
     * Connects a player to the chat service. This indicates that the player is ready to participate in the various
     * chat services. When a player connects, other services will be notified of the connection.
     *
     * @param connectionId    The connection with which the player has connected to the game server.
     * @param characterName The name of the character connecting.
     * @param networkId     The id of the character on the game server.
     * @param isSecure      If the connection is using a secure protocol.
     * @param isSubscribed  If the account is subscribed.
     * @throws AvatarDoesNotExistException If the chat service believes the character is already connected, but the
     *                                     disconnect attempt fails due to the chat service having no record of the avatar.
     * @throws AvatarNotConnectedException If the chat service believes the character is already connected, but the
     *                                     disconnect attempt fails due to the player not being connected.
     */
    void connectPlayer(int connectionId, String characterName, long networkId, boolean isSecure, boolean isSubscribed)
            throws AvatarDoesNotExistException, AvatarNotConnectedException;

    /**
     * Disconnects a player from the chat service.
     *
     * @param networkId The networkId for the player. This id should be known to the chat service.
     * @throws AvatarDoesNotExistException If the player is not known by the chat service.
     * @throws AvatarNotConnectedException If the player is not connected to the chat service.
     */
    void disconnectPlayer(long networkId) throws AvatarDoesNotExistException, AvatarNotConnectedException;

    /**
     * Disconnects a player via their avatarId from the chat service.
     *
     * @param avatarId The avatarId for the player. This id should be known to the chat service.
     * @throws AvatarDoesNotExistException If the player is not known by the chat service.
     * @throws AvatarNotConnectedException If the player is not connected to the chat service.
     */
    void disconnectAvatar(ChatAvatarId avatarId) throws AvatarDoesNotExistException, AvatarNotConnectedException;
}
