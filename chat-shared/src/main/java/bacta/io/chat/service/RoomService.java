package bacta.io.chat.service;

import bacta.io.chat.ChatAvatarId;

/**
 * Created by crush on 6/25/2017.
 */
public interface RoomService {
    void createRoom();
    void destroyRoom(long networkId, int sequenceId, int roomId);

    void enterRoom();
    void leaveRoom();

    void banFromRoom(ChatAvatarId avatarId);
    void unbanFromRoom(ChatAvatarId avatarId);
    void kickFromRoom(ChatAvatarId avatarId);
    void inviteToRoom(ChatAvatarId avatarId);
    void uninviteFromRoom(ChatAvatarId avatarId);

    void sendMessage(ChatAvatarId from, int roomId, String message, String outOfBand);
}
