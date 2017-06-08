package com.ocdsoft.bacta.engine.io.network.dispatch;

public interface ByteMessageDispatcher<C, D> extends MessageDispatcher {
	void dispatch(byte opcode, C client, D message);
}
