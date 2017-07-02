package com.ocdsoft.bacta.soe.network.connection;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
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
