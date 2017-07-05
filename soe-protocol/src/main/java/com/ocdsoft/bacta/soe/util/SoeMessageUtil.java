package com.ocdsoft.bacta.soe.util;

import com.google.common.io.Files;
import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.network.message.game.GameNetworkMessage;

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
