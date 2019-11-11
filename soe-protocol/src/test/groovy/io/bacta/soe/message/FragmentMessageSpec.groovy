package io.bacta.soe.message

import io.bacta.shared.GameNetworkMessage
import io.bacta.soe.network.connection.DefaultIncomingMessageProcessor
import io.bacta.soe.network.connection.DefaultSoeUdpConnection
import io.bacta.soe.network.connection.OutgoingMessageProcessor
import io.bacta.soe.util.SoeMessageUtil

import java.nio.ByteBuffer

class FragmentMessageSpec extends MessageProcessingBase {

    def "Incoming Fragment Assemble"() {
        setup:
        def fragmentList = SoeMessageUtil.readTextPacketDump(new File(this.getClass().getResource("/fragments.txt").getFile()))
        def soeUdpConnection = new DefaultSoeUdpConnection(null, null, 0, networkConfig, new DefaultIncomingMessageProcessor(networkConfig), new OutgoingMessageProcessor(networkConfig, serializer))

        when:
        for(List<Byte> array : fragmentList) {
            ByteBuffer buffer = ByteBuffer.wrap(array.toArray(new byte[array.size()]))
            soeMessageHandler.handleMessage(soeUdpConnection, buffer, processor)
        }

        then:
        noExceptionThrown()
        processedPackets.size() == 37
    }

    def "Outgoing Fragment Assemble"() {

        setup:
        def fragmentList = SoeMessageUtil.readTextPacketDump(new File(this.getClass().getResource("/fragments.txt").getFile()))
        def soeUdpConnection = new DefaultSoeUdpConnection(null, null, 0, networkConfig, new DefaultIncomingMessageProcessor(networkConfig), new OutgoingMessageProcessor(networkConfig, serializer))

        when:
        // Collect processed packets
        for(List<Byte> array : fragmentList) {
            ByteBuffer buffer = ByteBuffer.wrap(array.toArray(new byte[array.size()]))
            soeMessageHandler.handleMessage(soeUdpConnection, buffer, processor)
        }

        def acks = soeUdpConnection.getPendingMessages()
        for(GameNetworkMessage message : processedPackets) {
            soeUdpConnection.sendMessage(message)
        }

        def incomingProcessedPackets = processedPackets
        processedPackets = new ArrayList<ByteBuffer>()
        List<ByteBuffer> outGoingMessages = soeUdpConnection.getPendingMessages()

        // Make sure the sequence number is expected
        int sequenceNumber = outGoingMessages.size()
        for(ByteBuffer message : outGoingMessages) {
            message.putShort(2, (short)sequenceNumber++);
            soeMessageHandler.handleMessage(soeUdpConnection, message, processor)
        }

        then:
        noExceptionThrown()
        acks.size() == 1
        outGoingMessages.size() == 3
        incomingProcessedPackets.size() == processedPackets.size()
    }
}
