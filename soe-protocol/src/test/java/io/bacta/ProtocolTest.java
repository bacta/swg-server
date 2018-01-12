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

package io.bacta;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.UnsignedUtil;
import io.bacta.soe.network.XOREncryption;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ProtocolTest extends MessageDumpLoader {

    public static final Logger logger = LoggerFactory.getLogger(ProtocolTest.class);

	@Test
	public void test() {
				 
		XOREncryption protocol = new XOREncryption();

		for(int i = 0; i < pre.size(); ++i) {
			
			byte[] preArray = pre.get(i);
			ByteBuffer test1 = ByteBuffer.allocate(preArray.length);
			
			for(short b : preArray)
				test1.put((byte) b);
			
			logger.debug("Decoded");
            logger.debug("Test: " + BufferUtil.bytesToHex(test1));
            logger.debug("      " + BufferUtil.bytesToHex(decomp.get(i)));

			//test1 = test1.order(ByteOrder.LITTLE_ENDIAN);
			test1 = protocol.decode(sessionKey, test1);

            logger.debug("      " + BufferUtil.bytesToHex(test1));

            test1.rewind();

			for(int j = 0 ; j < decomp.get(i).length - 3; ++j) {
				
				short decCheck = (short) (decomp.get(i)[j] & 0xFF);
				
				if(test1.position() - test1.limit() == 0)
					fail("Decompression Decoded buffer too short at index " + j);
				
				short value = UnsignedUtil.getUnsignedByte(test1);

				if(decCheck != value) {
                    if (j == decomp.get(i).length - 2) {
                        fail("Incorrect CRC");
                    }
                    fail("Decompression Results do not match at index " + j);
                }
			}

			test1 = ByteBuffer.allocate(decomp.get(i).length);
			
			for(short b : decomp.get(i))
				test1.put((byte) b);
			
			test1.rewind();

            logger.debug("Encoded");
            logger.debug("Test: " + BufferUtil.bytesToHex(test1.array()));
            logger.debug("	   " + BufferUtil.bytesToHex(pre.get(i)));
			
			test1 = protocol.encode(sessionKey, test1, true);
			
			protocol.appendCRC(sessionKey, test1, 2);

            logger.debug("	   " + BufferUtil.bytesToHex(test1));

			test1.rewind();

			for(int j = 0 ; j < pre.get(i).length; ++j) {
				
				short decCheck = (short) (pre.get(i)[j] & 0xFF);
				
				if(test1.position() - test1.limit() == 0)
					fail("Encoded buffer too short at index " + j);
				
				short value = UnsignedUtil.getUnsignedByte(test1);

				if(decCheck != value) {
                    if (j == pre.get(i).length - 2) {
                        fail("Incorrect CRC");
                    }
                    fail("Encoded Results do not match at index " + j);
                }
			}

            test1.rewind();

            assertTrue(protocol.validate(sessionKey, test1));
		}
	}
}
