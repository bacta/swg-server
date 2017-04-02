package com.ocdsoft.bacta.swg.protocol.disruptor;

import com.google.inject.Inject;
import com.lmax.disruptor.EventHandler;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.protocol.SoeProtocol;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

public class SoeMarshallingConsumer<T extends SoeUdpConnection> implements EventHandler<SoeOutputEvent<T>> {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private final SoeProtocol protocol;

    @Inject
    public SoeMarshallingConsumer(SoeProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void onEvent(SoeOutputEvent<T> event, long sequence, boolean endOfBatch)
            throws Exception {

        T client = event.getClient();

        List<ByteBuffer> messageList = event.getMessageList();

        for (int i = 0; i < messageList.size(); ++i) {

            ByteBuffer buffer = messageList.get(i);

            short op = ByteBufUtil.swapShort(buffer.getShort(0));

            if (op > 0x2 && op != 0x4) {
                try {
                    buffer = protocol.encode(client.getConfiguration().getEncryptCode(), buffer, true);

                    protocol.appendCRC(client.getConfiguration().getEncryptCode(), buffer, 2);

                    messageList.set(i, buffer);

                    if (!protocol.validate(client.getConfiguration().getEncryptCode(), buffer)) {
                        logger.info("Invalid Packet");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
