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

package io.bacta.soe.network;

import com.jcraft.jzlib.CRC32;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.UnsignedUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("deprecation")
@Slf4j
@Component
public final class XOREncryption implements SoeEncryption {

    @Getter
    @Setter
	private boolean compression;

    @Inject
	public XOREncryption() {
		this.compression = true;
	}

	@Override
    public byte getEncryptionID() {
		return 0x4; // The id for default encryption
	}

	@Override
    public ByteBuffer decode(int seed, ByteBuffer data) {

        int swgByte = data.get(0);
        int soeByte = data.get(1);
        int offset;
        if(swgByte != 0) {
            offset = 1;
        } else {
            offset = 2;
        }

		if(verifyMessage(seed, data, offset)) {

			decrypt(seed, data, offset);
			if(LOGGER.isTraceEnabled()) {
                LOGGER.trace("Message: " + BufferUtil.bytesToHex(data));
            }

			if(data.get(data.limit() - 3) == 1) {
				data = decompress(data, offset);
			}

            data.order(ByteOrder.BIG_ENDIAN);
            data.rewind();
            return data;
		}

        return null;
	}

	@Override
    public ByteBuffer encode(int seed, ByteBuffer data, boolean doCompress) {

		if(compression && doCompress) {
			data = compress(data, 2);
			data.put((byte)1);
		} else {
		    data.limit(data.limit() + 1);
			data.put((byte)0);
		}

		encrypt(seed, data, 2);

		return data;
	}

    private void decrypt(int encKey, ByteBuffer data, int offset) {

        int intblocks = (data.limit() - 2 - offset) / 4;

        //TODO: Fix this hack
        data.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < intblocks; ++i) {

            int nextkey = (int) UnsignedUtil.getUnsignedInt(data, i * 4 + offset);
            data.putInt(i * 4 + offset, nextkey ^ encKey);
            encKey = nextkey;
        }
        data.order(ByteOrder.BIG_ENDIAN);

        for (int i = 0; i < ((data.limit() - 2 - offset) % 4); ++i)
        	data.put(intblocks * 4 + i + offset, (byte)((UnsignedUtil.getUnsignedByte(data, intblocks * 4 + i + offset)) ^ (encKey)));

