package bacta.io.soe.network.controller;

import bacta.io.buffer.BufferUtil;
import bacta.io.soe.config.SoeUdpConfiguration;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.message.EncryptMethod;
import bacta.io.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Slf4j
@Component
@SoeController(handles = {SoeMessageType.cUdpPacketConfirm})
public class ConfirmController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        int connectionID = buffer.getInt();
        int encryptCode = buffer.getInt();
        byte crcBytes = buffer.get();
        boolean compression = BufferUtil.getBoolean(buffer);
        byte cryptMethod = buffer.get();
        int maxRawPacketSize = buffer.getInt();

        connection.setId(connectionID);

        SoeUdpConfiguration configuration = new SoeUdpConfiguration(
                connection.getConfiguration().getProtocolVersion(),
                crcBytes,
                EncryptMethod.values()[cryptMethod],
                maxRawPacketSize,
                compression
        );

        connection.setConfiguration(configuration);
        connection.confirm();
    }
}
