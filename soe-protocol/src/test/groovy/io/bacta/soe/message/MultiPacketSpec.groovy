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

package io.bacta.soe.message


import io.bacta.shared.GameNetworkMessage
import io.bacta.soe.network.connection.DefaultIncomingMessageProcessor
import io.bacta.soe.network.connection.DefaultSoeUdpConnection
import io.bacta.soe.network.connection.OutgoingMessageProcessor
import io.bacta.soe.util.SoeMessageUtil

import java.nio.ByteBuffer

/**
 * Created by kburkhardt on 2/10/15.
 */
class MultiPacketSpec extends MessageProcessingBase {

    def "Process incoming messages"() {
        
        setup:
        def multiList = SoeMessageUtil.readTextPacketDump(new File(this.getClass().getResource("/multipackets.txt").getFile()))

        def soeUdpConnection = new DefaultSoeUdpConnection(null, null, 0, networkConfig, new DefaultIncomingMessageProcessor(networkConfig), new OutgoingMessageProcessor(networkConfig, serializer))

        when:
        for(List<Byte> array : multiList) {
            ByteBuffer buffer = ByteBuffer.wrap(array.toArray(new byte[array.size()]))
            soeMessageHandler.handleMessage(soeUdpConnection, buffer, processor)
        }
        
        then:
        noExceptionThrown()
        processedPackets.size() == 2
    }

    def "Process outgoing messages"() {

        setup:
        def multiList = SoeMessageUtil.readTextPacketDump(new File(this.getClass().getResource("/multipackets.txt").getFile()))
        def soeUdpConnection = new DefaultSoeUdpConnection(null, null, 0, networkConfig, new DefaultIncomingMessageProcessor(networkConfig), new OutgoingMessageProcessor(networkConfig, serializer))

        when:
        for(List<Byte> array : multiList) {
            ByteBuffer buffer = ByteBuffer.wrap(array.toArray(new byte[array.size()]))
            soeMessageHandler.handleMessage(soeUdpConnection, buffer, processor)
        }

        // Reset Connection and reliable counter - Hack
        soeUdpConnection = new DefaultSoeUdpConnection(null, null, 0, networkConfig, new DefaultIncomingMessageProcessor(networkConfig), new OutgoingMessageProcessor(networkConfig, serializer))

        for(GameNetworkMessage message : processedPackets) {
            soeUdpConnection.sendMessage(message)
        }

        def incomingProcessedPackets = processedPackets
        processedPackets = new ArrayList<ByteBuffer>()
        List<ByteBuffer> outGoingMessages = soeUdpConnection.getPendingMessages()
        for(ByteBuffer message : outGoingMessages) {
            soeMessageHandler.handleMessage(soeUdpConnection, message, processor)
        }

        then:
        noExceptionThrown()
        incomingProcessedPackets.size() == 2
        outGoingMessages.size() == 1
        incomingProcessedPackets.size() == processedPackets.size()
    }

}