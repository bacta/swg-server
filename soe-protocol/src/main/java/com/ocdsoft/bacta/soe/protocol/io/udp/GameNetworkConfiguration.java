package com.ocdsoft.bacta.soe.protocol.io.udp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import lombok.Getter;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Singleton
@Getter
public final class GameNetworkConfiguration extends BaseNetworkConfiguration implements NetworkConfiguration {

    private final int clusterId;
    private final int tcpPort;

    private final InetAddress loginAddress;
    private final int loginPort;

    private final InetAddress chatAddress;
    private final int chatPort;

    @Inject
    public GameNetworkConfiguration(final BactaConfiguration configuration) throws UnknownHostException {
        super(configuration, "Bacta/GameServer");

        clusterId = configuration.getInt("Bacta/GameServer", "clusterId");
        tcpPort = configuration.getInt("Bacta/GameServer", "TcpPort");

        String loginAddressString = configuration.getString("Bacta/LoginServer", "BindAddress");
        if(loginAddressString.equalsIgnoreCase("localhost")) {
            loginAddress = InetAddress.getLocalHost();
        } else {
            loginAddress = InetAddress.getByName(loginAddressString);
        }
        loginPort = configuration.getInt("Bacta/LoginServer", "UdpPort");

        String chatAddressString = configuration.getString("Bacta/ChatServer", "BindAddress");
        if(chatAddressString.equalsIgnoreCase("localhost")) {
            chatAddress = InetAddress.getLocalHost();
        } else {
            chatAddress = InetAddress.getByName(chatAddressString);
        }
        chatPort = configuration.getInt("Bacta/ChatServer", "UdpPort");
    }
}
