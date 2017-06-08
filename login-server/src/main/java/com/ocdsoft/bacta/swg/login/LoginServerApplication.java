package com.ocdsoft.bacta.swg.login;

import com.google.inject.*;
import com.ocdsoft.bacta.engine.ServerBuilder;
import com.ocdsoft.bacta.soe.protocol.network.ServerStatus;
import com.ocdsoft.bacta.engine.io.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.io.network.channel.OutboundMessageChannel;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.handler.ConnectionHandler;
import com.ocdsoft.bacta.soe.protocol.network.handler.ProtocolHandler;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.SoeProtocolPipeline;
import com.ocdsoft.bacta.soe.protocol.service.OutgoingConnectionService;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by kburkhardt on 2/14/14.
 */

@Slf4j
public class LoginServerApplication implements Runnable {

    private final LoginServerState serverState;
    private final SoeProtocolPipeline transceiver;

    public static void main(String args[]) {

        ServerBuilder.newInstance()
                .named("Login")
                .usingModules(new LoginModule(), new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(InboundMessageChannel.class).to(ConnectionHandler.class);
                        bind(OutboundMessageChannel.class).to(ProtocolHandler.class);
                    }
                })
                .withState(LoginServerState.class)
                .build()
                .run();


        log.info("Starting LoginServer");
        Injector injector = Guice.createInjector(new LoginModule());
        LoginServerApplication loginServer = injector.getInstance(LoginServerApplication.class);
        Thread loginThread = new Thread(loginServer);
        loginThread.start();
    }

    @Inject
    public LoginServerApplication(final LoginServerState serverState,
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
