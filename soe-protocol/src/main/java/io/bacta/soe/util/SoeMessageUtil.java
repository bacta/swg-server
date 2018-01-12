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

package io.bacta.soe.util;

import com.google.common.io.Files;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class SoeMessageUtil {

    public static String bytesToHex(ByteBuffer buffer) {
        return BufferUtil.bytesToHex(buffer);
    }

    public static String makeMessageStruct(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder();

        String bytes = SoeMessageUtil.bytesToHex(buffer);
        int length = (16 * 3) - 1;
        while (bytes.length() > (16 * 3) - 1) {
            builder.append("    " + bytes.substring(0, length) + "\n");
            bytes = bytes.substring(length + 1);
        }

        builder.append("    " + bytes + "\n");

        return builder.toString();
    }

    public static int getTimeZoneValue() {
        return ZonedDateTime.now().getOffset().getTotalSeconds();
        //return DateTimeZone.getDefault().getOffset(null) / 1000;
    }

    public static String getTimeZoneOffsetFromValue(int value) {
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(value);
        return zoneOffset.toString();
       // DateTimeZone zone = DateTimeZone.forOffsetMillis(value * 1000);
       // return zone.toString();
    }
    
    public static List<List<Byte>> readTextPacketDump(final File file) throws IOException {
        return Files.readLines(file, Charset.defaultCharset(), new MultiMessageLineProcessor());
    }

    public static String bytesToHex(GameNetworkMessage message) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        message.writeToBuffer(buffer);
        return BufferUtil.bytesToHex(buffer);
    }
}
