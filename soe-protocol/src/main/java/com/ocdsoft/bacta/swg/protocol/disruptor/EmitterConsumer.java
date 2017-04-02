package com.ocdsoft.bacta.swg.protocol.disruptor;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.lmax.disruptor.EventHandler;
import com.ocdsoft.bacta.engine.network.io.udp.UdpTransceiver;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;

import java.nio.ByteBuffer;
import java.util.List;

public class EmitterConsumer<C extends SoeUdpConnection, T extends UdpTransceiver> implements EventHandler<SoeOutputEvent<C>> {

    private final T transceiver;

    @Inject
    public EmitterConsumer(@Assisted T transceiver) {
        this.transceiver = transceiver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(SoeOutputEvent<C> event, long sequence, boolean endOfBatch)
            throws Exception {

        C client = event.getClient();

        List<ByteBuffer> messageList = event.getMessageList();

        while (!messageList.isEmpty()) {

            ByteBuffer message = messageList.remove(0);
            transceiver.sendMessage(client, message);
        }

    }

}
