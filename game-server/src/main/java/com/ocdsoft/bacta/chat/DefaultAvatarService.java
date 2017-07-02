package com.ocdsoft.bacta.chat;

import com.ocdsoft.bacta.chat.message.ChatOnConnectAvatar;
import com.ocdsoft.bacta.chat.service.AvatarService;
import com.ocdsoft.bacta.connection.ConnectionService;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crush on 6/28/2017.
 * <p>
 * The default implementation of the server avatar service. It offers various hooks through the ChatInterface interface
 * that allows additional logic to be plugged into the flow. For this reason, it's unlikely that a developer would ever
 * need to make their own implementation of AvatarService.
 * </p>
 * <h1>Connecting Players</h1>
 * <p>
 * A player has successfully connected to the AvatarService when he is located in the <code>connectedAvatars</code> set.
 * When a player disconnects from the connection server, it should notify the ConnectionService on the same game server
 * as this AvatarService. In turn, the ConnectionService will notify the AvatarService of the player's disconnection.
 * At this point, the AvatarService removes the player from the <code>connectedAvatars</code> set, via the
 * {@link #disconnectPlayer(long)} method.
 * </p>
 */
@Slf4j
@Service
public final class DefaultAvatarService implements AvatarService {
    private static final String SYSTEM_AVATAR_NAME = "SYSTEM";
    private static final String GAME_CODE = "SWG";

    private transient final ConnectionService connectionService;
    private transient final ChatInterface chatInterface;
    /**
     * A map of the connections to the chat service. These connections may still be trying to connect, in which case
     * their existence in this map will prevent them from trying to connect a second time before the first attempt
     * finishes.
     */
    private transient final TLongIntMap clientMap;
    /**
     * Avatars pending connection to the chat service.
     */
    private transient final TObjectLongMap<String> pendingAvatars;

    private transient final Map<ChatAvatarId, ChatAvatarOwner> connectedAvatars;
    private transient ChatAvatarId systemAvatarId;
    private transient String clusterName;

    public DefaultAvatarService(ConnectionService connectionService, ChatInterface chatInterface) {
        this.connectionService = connectionService;
        this.chatInterface = chatInterface;

        this.clientMap = new TLongIntHashMap();
        this.pendingAvatars = new TObjectLongHashMap<>();
        this.connectedAvatars = new HashMap<>();

        this.clusterName = "bacta"; //TODO: Should come from configuration or be told by connection server.
    }

    @Override
    public void connectPlayer(int connectionId, String characterName, long networkId, boolean isSecure, boolean isSubscribed) {
        characterName = ensureLowerFirstName(characterName);

        LOGGER.info("Connecting player {}(networkId: {}, connectionId: {}).", characterName, networkId, connectionId);

        //If they already exist in the client map, then just overwrite the connection associated with the networkId
        //because we are already trying to connect them.
        if (clientMap.containsKey(networkId)) {
            LOGGER.warn("Received attempt for player {} who is already connected or in the process of connecting.", characterName);
            clientMap.put(networkId, connectionId);
            return;
        }

        clientMap.put(networkId, connectionId);
        pendingAvatars.put(characterName, networkId);

        final ChatResult loginResult = chatInterface.loginAvatar(characterName, networkId);

        //Login timed out. Try again.
        if (loginResult == ChatResult.TIMEOUT) {
            connectPlayer(connectionId, characterName, networkId, isSecure, isSubscribed);
            return;
        }

        final ChatAvatarId avatarId = new ChatAvatarId(GAME_CODE, clusterName, characterName);

        //Login failed.
        if (loginResult != ChatResult.SUCCESS) {
            LOGGER.warn("Failed to login avatar {}. Reason: {}.", avatarId, loginResult);
            pendingAvatars.remove(characterName);
            clientMap.remove(networkId);
            return;
        }

        //Login succeeded.
        if (systemAvatarId.equals(avatarId)) {
            //The system avatar is the one connecting to the chat server.
            LOGGER.info("SYSTEM avatar connected.");

            //chatInterface.getRoomSummaries();
        } else {
            if (pendingAvatars.containsKey(characterName)) {
                final ChatAvatarOwner owner = new ChatAvatarOwner(connectionId, networkId);
                connectedAvatars.put(avatarId, owner);

                //TODO: Flush any pending chat messages for the avatar.
                //get persistent message headers.
                pendingAvatars.remove(characterName);
            } else {
                //They disconnected before the login attempt completed.
                //Go ahead and log them out of the authoritative chat server.
                chatInterface.logoutAvatar(avatarId);
            }
        }

        final ChatOnConnectAvatar connectMessage = new ChatOnConnectAvatar();
        connectionService.send(connectionId, connectMessage);

        //TODO: Metrics
        //TODO: Track God Clients
        //TODO: Track NonSubscribed Clients
        //LOGGER.info("Connected player successfully. (avatarId: {})", avatarId.toString());
    }

    @Override
    public void disconnectPlayer(long networkId) {
        //Lookup the chat avatar id,
        //remove them from the client map
        //metrics
        //remove from god clients
        //remove from not subscribed
    }

    @Override
    public void disconnectAvatar(ChatAvatarId avatarId) {

    }

    /**
     * Looks for a space in the name. If it finds one, then it will break the name before the space. Lowercases the name
     * in all circumstances.
     *
     * @param name The name to be lower cased and split into first name only.
     * @return The processed name.
     */
    private static String ensureLowerFirstName(final String name) {
        final int whitespaceIndex = name.indexOf(' ');

        //If there is no whitespace, just lowercase the name and return it.
        if (whitespaceIndex == -1) {
            return name.toLowerCase();
        }

        final String firstName = name.substring(0, whitespaceIndex);
        return firstName.toLowerCase();
    }

    @Getter
    @AllArgsConstructor
    private final class ChatAvatarOwner {
        private final int connectionId;
        private final long networkId;
    }
}
