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

package io.bacta.connection.message;

import io.bacta.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 7/4/2017.
 *
 * ConnectionServer to CentralServer and LoginServer. Updates how many players are currently connected
 * to the connection server.
 */
@Getter
@Priority(0x04)
@AllArgsConstructor
public final class UpdatePlayerCountMessage extends GameNetworkMessage{
    private final boolean loadedRecently;
    private final int playerCount;
    private final int freeTrialCount;
    private final int emptySceneCount;
    private final int tutorialSceneCount;
    private final int falconSceneCount;

    public UpdatePlayerCountMessage(ByteBuffer buffer) {
        loadedRecently = BufferUtil.getBoolean(buffer);
        playerCount = buffer.getInt();
        freeTrialCount = buffer.getInt();
        emptySceneCount = buffer.getInt();
        tutorialSceneCount = buffer.getInt();
        falconSceneCount = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, loadedRecently);
        BufferUtil.put(buffer, playerCount);
        BufferUtil.put(buffer, freeTrialCount);
        BufferUtil.put(buffer, emptySceneCount);
        BufferUtil.put(buffer, tutorialSceneCount);
        BufferUtil.put(buffer, falconSceneCount);
    }
}
