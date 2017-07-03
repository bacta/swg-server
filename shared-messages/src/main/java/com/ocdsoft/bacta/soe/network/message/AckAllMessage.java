package com.ocdsoft.bacta.soe.network.message;

import org.springframework.stereotype.Component;

public final class AckAllMessage extends SoeMessage {

	public AckAllMessage(short sequenceNum) {
		super(SoeMessageType.cUdpPacketAckAll1);

		compressed = false;
		buffer.putShort(sequenceNum);
	}
}
