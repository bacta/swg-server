package com.ocdsoft.bacta.soe.network.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import org.springframework.stereotype.Component;

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
