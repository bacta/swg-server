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

package io.bacta.galaxy.message;

import bacta.io.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 7/4/2017.
 * <p>
 * GalaxyServer to LoginServer informing LoginServer to add teh specified character to the login database
 * and reply with LoginCreateCharacterAckMessage.
 */
@Getter
@AllArgsConstructor
public final class LoginCreateCharacterMessage extends GameNetworkMessage {
    private final int bactaId;
    private final String characterName;
    private final long characterObjectId;
    private final int templateId;
    private final boolean jedi;

    public LoginCreateCharacterMessage(ByteBuffer buffer) {
        bactaId = buffer.getInt();
        characterName = BufferUtil.getUnicode(buffer);
        characterObjectId = buffer.getLong();
        templateId = buffer.getInt();
        jedi = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, bactaId);
        BufferUtil.putUnicode(buffer, characterName);
        BufferUtil.put(buffer, characterObjectId);
        BufferUtil.put(buffer, templateId);
        BufferUtil.put(buffer, jedi);
    }
}
