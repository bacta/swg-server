package com.ocdsoft.bacta.swg.protocol.connection;

import lombok.Data;

/**
 * Created by kburkhardt on 2/7/15.
 enum Configuration 
 {
     int encryptCode;
     int crcBytes;
     UdpManager::EncryptMethod encryptMethod[2];
     int maxRawPacketSize;
 }; 
 */

@Data
public class Configuration {

    private int protocolVersion;
    private int encryptCode;
    private int crcBytes;
    private EncryptMethod encryptMethod;
    private int maxRawPacketSize;
    private boolean compression;
    
    public Configuration(final int protocolVersion,
                         final int crcBytes,
                         final EncryptMethod encryptMethod,
                         final int maxRawPacketSize,
                         final boolean compression) {

        this.protocolVersion = protocolVersion;
        this.crcBytes = crcBytes;
        this.encryptMethod = encryptMethod;
        this.maxRawPacketSize = maxRawPacketSize;
        this.compression = compression;
    }
}