        //log.debug("Post-decryption: " + StringUtil.bytesToHex(msg.data().array()));
	}

    private void encrypt(int dencKey, ByteBuffer data, int offset) {

        int intblocks = (data.limit() - offset) / 4;
        int remainderblocks = (data.limit() - offset) % 4;

        //log.debug("Pre-decryption: " + StringUtil.bytesToHex(msg.data().array()));
        
        for (int i = 0; i < intblocks; ++i)
        {

            data.putInt(i * 4 + offset, data.getInt(i * 4 + offset) ^ dencKey);
            
            dencKey = (int)UnsignedUtil.getUnsignedInt(data, i * 4 + offset);
        }

        for (int i = 0; i < remainderblocks; ++i)
        	data.put(intblocks * 4 + i + offset, (byte)((UnsignedUtil.getUnsignedByte(data, intblocks * 4 + i + offset)) ^ (dencKey)));

        //log.debug("Post-decryption: " + StringUtil.bytesToHex(msg.data().array()));
	}

    private ByteBuffer decompress(ByteBuffer data, int offset) {

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
            LOGGER.info("Error Decompressing '{}'", zstream.getMessage());
            return null;
        }
        
        int newLength = (int)zstream.total_out;
        zstream.inflateEnd();

        out.rewind();
        out.limit(newLength + offset);
        //log.info("Post-decompress: " + StringUtil.bytesToHex(out.array()) + out.readerIndex() + " : " + out.writerIndex());
        
        return out;

	}

	private ByteBuffer compress(ByteBuffer data, int offset) {

        ByteBuffer out = ByteBuffer.allocate(496).order(ByteOrder.LITTLE_ENDIAN);
        out.put(data.array(), 0, offset);

        ZStream zstream = new ZStream();
        zstream.avail_in = 0;
        zstream.deflateInit(JZlib.Z_DEFAULT_COMPRESSION);

        zstream.next_in = data.array();
        zstream.next_in_index = offset;
        zstream.avail_in = data.limit() - offset;
        
        zstream.next_out = out.array();
        zstream.next_out_index = offset;
        zstream.avail_out = 496;

        //log.info("Pre-compress: " + StringUtil.bytesToHex(data.array()));
        
        if (zstream.deflate(JZlib.Z_FINISH) == JZlib.Z_DATA_ERROR)
        {
            LOGGER.info("Error Compressing '{}'", zstream.getMessage());
            return data;
        }
        
        int newLength = (int)zstream.total_out;
        zstream.deflateEnd();

        out.position(newLength + offset);
        out.limit(newLength + offset + 1);
        //log.info("Post-compress: " + StringUtil.bytesToHex(out.array()) + out.readerIndex() + " : " + out.writerIndex());
        
        return out;
	}

    private int generateCRC(int nCrcSeed, ByteBuffer data, int crcLength)
    {
    	int[] g_nCrcTable = CRC32.getCRC32Table();
    
    	int nCrc = g_nCrcTable[(~nCrcSeed) & 0xFF];
        nCrc ^= 0x00FFFFFF;
        int nIndex = (nCrcSeed >>> 8) ^ nCrc;
        nCrc = (nCrc >>> 8) & 0x00FFFFFF;
        nCrc ^= g_nCrcTable[(nIndex & 0xFF)];
        nIndex = (nCrcSeed >>> 16) ^ nCrc;
        nCrc = (nCrc >>> 8) & 0x00FFFFFF;
        nCrc ^= g_nCrcTable[(nIndex & 0xFF)];
        nIndex = (nCrcSeed >>> 24) ^ nCrc;
        nCrc = (nCrc >>> 8) & 0x00FFFFFF;
        nCrc ^= g_nCrcTable[(nIndex & 0xFF)];

        for( short i = 0; i < data.limit() - crcLength; i++ )
        {
            nIndex = (data.get(i)) ^ nCrc;
            nCrc = (nCrc >>> 8) & 0x00FFFFFF;
            nCrc ^= g_nCrcTable[(nIndex & 0xFF)];
        }
        return ~nCrc;
    }

    private boolean verifyMessage(int crcSeed, ByteBuffer data, int crcLength) {

        boolean doHacks = false;
        boolean crctest = true;

        do
        {

            int nLength = data.limit();

            crctest = true;
            if (crcLength > 0)
            {
                int p_crc = generateCRC(crcSeed, data, crcLength);
                int crc = 0;
                int mask = 0; 
                int pullbyte = 0;

                for (int i = nLength - crcLength; i < nLength; i++)
                {
                    pullbyte = UnsignedUtil.getUnsignedByte(data, i);
                    crc |= (pullbyte << ((nLength - 1 - (i)) * 8));
                    mask <<= 8;
                    mask |= 0xFF;
                }

                p_crc &= mask;
                
                if (p_crc != crc)
                {
                    crctest = false;
                    doHacks = true;
                    if(crcLength > 4)
                    	return false;

                    if (data.limit() - data.position() > 5)
                    {
                        crcLength++;
                    }
                    else
                    {
                        doHacks = false;
                    }
                }
                else
                {
                    doHacks = false;
                    crctest = true;
                }
            }

        } while (doHacks);

        return crctest;
            
    }
    
	@Override
    public void appendCRC(int nCrcSeed, ByteBuffer data, int crcLength) {
        int crc = generateCRC(nCrcSeed, data, 0);
        data.limit(data.limit() + crcLength);
        for( int i = (crcLength - 1); i >= 0; i--) {
            data.put((byte) ((crc >>> (8 * i)) & 0xFF));
        }
    }

	@Override
    public boolean validate(int seed, ByteBuffer data) {
		return verifyMessage(seed, data, 2);
	}
}