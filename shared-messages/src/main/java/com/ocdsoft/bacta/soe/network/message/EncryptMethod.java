package com.ocdsoft.bacta.soe.network.message;

/**
 * Created by kburkhardt on 2/7/15.
 */

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import java.nio.ByteBuffer;

/**
 enum UdpManager::EncryptMethod
 {
     cEncryptMethodNone = 0x0,
     cEncryptMethodUserSupplied = 0x1,
     cEncryptMethodUserSupplied2 = 0x2,
     cEncryptMethodXorBuffer = 0x3,
     cEncryptMethodXor = 0x4,
     cEncryptMethodCount = 0x5,
 };
 */
public enum EncryptMethod implements ByteBufferWritable {
    NONE(0x0),
    USERSUPPLIED(0x1),
    USERSUPPLIED2(0x2),
    XORBUFFER(0x3),
    XOR(0x4),
    COUNT(0x5);

    private final byte value;

    EncryptMethod(int i) {
         this.value = (byte) i;
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.put(value);
    }
}
