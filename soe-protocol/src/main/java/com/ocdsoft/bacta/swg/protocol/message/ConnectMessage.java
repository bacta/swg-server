package com.ocdsoft.bacta.swg.protocol.message;

public final class ConnectMessage extends SoeMessage {

	public ConnectMessage(final int protocolVersion,
                          final int connectionID,
                          final int udpSize) {

		super(UdpPacketType.cUdpPacketConnect);

        // TODO: Implement connection settings object
		buffer.putInt(protocolVersion);
        buffer.putInt(connectionID);
        buffer.putInt(udpSize);
	}
}
