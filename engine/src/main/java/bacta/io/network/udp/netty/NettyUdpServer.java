/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.network.udp.netty;

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
import org.springframework.beans.factory.DisposableBean;

import java.net.InetAddress;

@Slf4j
final class NettyUdpServer implements Runnable, DisposableBean {

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

            LOGGER.info("Receiver Starting");

            b = new Bootstrap();

            if(Epoll.isAvailable()) {
                b.group(new EpollEventLoopGroup());
                b.channel(EpollDatagramChannel.class);
            } else {
                b.group(new NioEventLoopGroup());
                b.channel(NioDatagramChannel.class);
            }

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
                b.localAddress(bindAddress, 0);
            }

			channel = b.bind().sync().channel();

            LOGGER.info("Receiver Running: {}", channel.toString());
			channel.closeFuture().await();
			
		} catch (InterruptedException e) {
            LOGGER.info("Receiver Closing: {}", channel.toString());
        } finally {
            if (b != null) {
                b.group().shutdownGracefully();
            }
        }
    }

	@Override
    public String toString() {
	    return channel.toString();
    }

    @Override
    public void destroy() throws Exception {
        channel.close();
    }
}
