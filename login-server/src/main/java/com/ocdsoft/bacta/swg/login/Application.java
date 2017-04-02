package com.ocdsoft.bacta.swg.login;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.network.client.ServerStatus;
import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.io.udp.SoeTransceiver;
import com.ocdsoft.bacta.swg.protocol.service.OutgoingConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by kburkhardt on 2/14/14.
 */

@Singleton
public class Application implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private final LoginServerState serverState;
    private final SoeTransceiver transceiver;

    @Inject
    public Application(final LoginServerState serverState,
                       final SoeTransceiver transceiver,
                       final OutgoingConnectionService outgoingConnectionService) {

        this.serverState = serverState;
        this.transceiver = transceiver;

        UdpServerBuilder()
                .withHandler()



        ((LoginOutgoingConnectionService)outgoingConnectionService).createConnection = transceiver::createOutgoingConnection;
    }

    @Override
    public void run() {

        try {

            LOGGER.info("Login Server is starting");
            serverState.setServerStatus(ServerStatus.UP);

            transceiver.run();
            LOGGER.info("Stopping");

        } catch (Exception e) {
            LOGGER.error("Error cluster transceiver", e);
        }
    }

    public void stop() {
        if(transceiver != null) {
            transceiver.stop();
        }
    }

    @Singleton
    final static public class LoginOutgoingConnectionService implements OutgoingConnectionService {

        private BiFunction<InetSocketAddress, Consumer<SoeUdpConnection>, SoeUdpConnection> createConnection;

        @Override
        public SoeUdpConnection createOutgoingConnection(final InetSocketAddress address, final Consumer<SoeUdpConnection> connectCallback) {
            if(createConnection == null) return null;

            return createConnection.apply(address, connectCallback);
        }
    }
}
