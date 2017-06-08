package com.ocdsoft.bacta.engine.io.network.dispatch;

public interface IntMessageDispatcher<C, D> extends MessageDispatcher {
	void dispatch(int opcode, C client, D message);
}
