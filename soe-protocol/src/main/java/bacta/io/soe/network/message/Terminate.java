package bacta.io.soe.network.message;

public final class Terminate extends SoeMessage {

	public Terminate(int connectionId, TerminateReason reason) {
		super(SoeMessageType.cUdpPacketTerminate);
				
		buffer.putInt(connectionId);
        reason.writeToBuffer(buffer);
	}
}
