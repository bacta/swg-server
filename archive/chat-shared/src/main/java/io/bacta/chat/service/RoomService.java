/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.chat.service;

import io.bacta.chat.ChatAvatarId;

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
