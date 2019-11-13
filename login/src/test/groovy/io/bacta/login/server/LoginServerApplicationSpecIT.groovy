package io.bacta.login.server


import groovy.util.logging.Slf4j
import io.bacta.engine.network.connection.ConnectionState
import io.bacta.engine.util.AwaitUtil
import io.bacta.login.message.LoginClientId
import io.bacta.login.message.LoginIncorrectClientId
import io.bacta.soe.config.SoeNetworkConfiguration
import io.bacta.soe.network.channel.SoeMessageChannel
import io.bacta.soe.network.connection.SoeUdpConnection
import io.bacta.soe.network.message.TerminateReason
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import javax.inject.Inject

@Slf4j
@SpringBootTest(classes = Application.class)
@Import(ITTestConfiguration.class)
class LoginServerApplicationSpecIT extends Specification {

    @Inject
    ClientChannelProperties properties

    @Inject
    SoeNetworkConfiguration networkConfiguration

    @Inject
    @Qualifier("ClientChannel")
    SoeMessageChannel soeMessageChannel;

    SoeUdpConnection connection
    TestAwaitMessageInterceptor intercept

    def setup() {
        connection = soeMessageChannel.getConnectionCache().getAndAddNewConnection(
                new InetSocketAddress(properties.getLoginAddress(), properties.getLoginPort())
        )

        connection.connect(null)
        intercept = connection.getInterceptor(TestAwaitMessageInterceptor.class)
        assert intercept != null
        AwaitUtil.awaitTrue({connection.getConnectionState() == ConnectionState.ONLINE}, 5)
        AwaitUtil.awaitTrue({connection.getEncryptCode() != 0}, 5)
    }

    def teardown() {
        connection.terminate(TerminateReason.APPLICATION)
    }

    def "TestConnect"() {

        when:
        AwaitUtil.awaitTrue({connection.getEncryptCode() != 0}, 5)

        then:
        noExceptionThrown()
        connection.getConnectionState() == ConnectionState.ONLINE
        connection.getEncryptCode() != 0
    }

    def "BadClientVersion"() {

        when:
        connection.sendMessage(new LoginClientId("DEADBABE", "DEADBABE", "DEADBABE"))
        AwaitUtil.awaitTrue({intercept.hasMessage(LoginIncorrectClientId.class)}, 5)

        then:
        connection.getConnectionState() == ConnectionState.ONLINE
        connection.terminate(TerminateReason.APPLICATION)
    }
//
//    def "TestConnect with ConnectionMap"() {
//
//        setup:
//
//        BroadcastService connectionMap = new DefaultBroadcastService()
//        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
//        SoeClient connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, properties.getBindPort()));
//
//        when:
//
//        connection.connect({udpConnection -> log.info("I connected")})
//
//        then:
//        noExceptionThrown()
//        AwaitUtil.awaitTrue(connection.&isConnected, 5)
//    }
//
//    def "Test Implicit connect"() {
//
//        setup:
//
//        BroadcastService connectionMap = new DefaultBroadcastService()
//        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
//        SoeClient connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, properties.getBindPort()));
//        def startingReceivedReliables = connection.soeUdpConnection.incomingMessageProcessor.gameNetworkMessagesReceived.get()
//        def startingSequence = connection.soeUdpConnection.outgoingMessageQueue.udpMessageProcessor.reliableUdpMessageBuilder.sequenceNum.get()
//
//        when:
//
//        connection.sendMessage(new ImplicitConnectionTestMessage())
//
//        then:
//        noExceptionThrown()
//        startingReceivedReliables == 0
//        startingSequence == 0
//        AwaitUtil.awaitTrue(connection.&isConnected, 5)
//        connection.soeUdpConnection.outgoingMessageQueue.gameNetworkMessagesSent.get() == 1
//        //connection.soeUdpConnection.incomingMessageProcessor.pendingReliablePackets.pendingMap.size() == 0
//        connection.soeUdpConnection.outgoingMessageQueue.udpMessageProcessor.reliableUdpMessageBuilder.sequenceNum.get() == 1
//    }

//    def "Test Broadcast"() {
//
//        setup:
//
//        ConnectionMap connectionMap = new DefaultConnectionMap()
//        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
//        connectionMap.setBroadcastMethod(soeClient.&broadcast)
//        SoeConnection connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, serverPort));
//
//        when:
//
//        connection.connect({udpConnection -> connectionMap.broadcast(new LoginServerOnline())})
//
//        then:
//        noExceptionThrown()
//        AwaitUtil.awaitTrue(connection.&isConnected, 5)
//    }
}
