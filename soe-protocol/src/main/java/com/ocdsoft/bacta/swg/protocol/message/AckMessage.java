package com.ocdsoft.bacta.swg.protocol.message;

public final class AckMessage extends SoeMessage {

	public AckMessage(short sequenceNum) {
		super(UdpPacketType.cUdpPacketAck1);

		compressed = false;

		buffer.putShort(sequenceNum);
	}
}
