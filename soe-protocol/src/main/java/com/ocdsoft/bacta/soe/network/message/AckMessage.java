package com.ocdsoft.bacta.soe.network.message;

public final class AckMessage extends SoeMessage {

	public AckMessage(short sequenceNum) {
		super(SoeMessageType.cUdpPacketAck1);

		compressed = false;

		buffer.putShort(sequenceNum);
	}
}
