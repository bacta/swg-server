package com.ocdsoft.bacta.engine.io.network.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

public final class TcpServer extends Observable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);
    private final ChannelInboundHandlerAdapter handler;

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;
	private final ServerBootstrap bootstrap;
	
	private int port;

	public TcpServer(ChannelInboundHandlerAdapter handler, int port) {
		this.port = port;
		this.handler = handler;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
	}
	
    public final void start() {

        LOGGER.info("Starting TCP Server on port {}", port);

        new Thread(() -> {
            try {
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .childHandler(handler)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);


                LOGGER.info("TCP Server Running on port {}", port);

                ChannelFuture f = bootstrap.bind(port).sync();

                update();
                notifyObservers(Status.CONNECTED);

                f.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            } finally {
                stop();
            }

            update();
            notifyObservers(Status.DISCONNECTED);

        }).start();
    }

	public void stop() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

    public boolean isConnected() {
        return !workerGroup.isShutdown();
    }

    private void update() {
        setChanged();
    }

    public static enum Status {
        CONNECTED,DISCONNECTED
    }
}
