package io.bacta.soe.network.connection

import io.bacta.soe.config.SoeNetworkConfiguration
import spock.lang.Specification

import java.nio.ByteBuffer

class ReliableUdpMessageBuilderTest extends Specification {

    def "Build Message Fragments"() {

        setup:
        def config = new SoeNetworkConfiguration()
        config.setMaxOutstandingPackets(400)
        config.setMultiGameMessages(true)
        config.setMaxRawPacketSize(496)
        config.setCrcBytes((byte) 2)
        def reliableMessageBuilder = new ReliableMessageChannel(config)

        when:
        def bytes = ByteBuffer.allocate(config.getMaxRawPacketSize() * 5)
        reliableMessageBuilder.add(bytes)

        ByteBuffer buffer;
        def bufferList = new ArrayList<ByteBuffer>();
        while ((buffer = reliableMessageBuilder.buildNext()) != null) {
            bufferList.add(buffer)
        }

        then:
        noExceptionThrown()
        bufferList.size() == 6
        bufferList.get(0).getInt(4) == config.getMaxRawPacketSize() * 5
    }

    def "Build Multiple GMN in one payload"() {

        setup:
        def config = new SoeNetworkConfiguration()
        config.setMaxOutstandingPackets(400)
        config.setMultiGameMessages(true)
        config.setMaxRawPacketSize(496)
        config.setCrcBytes((byte) 2)
        def reliableMessageBuilder = new ReliableMessageChannel(config)
        def messageSize = 30
        def messageCount = 5;

        when:
        for (int i = 0; i < messageCount; ++i) {
            def bytes = ByteBuffer.allocate(messageSize)
            byte[] bytesArray = new byte[messageSize];
            Arrays.fill( bytesArray, (byte) i );
            bytes.put(bytesArray)
            reliableMessageBuilder.add(bytes)
        }


        ByteBuffer buffer;
        def bufferList = new ArrayList<ByteBuffer>();
        while ((buffer = reliableMessageBuilder.buildNext()) != null) {
            bufferList.add(buffer)
        }

        then:
        noExceptionThrown()
        bufferList.size() == 1
    }
}
