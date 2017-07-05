package bacta.io.soe.network.message;

public class KeepAliveMessage extends SoeMessage {

	public KeepAliveMessage(short readShort, byte readByte) {
		super(SoeMessageType.cUdpPacketKeepAlive);

		buffer.putShort(readShort);
		buffer.put(readByte);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putShort((short) 0);
    }
}
