package bacta.io.network.dispatch;

public interface ShortMessageDispatcher<C, D> extends MessageDispatcher {
	void dispatch(short opcode, C client, D message);
}
