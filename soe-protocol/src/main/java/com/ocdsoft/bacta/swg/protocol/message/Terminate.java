package com.ocdsoft.bacta.swg.protocol.message;

public final class Terminate extends SoeMessage {

	public Terminate(int connectionId, TerminateReason reason) {
		super(UdpPacketType.cUdpPacketTerminate);
				
		buffer.putInt(connectionId);
        reason.writeToBuffer(buffer);
	}
}
