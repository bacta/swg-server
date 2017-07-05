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

package bacta.io.soe.util;

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
