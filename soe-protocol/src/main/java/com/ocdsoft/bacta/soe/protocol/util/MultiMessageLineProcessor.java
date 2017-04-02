package com.ocdsoft.bacta.soe.protocol.util;

import com.google.common.io.LineProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kburkhardt on 2/10/15.
 */
public class MultiMessageLineProcessor implements LineProcessor<List<List<Byte>>> {
    
    private List<List<Byte>> messages = new ArrayList<>();
    
    private final String newMessageTrigger = "Packet";
    private final String packetStart = "0000:";
    
    private int currentMessageLine;
    private List<Byte> currentMessage;

    @Override
    public boolean processLine(String line) throws IOException {
        
        if(line.startsWith(newMessageTrigger)) {
            return true;
        }

        if(line.startsWith(packetStart)) {

            if(currentMessage != null) {
                messages.add(currentMessage);
            }

            currentMessageLine = 0;
            currentMessage = new ArrayList<>();
        }

        String lineStart = String.valueOf(currentMessageLine);
        lineStart =  String.format("%04d", Integer.parseInt(lineStart)) + ":";
       
        if(line.startsWith(lineStart)) {
            addLineBytes(line.substring(line.indexOf(":") + 1, line.lastIndexOf("  ")).trim());
            currentMessageLine += 10;
        }

        return true;
    }

    private void addLineBytes(String trim) {
        String[] bytes = trim.split(" ");
        for(String item : bytes) {
            currentMessage.add((byte) Integer.parseInt(item, 16));
        }
    }

    @Override
    public List<List<Byte>> getResult() {

        if(currentMessage != null) {
            messages.add(currentMessage);
        }
        
        return messages;
    }
}
