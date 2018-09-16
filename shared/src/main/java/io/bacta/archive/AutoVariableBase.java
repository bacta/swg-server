package io.bacta.archive;

import java.nio.ByteBuffer;

/**
 * Created by crush on 8/13/2014.
 */
public interface AutoVariableBase {
    void pack(ByteBuffer buffer);
    void unpack(ByteBuffer buffer);
}
