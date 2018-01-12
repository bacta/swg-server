package io.bacta.soe;

import io.bacta.soe.network.connection.SoeConnection;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class SoeConnectionConfiguration {
    public InetSocketAddress[] getAddresses(Class<? extends SoeConnection> connectionClass) {
        return new InetSocketAddress[0];
    }
}
