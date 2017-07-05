package bacta.io.soe.network.message;

import bacta.io.buffer.BufferUtil;

public final class ConfirmMessage extends SoeMessage {

	public ConfirmMessage(final byte crcLength,
                          final int connectionID,
                          final int sessionKey,
                          final EncryptMethod encryptMethod,
                          final boolean useComp,
                          final int udpSize) {

		super(SoeMessageType.cUdpPacketConfirm);

		buffer.putInt(connectionID);
        buffer.putInt(sessionKey);
		buffer.put(crcLength);
		BufferUtil.put(buffer, useComp);
		encryptMethod.writeToBuffer(buffer);
        buffer.putInt(udpSize);
	}
}
