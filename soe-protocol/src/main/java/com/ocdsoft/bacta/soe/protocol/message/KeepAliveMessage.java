package com.ocdsoft.bacta.soe.protocol.message;


public class KeepAliveMessage extends SoeMessage {

	public KeepAliveMessage(short readShort, byte readByte) {
		super(UdpPacketType.cUdpPacketKeepAlive);

		buffer.putShort(readShort);
		buffer.put(readByte);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putShort((short) 0);
    }
}
