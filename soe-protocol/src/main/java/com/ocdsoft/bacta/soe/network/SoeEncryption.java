package com.ocdsoft.bacta.soe.network;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 7/1/2017.
 */
public interface SoeEncryption {
    byte getEncryptionID();

    ByteBuffer decode(int seed, ByteBuffer data);

    ByteBuffer encode(int seed, ByteBuffer data, boolean doCompress);

    void appendCRC(int nCrcSeed, ByteBuffer data, int crcLength);

    boolean validate(int seed, ByteBuffer data);

    void setCompression(boolean compression);

    boolean isCompression();
}
