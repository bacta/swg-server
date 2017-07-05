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

package bacta.io.network.message

import com.jcraft.jzlib.JZlib
import com.jcraft.jzlib.ZStream
import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.ByteOrder;

/**
 * Created by kyle on 7/2/2017.
 */
public class ClockSyncCompression extends Specification {

    def "CompressionTest"() {


        when:

        ByteBuffer data = ByteBuffer.allocate(42)
        data.put(0x0 as byte);
        data.put(0x7 as byte);
        data.put(0xA5 as byte);
        data.put(0xAA as byte);
        for(int i = 0; i < 27; ++i) {
            data.put(0x0 as byte);
        }
        data.put(0x02 as byte); // byte 31
        for(int i = 0; i < 7; ++i) {
            data.put(0x0 as byte);
        }
        data.put(0x1 as byte);  // byte 39
        data.put(0x21 as byte);
        data.put(0x29 as byte);

        def offset = 2;

        ByteBuffer out = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
        out.put(data.array(), 0, offset);

        ZStream zstream = new ZStream();
        zstream.avail_in = 0;
        zstream.inflateInit();
        zstream.next_in = data.array();
        zstream.next_in_index = offset;
        zstream.next_out_index = offset;
        zstream.avail_in = data.limit() - 4;
        zstream.next_out = out.array();
        zstream.avail_out = 1024;


        //log.trace("Pre-decompress: {}", SoeMessageUtil.bytesToHex(data));

        if (zstream.inflate(JZlib.Z_FINISH) == JZlib.Z_DATA_ERROR)
        {
            LOGGER.info("Error Decompressing");
            return null;
        }
        then:
        noExceptionThrown()


    }
}
