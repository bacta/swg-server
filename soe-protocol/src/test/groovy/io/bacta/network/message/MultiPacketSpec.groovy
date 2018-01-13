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

package io.bacta.network.message

import bacta.io.soe.network.controller.*
import io.bacta.engine.conf.ini.IniBactaConfiguration
import io.bacta.soe.config.SoeNetworkConfigurationImpl
import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.network.connection.SoeIncomingMessageProcessor
import io.bacta.soe.network.connection.SoeOutgoingMessageProcessor
import io.bacta.soe.network.connection.SoeUdpConnection
import io.bacta.soe.network.controller.*
import io.bacta.soe.network.dispatch.GameNetworkMessageDispatcher
import io.bacta.soe.network.dispatch.SoeDevMessageDispatcher
import io.bacta.soe.network.message.SoeMessageType
import io.bacta.soe.serialize.GameNetworkMessageSerializer
import io.bacta.soe.util.SoeMessageUtil
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
    def swgMessageRouter
    
    @Shared
    List<ByteBuffer> processedPackets

    @Shared
    def networkConfig = new SoeNetworkConfigurationImpl()

    def setupSpec() {

        networkConfig.setMaxInstandingPackets(400)
        networkConfig.setMaxOutstandingPackets(400)

        processedPackets = new ArrayList<ByteBuffer>()

        swgMessageRouter = Mock(GameNetworkMessageDispatcher) {
            dispatch(_,_,_,_) >> { short priority, int gameMessageType, SoeConnection connection, ByteBuffer buffer ->
                processedPackets.add(buffer)
            }
        }

        soeMessageRouter = new SoeDevMessageDispatcher(null, swgMessageRouter)
        loadControllers(soeMessageRouter.metaClass.getProperty(soeMessageRouter, "controllers"))

    }

    def "CollectMessages"() {
        
        setup:
        def multiList = SoeMessageUtil.readTextPacketDump(new File(this.getClass().getResource("/multipackets.txt").getFile()))
        def bactaConfig = new IniBactaConfiguration()
        def messageSerializer = Mock(GameNetworkMessageSerializer)

        def soeUdpConnection = new SoeUdpConnection(null, null, 0, networkConfig, new SoeIncomingMessageProcessor(networkConfig), new SoeOutgoingMessageProcessor(networkConfig, messageSerializer))
        def SoeConnection = new SoeConnection(soeUdpConnection)

        when:
        for(List<Byte> array : multiList) {
            ByteBuffer buffer = ByteBuffer.wrap(array.toArray(new byte[array.size()]))
            soeMessageRouter.dispatch(SoeConnection, buffer)
        }
        
        then:
        noExceptionThrown()
        processedPackets.size() > 0
        for(ByteBuffer buffer : processedPackets) {
          println SoeMessageUtil.bytesToHex(buffer)
        }
    }
    
    
    def loadControllers(controllers) {

        controllers.put(SoeMessageType.cUdpPacketConnect, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketConfirm, Mock(SoeMessageController))
        controllers.put(SoeMessageType.cUdpPacketAckAll1, Mock(SoeMessageController))

        def multiController = new MultiController()
        multiController.setSoeMessageDispatcher(soeMessageRouter)
        multiController.setGameNetworkMessageDispatcher(swgMessageRouter)

        controllers.put(SoeMessageType.cUdpPacketMulti, multiController)

        def reliableController = new ReliableMessageController(networkConfig)
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