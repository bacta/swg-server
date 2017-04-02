package com.ocdsoft.bacta.engine.network.io.udp;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public final class UdpServer implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(UdpServer.class);
	
	private Bootstrap b = null;
	private ChannelInboundHandlerAdapter[] handlers;
	protected final InetAddress bindAddress;
    protected final int port;


	public UdpServer(InetAddress bindAddress, int port, ChannelInboundHandlerAdapter... handlers) {
		this.bindAddress = bindAddress;
        this.port = port;
		this.handlers = handlers;
	}
	
	@Override
    public void run() {

		try {

            b = new Bootstrap();

            if(Epoll.isAvailable()) {
                b.group(new EpollEventLoopGroup());
                b.channel(EpollDatagramChannel.class);
            } else {
                b.group(new NioEventLoopGroup());
                b.channel(NioDatagramChannel.class);
            }

            LOGGER.debug("Starting on port: {}", port);

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

			Channel channel = b.bind().sync().channel();
					
			LOGGER.debug("Running on port: {}", port);

			channel.closeFuture().await();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (b != null) {
                b.group().shutdownGracefully();
            }
        }
    }

	public void stop()  {

	}
}
