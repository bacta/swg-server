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

package io.bacta.login.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Priority(0x4)
public final class LoginClientToken extends GameNetworkMessage {
    private final String authToken;
    private final int bactaId;
    private final String username;

    public LoginClientToken(final ByteBuffer buffer) {
        authToken = BufferUtil.getBinaryString(buffer);
        bactaId = buffer.getInt();
        username = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putBinaryString(buffer, authToken);
        buffer.putInt(bactaId);
        BufferUtil.putAscii(buffer, username);
        //This extra information shouldn't exist...
        //buffer.put((byte) 0);
        //buffer.putShort((short) 0);
    }

    /**  Example
	 *  04 00 
	 *  C6 96 B2 AA 
	 *  08 00 00 00 
	 *  EF F8 3C 5D 
	 *  66 28 00 00 
	 *  40 D9 52 76 
	 *  04 00 4B 79 6C 65 
	 *  00 
	 *  00 00 
	 */
}
