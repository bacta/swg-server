package com.ocdsoft.bacta.engine.io.network.dispatch;

public interface StringMessageDispatcher<C, D> extends MessageDispatcher {
	void dispatch(String opcode, C client, D message);
}
