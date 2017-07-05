package bacta.io.soe.config;

import bacta.io.soe.network.message.EncryptMethod;
import lombok.Getter;

/**
 * Created by kyle on 6/28/2017.
 */
@Getter
public class SoeUdpConfiguration {

    private final int protocolVersion;
    private final int crcBytes;
    private final EncryptMethod encryptMethod;
    private int maxRawPacketSize;
    private final boolean compression;
    private int encryptCode;

    public SoeUdpConfiguration(int protocolVersion, int crcBytes, EncryptMethod encryptMethod, int maxRawPacketSize, boolean compression) {
        this.protocolVersion = protocolVersion;
        this.crcBytes = crcBytes;
        this.encryptMethod = encryptMethod;
        this.maxRawPacketSize = maxRawPacketSize;
        this.compression = compression;
    }

    public void setEncryptCode(int code) {
        this.encryptCode = code;
    }

    public void setMaxRawPacketSize(int maxRawPacketSize) {
        this.maxRawPacketSize = maxRawPacketSize;
    }

    public int getEncryptCode() {
        return encryptCode;
    }
}
