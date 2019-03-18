package io.bacta.soe.network.connection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to break large messages into smaller fragments
 */
public class FragmentUtil {
    private FragmentUtil() {}

    /**
     * Take a byte buffer and break it into fragments that are the appropriate size based on max payload
     * @param buffer buffer to be sliced
     * @param maxPayload maximum slice size
     * @return Collection of {@link ByteBuffer}
     */
    static List<ByteBuffer> createFragments(ByteBuffer buffer, int maxPayload) {

        List<ByteBuffer> fragments = new ArrayList<>();

        while(buffer.hasRemaining()) {

            int messageSize;

            if (buffer.remaining() > maxPayload) {
                messageSize = maxPayload;
            } else {
                messageSize = buffer.remaining();
            }

            // Leave room for the overall fragment size in the first message
            if (fragments.isEmpty()) {
                messageSize -= 4;
            }

            ByteBuffer slice = buffer.slice();
            slice.limit(messageSize);

            buffer.position(buffer.position() + messageSize);

            fragments.add(slice);
        }
        return fragments;
    }
}
