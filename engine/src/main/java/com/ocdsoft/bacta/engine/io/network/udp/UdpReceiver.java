package com.ocdsoft.bacta.engine.io.network.udp;

import com.ocdsoft.bacta.engine.context.ShutdownListener;
import org.springframework.beans.factory.DisposableBean;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 **/
public interface UdpReceiver extends DisposableBean {
	void receiveMessage(InetSocketAddress client, ByteBuffer msg);
}
