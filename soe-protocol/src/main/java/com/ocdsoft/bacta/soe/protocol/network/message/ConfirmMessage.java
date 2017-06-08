package com.ocdsoft.bacta.soe.protocol.network.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.protocol.network.connection.EncryptMethod;

public final class ConfirmMessage extends SoeMessage {

	public ConfirmMessage(final byte crcLength,
                          final int connectionID,
                          final int sessionKey,
                          final EncryptMethod encryptMethod,
                          final boolean useComp,
                          final int udpSize) {

		super(UdpPacketType.cUdpPacketConfirm);

		buffer.putInt(connectionID);
        buffer.putInt(sessionKey);
		buffer.put(crcLength);
		BufferUtil.put(buffer, useComp);
		encryptMethod.writeToBuffer(buffer);
        buffer.putInt(udpSize);
	}
}
