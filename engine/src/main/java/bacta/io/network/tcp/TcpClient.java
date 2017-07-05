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

package bacta.io.network.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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
