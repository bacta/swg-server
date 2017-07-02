package com.ocdsoft.bacta.engine.conf;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Created by kyle on 4/14/2017.
 */
public interface NetworkConfiguration {
    InetAddress getBindAddress();
    int getBindPort();
}
