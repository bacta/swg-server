package com.ocdsoft.bacta.soe.protocol.network.message

import com.ocdsoft.bacta.engine.conf.ini.IniBactaConfiguration
import com.ocdsoft.bacta.engine.io.network.ConnectionState
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection
import com.ocdsoft.bacta.soe.protocol.network.controller.GroupMessageController
import com.ocdsoft.bacta.soe.protocol.network.controller.MultiController
import com.ocdsoft.bacta.soe.protocol.network.controller.ReliableMessageController
import com.ocdsoft.bacta.soe.protocol.network.controller.SoeMessageController
import com.ocdsoft.bacta.soe.protocol.network.controller.ZeroEscapeController
import com.ocdsoft.bacta.soe.protocol.network.dispatch.GameNetworkMessageDispatcher
import com.ocdsoft.bacta.soe.protocol.network.dispatch.SoeDevMessageDispatcher
import com.ocdsoft.bacta.soe.protocol.GameNetworkConfiguration
import com.ocdsoft.bacta.soe.protocol.serialize.GameNetworkMessageSerializer
import com.ocdsoft.bacta.soe.protocol.util.SoeMessageUtil
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
        def networkConfig = new GameNetworkConfiguration(bactaConfig)
        def messageSerializer = Mock(GameNetworkMessageSerializer)

        def soeUdpConnection = new SoeUdpConnection(networkConfig, null, ConnectionState.DISCONNECTED, messageSerializer, null)
        
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

        controllers.put(UdpPacketType.cUdpPacketConnect, Mock(SoeMessageController))
        controllers.put(UdpPacketType.cUdpPacketConfirm, Mock(SoeMessageController))
        controllers.put(UdpPacketType.cUdpPacketAckAll1, Mock(SoeMessageController))

        def multiController = new MultiController()
        multiController.setSoeMessageDispatcher(soeMessageRouter)
        multiController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(UdpPacketType.cUdpPacketMulti, multiController)

        def reliableController = new ReliableMessageController()
        reliableController.setSoeMessageDispatcher(soeMessageRouter)
        reliableController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(UdpPacketType.cUdpPacketReliable1, reliableController)

        def groupController = new GroupMessageController()
        groupController.setSoeMessageDispatcher(soeMessageRouter)
        groupController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(UdpPacketType.cUdpPacketGroup, groupController)

        def zeroController = new ZeroEscapeController()
        zeroController.setSoeMessageDispatcher(soeMessageRouter)
        zeroController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(UdpPacketType.cUdpPacketZeroEscape, zeroController)

        return controllers
    }
}