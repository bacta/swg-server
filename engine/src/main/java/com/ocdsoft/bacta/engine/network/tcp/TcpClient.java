package com.ocdsoft.bacta.engine.network.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Observable;

public final class TcpClient extends Observable {

	private final static Logger LOGGER = LoggerFactory.getLogger(TcpClient.class);

    @Getter
	private final InetSocketAddress remoteAddress;
	private final ChannelInboundHandlerAdapter handler;
    private final EventLoopGroup workerGroup;

    /**
     * This constructor is for when you want to provide a handler to expand the basic functionality
     * @param remoteAddress host to connect to
     * @param handler provided handler
     */
    public TcpClient(final InetSocketAddress remoteAddress, final ChannelInboundHandlerAdapter handler) {
        this.remoteAddress = remoteAddress;
        this.handler = handler;
        this.workerGroup = new NioEventLoopGroup();
    }

    public void start()  {

        LOGGER.info("Connecting to {}", remoteAddress);

        final Thread tcpThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Bootstrap b = new Bootstrap(); // (1)
                    b.group(workerGroup); // (2)
                    b.channel(NioSocketChannel.class); // (3)
                    b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
                    b.handler(handler);

                    // Start the client.
                    ChannelFuture f = b.connect(remoteAddress); // (5)

                    update();
                    notifyObservers(TcpServer.Status.CONNECTED);
                    // Wait until the soe is closed.
                    f.channel().closeFuture().sync();

                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted", e);
                } finally {
                    workerGroup.shutdownGracefully();
                }

                update();
                notifyObservers(TcpServer.Status.DISCONNECTED);
                LOGGER.info("Connection to {} closed", remoteAddress);
            }
        });
        tcpThread.start();
    }

    public boolean isConnected() {
        return !workerGroup.isShutdown();
    }

    private void update() {
        setChanged();
    }

}
