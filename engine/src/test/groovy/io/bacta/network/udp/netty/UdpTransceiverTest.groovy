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

package io.bacta.network.udp.netty

import com.codahale.metrics.MetricRegistry
import io.bacta.Application
import io.bacta.engine.network.udp.UdpEmitter
import io.bacta.engine.network.udp.netty.NettyUdpTransceiver
import io.netty.channel.socket.DatagramPacket
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject
import java.nio.ByteBuffer

/**
 * Created by kyle on 4/11/2017.
 */
@SpringBootTest(classes = Application.class)
class UdpTransceiverTest extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    def host = InetAddress.getByName("127.0.0.1")

    def "IsAvailable"() {
        when:

        def server = new NettyUdpTransceiver(host, 5000, metricRegistry, "test", this.&readIncoming)

        then:
        noExceptionThrown()
        server.isReady()
        server.stop()
    }

    def "SendReceiveMessage"() {
        setup:


        def server = new NettyUdpTransceiver(host, 5000, metricRegistry, "test", this.&readIncoming)
        UdpEmitter client = server.getEmitter();

        when:
        client.sendMessage(new InetSocketAddress(host, 5000), ByteBuffer.allocate(1))
        client.sendMessage(new InetSocketAddress(host, 5000), ByteBuffer.allocate(1))
        client.sendMessage(new InetSocketAddress(host, 5000), ByteBuffer.allocate(1))

        then:

        noExceptionThrown()

        client.metrics.getSentMessages().getCount() == 3
        server.receiver.metrics.getReceivedMessages().getCount() == 3
    }

    public void readIncoming(InetSocketAddress sender, DatagramPacket msg) {

    }
}
