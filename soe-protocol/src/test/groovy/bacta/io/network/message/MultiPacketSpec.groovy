package bacta.io.network.message

import bacta.io.conf.ini.IniBactaConfiguration
import bacta.io.soe.config.SoeNetworkConfigurationImpl
import bacta.io.soe.network.connection.SoeUdpConnection
import bacta.io.soe.network.controller.*
import bacta.io.soe.network.dispatch.GameNetworkMessageDispatcher
import bacta.io.soe.network.dispatch.SoeDevMessageDispatcher
import bacta.io.soe.network.message.SoeMessageType
import bacta.io.soe.serialize.GameNetworkMessageSerializer
import bacta.io.soe.util.SoeMessageUtil
import spock.lang.Shared
import spock.lang.Specification

import java.nio.ByteBuffer

/**
 * Created by kburkhardt on 2/10/15.
 */
class MultiPacketSpec extends Specification {

    @Shared
    def soeMessageRouter
    
    @Shared
    List<ByteBuffer> processedPackets

    def setupSpec() {

        processedPackets = new ArrayList<ByteBuffer>()
        
        soeMessageRouter = new SoeDevMessageDispatcher(null, null)
        loadControllers(soeMessageRouter.metaClass.getProperty(soeMessageRouter, "controllers"))

    }

    def "CollectMessages"() {
        
        setup:
        def multiList = SoeMessageUtil.readTextPacketDump(new File(this.getClass().getResource("/multipackets.txt").getFile()))
        def bactaConfig = new IniBactaConfiguration()
        def networkConfig = new SoeNetworkConfigurationImpl()
        def messageSerializer = Mock(GameNetworkMessageSerializer)

        def soeUdpConnection = new SoeUdpConnection(networkConfig, null, messageSerializer, null)
        
        when:
        for(List<Byte> array : multiList) {
            ByteBuffer buffer = ByteBuffer.wrap(array.toArray(new byte[array.size()]))
            soeMessageRouter.dispatch(soeUdpConnection, buffer)
        }
        
        then:
        noExceptionThrown()
        processedPackets.size() > 0
        for(ByteBuffer buffer : processedPackets) {
          println SoeMessageUtil.bytesToHex(buffer)
        }
    }
    
    
    def loadControllers(controllers) {

        def swgMessageRouter = Mock(GameNetworkMessageDispatcher) {
            dispatch(_,_,_,_) >> { short zeroByte, int opcode, SoeUdpConnection connection, ByteBuffer buffer ->
                processedPackets.add(buffer)
            }
        }

        controllers.put(SoeMessageType.cUdpPacketConnect, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketConfirm, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketAckAll1, Mock(SoeMessageController))

        def multiController = new MultiController()
        multiController.setSoeMessageDispatcher(soeMessageRouter)
        multiController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketMulti, multiController)

        def reliableController = new ReliableMessageController()
        reliableController.setSoeMessageDispatcher(soeMessageRouter)
        reliableController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketReliable1, reliableController)

        def groupController = new GroupMessageController()
        groupController.setSoeMessageDispatcher(soeMessageRouter)
        groupController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketGroup, groupController)

        def zeroController = new ZeroEscapeController()
        zeroController.setSoeMessageDispatcher(soeMessageRouter)
        zeroController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketZeroEscape, zeroController)

        return controllers
    }
}