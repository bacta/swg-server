package com.ocdsoft.bacta.soe.protocol.message;

public final class AckMessage extends SoeMessage {

	public AckMessage(short sequenceNum) {
		super(UdpPacketType.cUdpPacketAck1);

		compressed = false;

		buffer.putShort(sequenceNum);
	}
}
