package bacta.io.network.udp.netty;

import bacta.io.network.channel.InboundMessageChannel;
import bacta.io.network.udp.UdpEmitter;
import bacta.io.network.udp.UdpMetrics;
import bacta.io.network.udp.UdpReceiver;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
  */
@Slf4j
public final class NettyUdpReceiver implements UdpReceiver {

    private final Thread thread;
    private final NettyUdpServer udpServer;
    private final NettyUdpHandler udpHandler;

    private final UdpMetrics metrics;

    private final InboundMessageChannel inboundMessageChannel;

	public NettyUdpReceiver(final InetAddress bindAddress,
                            final int bindPort,
                            final UdpMetrics metrics,
                            final InboundMessageChannel inboundMessageChannel) {

        this.metrics = metrics;

        this.udpHandler = new NettyUdpHandler(this, metrics);
        udpServer = new NettyUdpServer(bindAddress, bindPort, udpHandler);
        this.inboundMessageChannel = inboundMessageChannel;
        this.thread = new Thread(udpServer);
        thread.setDaemon(true);
        thread.setName("UdpReceiver");
    }

    @Override
    public UdpEmitter start() {
        thread.start();
        int attempts = 0;
        while(!isAvailable() && attempts < 10) {
            attempts++;
            try {
                Thread.sleep(500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return udpHandler.getEmitter();
    }

    @Override
    public void receiveMessage(final InetSocketAddress inetSocketAddress, final ByteBuffer msg) {
        metrics.receiveMessage();
        inboundMessageChannel.receiveMessage(inetSocketAddress, msg);
    }

    @Override
    public boolean isAvailable() {
        return udpHandler.isReady();
    }

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
    }
}
