package com.ocdsoft.bacta.swg.protocol.disruptor;

import com.google.inject.Inject;
import com.lmax.disruptor.EventHandler;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.protocol.SoeProtocol;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class SoeUnmarshallingConsumer<T extends SoeUdpConnection> implements EventHandler<SoeInputEvent<T>> {
	
	private final SoeProtocol protocol;
	
	@Inject
	public SoeUnmarshallingConsumer(SoeProtocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public void onEvent(SoeInputEvent<T> event, long sequence, boolean endOfBatch)
			throws Exception {

		ByteBuffer message = event.getBuffer();
		T client = event.getClient(); 
		
		int swgByte = message.get(0);
		
		ByteBuf decodedMessage;
		
		if(swgByte != 0) {
			
			//decodedMessage = protocol.decode(client.getSessionKey(), message.order(ByteOrder.LITTLE_ENDIAN), 1);
			//decodedMessage.skipBytes(2);
            event.setSwgMessage(true);

		} else {
			
 			//decodedMessage = protocol.decode(client.getSessionKey(), message.order(ByteOrder.LITTLE_ENDIAN), 2);
		
		}

       // event.setBuffer(decodedMessage);
	}

}
