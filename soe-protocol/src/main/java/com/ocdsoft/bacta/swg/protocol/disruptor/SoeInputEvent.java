package com.ocdsoft.bacta.swg.protocol.disruptor;

import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

public final class SoeInputEvent<Client extends SoeUdpConnection> {

    @Getter
    @Setter
    private Client client;
    @Getter
    @Setter
    private ByteBuffer buffer;
    @Getter
    @Setter
    private boolean SwgMessage = false;

}
