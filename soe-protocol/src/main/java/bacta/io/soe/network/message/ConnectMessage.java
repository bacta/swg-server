package bacta.io.soe.network.message;

public final class ConnectMessage extends SoeMessage {

	public ConnectMessage(final int protocolVersion,
                          final int connectionID,
                          final int udpSize) {

		super(SoeMessageType.cUdpPacketConnect);

        // TODO: Implement soe settings object
		buffer.putInt(protocolVersion);
        buffer.putInt(connectionID);
        buffer.putInt(udpSize);
	}
}
