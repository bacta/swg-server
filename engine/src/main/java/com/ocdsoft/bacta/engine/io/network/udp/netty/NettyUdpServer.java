package com.ocdsoft.bacta.engine.io.network.udp.netty;

import com.ocdsoft.bacta.engine.context.ShutdownListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
final class NettyUdpServer implements Runnable, ShutdownListener {

	private Bootstrap b = null;
	private ChannelInboundHandlerAdapter[] handlers;
    private final InetAddress bindAddress;
    private final int port;
    private Channel channel;


	NettyUdpServer(InetAddress bindAddress, int port, ChannelInboundHandlerAdapter... handlers) {
		this.bindAddress = bindAddress;
        this.port = port;
		this.handlers = handlers;
	}
	
	@Override
    public void run() {

		try {

            LOGGER.info("Transceiver Starting");

            b = new Bootstrap();

            if(Epoll.isAvailable()) {
                b.group(new EpollEventLoopGroup());
                b.channel(EpollDatagramChannel.class);
            } else {
                b.group(new NioEventLoopGroup());
                b.channel(NioDatagramChannel.class);
            }

            LOGGER.info("Using port: {}", port);

	        b.option(ChannelOption.SO_RCVBUF, 768)
				.option(ChannelOption.SO_SNDBUF, 768)
				.option(ChannelOption.SO_BROADCAST, true)
                
                //.option(ChannelOption.ALLOCATOR, Unpooled.DEFAULT)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {

                        @Override
                        protected void initChannel(NioDatagramChannel ch)
                                throws Exception {

                            ch.pipeline().addLast(handlers);
                        }

                    });
            
            if(port != 0) {
                b.localAddress(bindAddress, port);
            } else {
                b.localAddress(0);
            }

			channel = b.bind().sync().channel();

            LOGGER.info("Running on port: {}", port);
			channel.closeFuture().await();
			
		} catch (InterruptedException e) {
            LOGGER.info("Transceiver closing on: {}", port);
            shutdown();
        } finally {
            if (b != null) {
                b.group().shutdownGracefully();
            }
        }
    }

    @Override
	public void shutdown()  {
        channel.close();
	}
}
