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

package io.bacta.soe.network.connection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to create {@link SoeConnection} class based on provided class
 *
 * @author Kyle Burkhardt
 * @since 1.0
 */
@Slf4j
@Component
public class SoeConnectionFactory {

    private final SoeUdpConnectionFactory udpConnectionFactory;
    private final Map<Class<? extends SoeConnection>, Constructor<? extends SoeConnection>> constructorMap;

    /**
     * Constructor that takes the {@link SoeUdpConnectionFactory}
     * @param udpConnectionFactory
     */
    @Inject
    public SoeConnectionFactory(final SoeUdpConnectionFactory udpConnectionFactory) {
        this.udpConnectionFactory = udpConnectionFactory;
        this.constructorMap = new HashMap<>();
    }

    /**
     * Creates new instance of the provided connection class
     *
     * @param connectionClass type of connection to create
     * @param sender remote address of connection for future routing
     * @return new {@link SoeConnection}
     */
    public SoeConnection newInstance(final Class<? extends SoeConnection> connectionClass, final InetSocketAddress sender) {

        final SoeUdpConnection udpConnection = udpConnectionFactory.newInstance(sender);

        SoeConnection connection = null;
        try {
            Constructor<? extends SoeConnection> connectionConstructor = constructorMap.get(connectionClass);
            if(connectionConstructor == null) {
                constructorMap.put(connectionClass, connectionClass.getConstructor(SoeUdpConnection.class));
                connectionConstructor = constructorMap.get(connectionClass);
            }

            connection = connectionConstructor.newInstance(udpConnection);

        } catch (NoSuchMethodException e) {
            LOGGER.error("Invalid connection class, requires constructor with signature '(SoeUdpConnection)'", e);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            LOGGER.error("Unhandle exception creating connection", e);

        }

        return connection;
    }
}
