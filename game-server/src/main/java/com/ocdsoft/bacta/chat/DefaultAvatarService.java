package com.ocdsoft.bacta.chat;

import com.ocdsoft.bacta.chat.service.AvatarService;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by crush on 6/28/2017.
 */
@Slf4j
@Service
public final class DefaultAvatarService implements AvatarService {
    private final TLongObjectMap<ChatAvatarId> networkIdToAvatarIdMap;
    private final TObjectLongMap<ChatAvatarId> avatarIdToNetworkIdMap;
    private final Set<ConnectedAvatarEntry> connectedAvatars;

    public DefaultAvatarService() {
        this.networkIdToAvatarIdMap = new TLongObjectHashMap<>();
        this.avatarIdToNetworkIdMap = new TObjectLongHashMap<>();
        this.connectedAvatars = new HashSet<>();
    }

    @Override
    public ChatAvatarId getSystemAvatarId() {
        return null;
    }

    @Override
    public ChatAvatarId lookupAvatarId(long networkId) {
        return null;
    }

    @Override
    public long lookupNetworkId(ChatAvatarId avatarId) {
        return 0;
    }

    @Override
    public int getConnectionId(ChatAvatarId avatarId) {
        return 0;
    }

    @Override
    public int getConnectionId(long networkId) {
        return 0;
    }

    @Override
    public void connectPlayer(int connectionId, String characterName, long networkId, boolean isSecure, boolean isSubscribed) throws AvatarDoesNotExistException, AvatarNotConnectedException {

    }

    @Override
    public void disconnectPlayer(long networkId) throws AvatarDoesNotExistException, AvatarNotConnectedException {

    }

    @Override
    public void disconnectAvatar(ChatAvatarId avatarId) throws AvatarDoesNotExistException, AvatarNotConnectedException {

    }

    @Getter
    @AllArgsConstructor
    private final class ConnectedAvatarEntry {
        private final int connectionId;
        private final long networkId;
        private final ChatAvatarId avatarId;
    }
}
