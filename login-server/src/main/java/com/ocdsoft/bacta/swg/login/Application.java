package com.ocdsoft.bacta.swg.login;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.network.client.ServerStatus;
import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.io.udp.SoeTransceiver;
import com.ocdsoft.bacta.soe.protocol.service.OutgoingConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by kburkhardt on 2/14/14.
 */

@Singleton
@Slf4j
public class Application implements Runnable {

    private final LoginServerState serverState;
    private final SoeTransceiver transceiver;

    public static void main(String args[]) {
        log.info("Starting LoginServer");
        Injector injector = Guice.createInjector(new LoginModule());
        Application loginServer = injector.getInstance(Application.class);
        Thread loginThread = new Thread(loginServer);
        loginThread.start();
    }

    @Inject
    public Application(final LoginServerState serverState,
                       final SoeTransceiver transceiver,
                       final OutgoingConnectionService outgoingConnectionService) {

        this.serverState = serverState;
        this.transceiver = transceiver;


        ((LoginOutgoingConnectionService)outgoingConnectionService).createConnection = transceiver::createOutgoingConnection;
    }

    @Override
    public void run() {

        try {

            log.info("Login Server is starting");
            serverState.setServerStatus(ServerStatus.UP);

            transceiver.run();
            log.info("Stopping");

        } catch (Exception e) {
            log.error("Error cluster transceiver", e);
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
