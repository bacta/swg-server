package bacta.io.conf;

import java.net.InetAddress;

/**
 * Created by kyle on 4/14/2017.
 */
public interface NetworkConfiguration {
    InetAddress getBindAddress();
    int getBindPort();
}

