package io.bacta.soe.message

import com.codahale.metrics.Histogram
import com.codahale.metrics.MetricRegistry
import io.bacta.shared.GameNetworkMessage
import io.bacta.soe.config.SoeNetworkConfiguration
import io.bacta.soe.network.connection.DefaultSoeUdpConnection
import io.bacta.soe.network.controller.*
import io.bacta.soe.network.dispatch.SoeDevMessageDispatcher
import io.bacta.soe.network.forwarder.GameNetworkMessageProcessor
import io.bacta.soe.network.message.SoeMessageType
import io.bacta.soe.serialize.DefaultGameNetworkMessageSerializer
import io.bacta.soe.serialize.GameNetworkMessageSerializer
import io.bacta.soe.serialize.ObjControllerMessageSerializer
import io.bacta.soe.util.GameNetworkMessageTemplateWriter
import spock.lang.Shared
import spock.lang.Specification

import java.nio.ByteBuffer

abstract class MessageProcessingBase extends Specification {
    @Shared
    def soeMessageRouter

    @Shared
    def processor

    @Shared
    List<GameNetworkMessage> processedPackets = new ArrayList<ByteBuffer>()

    @Shared
    def GameNetworkMessageSerializer serializer;

    @Shared
    def networkConfig = new SoeNetworkConfiguration()

    def setupSpec() {

        networkConfig.setMaxInstandingPackets(400)
        networkConfig.setMaxOutstandingPackets(400)
        networkConfig.setReliableChannelCount(4)
        networkConfig.setCrcBytes((byte) 2)
        networkConfig.setMaxRawPacketSize(496)
        networkConfig.setMultiGameMessages(true)
        networkConfig.setMultiSoeMessages(true)
        networkConfig.setHardMaxOutstandingPackets(30000)

        def metrics = Mock(MetricRegistry)
        metrics.histogram(_) >> {
            return Mock(Histogram)
        }

        def objSerializer = new ObjControllerMessageSerializer()
        objSerializer.loadMessages()

        serializer = new DefaultGameNetworkMessageSerializer(metrics, objSerializer, Mock(GameNetworkMessageTemplateWriter))
        serializer.loadMessages()

        processor = Mock(GameNetworkMessageProcessor) {
            process(_,_,) >> { final DefaultSoeUdpConnection connection, final GameNetworkMessage gameNetworkMessage ->
                processedPackets.add(gameNetworkMessage)
            }
        }

        soeMessageRouter = new SoeDevMessageDispatcher(null)
        loadControllers(soeMessageRouter.metaClass.getProperty(soeMessageRouter, "controllers"))

    }

    def setup() {
        processedPackets.clear()
    }

    def loadControllers(controllers) {

        controllers.put(SoeMessageType.cUdpPacketConnect, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketConfirm, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketAckAll1, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketAck1, Mock(SoeMessageController))

        def multiController = new MultiController(soeMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketMulti, multiController)

        def reliableController = new ReliableMessageController(networkConfig, soeMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketReliable1, reliableController)

        def groupController = new GroupMessageController(soeMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketGroup, groupController)

        def zeroController = new ZeroEscapeController(serializer)

        controllers.put(SoeMessageType.cUdpPacketZeroEscape, zeroController)

        return controllers
    }
}
