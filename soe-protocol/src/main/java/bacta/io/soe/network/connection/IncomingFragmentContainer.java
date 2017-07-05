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

package bacta.io.soe.network.connection;

import bacta.io.buffer.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/29/2016.
 */
class IncomingFragmentContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingFragmentContainer.class);

    private ByteBuffer incompleteMessage;
    private int completedSize;

    IncomingFragmentContainer() {
        incompleteMessage = null;
        completedSize = 0;
    }

    ByteBuffer addFragment(final ByteBuffer buffer) {

        if(incompleteMessage == null) {
            completedSize = buffer.getInt();
            incompleteMessage = buffer.slice();
            incompleteMessage.position(incompleteMessage.limit());
            LOGGER.debug("New Fragment started, size: {}/{}", incompleteMessage.limit(), completedSize);
            return null;
        }

        incompleteMessage = BufferUtil.combineBuffers(incompleteMessage, buffer);
        LOGGER.debug("Fragment added, current size: {}/{}", incompleteMessage.position(), completedSize);

        if(incompleteMessage.position() == completedSize) {
            final ByteBuffer outgoingBuffer = incompleteMessage;
            incompleteMessage = null;
            completedSize = 0;
            outgoingBuffer.rewind();
            return outgoingBuffer;
        }

        return null;
    }

}
