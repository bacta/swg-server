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

package io.bacta.soe.config;

import lombok.Getter;

/**
 * Created by kyle on 6/28/2017.
 */
@Getter
public class SoeUdpConfiguration {

    private final int protocolVersion;
    private final int crcBytes;
    private int maxRawPacketSize;
    private final boolean compression;
    private int encryptCode;

    public SoeUdpConfiguration(int protocolVersion, int crcBytes, int maxRawPacketSize, boolean compression) {
        this.protocolVersion = protocolVersion;
        this.crcBytes = crcBytes;
        this.maxRawPacketSize = maxRawPacketSize;
        this.compression = compression;
    }

    public SoeUdpConfiguration(int protocolVersion, int crcBytes, int maxRawPacketSize, boolean compression, int encryptCode) {
        this(protocolVersion, crcBytes, maxRawPacketSize, compression);
        this.encryptCode = encryptCode;
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
