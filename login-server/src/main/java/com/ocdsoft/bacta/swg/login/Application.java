package com.ocdsoft.bacta.swg.login;

import com.google.inject.*;
import com.ocdsoft.bacta.engine.ServerBuilder;
import com.ocdsoft.bacta.engine.network.ServerStatus;
import com.ocdsoft.bacta.engine.network.handler.IncomingMessageHandler;
import com.ocdsoft.bacta.engine.network.handler.OutgoingMessageHandler;
import com.ocdsoft.bacta.engine.network.pipeline.MessagePipeline;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.handler.ConnectionHandler;
import com.ocdsoft.bacta.soe.protocol.network.handler.ProtocolHandler;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.SoeProtocolPipeline;
import com.ocdsoft.bacta.soe.protocol.service.OutgoingConnectionService;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by kburkhardt on 2/14/14.
 */

@Singleton
@Slf4j
public class Application implements Runnable {

    private final LoginServerState serverState;
    private final SoeProtocolPipeline transceiver;

    public static void main(String args[]) {

        ServerBuilder.newInstance()
                .named("Login")
                .usingModules(new LoginModule(), new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IncomingMessageHandler.class).to(ConnectionHandler.class);
                        bind(OutgoingMessageHandler.class).to(ProtocolHandler.class);
                    }
                })
                .withState(LoginServerState.class)
                .build()
                .run();


        log.info("Starting LoginServer");
        Injector injector = Guice.createInjector(new LoginModule());
        Application loginServer = injector.getInstance(Application.class);
        Thread loginThread = new Thread(loginServer);
        loginThread.start();
    }

    @Inject
    public Application(final LoginServerState serverState,
                       final SoeProtocolPipeline transceiver,
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
