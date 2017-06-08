package com.ocdsoft.bacta.soe.protocol.network.dispatch;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.controller.SoeController;
import com.ocdsoft.bacta.soe.protocol.network.controller.SoeMessageController;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import com.ocdsoft.bacta.soe.protocol.util.SoeMessageUtil;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Controllers are required to exist in the com.ocdsoft.bacta.soe.protocol.network.controller package to
 * be loaded.
 */
@Singleton
public final class SoeDevMessageDispatcher implements SoeMessageDispatcher {

    private final static Logger LOGGER = LoggerFactory.getLogger(SoeDevMessageDispatcher.class);

    private Map<UdpPacketType, SoeMessageController> controllers = new HashMap<>();

    private final GameNetworkMessageDispatcher gameNetworkMessageDispatcher;

    @Inject
    public SoeDevMessageDispatcher(final Injector injector, final GameNetworkMessageDispatcher gameNetworkMessageDispatcher) {
        this.gameNetworkMessageDispatcher = gameNetworkMessageDispatcher;

        if(injector != null) {
            load(injector);
        }
    }

    @Override
    public void dispatch(SoeUdpConnection client, ByteBuffer buffer) {

        byte zeroByte = buffer.get();
        byte type = buffer.get();
        if(type < 0 || type > 0x1E) {
            throw new RuntimeException("Type out of range: {} {}" + type + " " + buffer.toString() + " " + SoeMessageUtil.bytesToHex(buffer));
        }

        UdpPacketType packetType = UdpPacketType.values()[type];

        SoeMessageController controller = controllers.get(packetType);

        if (controller == null) {
            LOGGER.error("Unhandled SOE Opcode 0x{}", Integer.toHexString(packetType.ordinal()).toUpperCase());
            LOGGER.error(SoeMessageUtil.bytesToHex(buffer));
            return;
        }

        try {

            LOGGER.trace("Routing to {} : {}", controller.getClass().getSimpleName(), BufferUtil.bytesToHex(buffer));
            controller.handleIncoming(zeroByte, packetType, client, buffer);

        } catch (Exception e) {
            LOGGER.error("SOE Routing", e);
        }
    }

    public void load(final Injector injector) {
        
        Reflections reflections = new Reflections();

        Set<Class<? extends SoeMessageController>> subTypes = reflections.getSubTypesOf(SoeMessageController.class);

        Iterator<Class<? extends SoeMessageController>> iter = subTypes.iterator();

        controllers.clear();

        while (iter.hasNext()) {

            try {
                
                Class<? extends SoeMessageController> controllerClass = iter.next();
                
                if(Modifier.isAbstract(controllerClass.getModifiers())) {
                    continue;
                }
                
                SoeController controllerAnnotation = controllerClass.getAnnotation(SoeController.class);

                if (controllerAnnotation == null) {
                    LOGGER.info("Missing @SoeController annotation, discarding: {}", controllerClass.getName());
                    continue;
                }

                UdpPacketType[] types = controllerAnnotation.handles();
                LOGGER.debug("Loading SoeMessageController: {}", controllerClass.getSimpleName());

                SoeMessageController controller = injector.getInstance(controllerClass);
                controller.setSoeMessageDispatcher(this);
                controller.setGameNetworkMessageDispatcher(gameNetworkMessageDispatcher);

                for(UdpPacketType udpPacketType : types) {

                    if (!controllers.containsKey(udpPacketType)) {
                        LOGGER.trace("Adding SOE controller: {} {}", udpPacketType.name(), controller.getClass().getSimpleName());
                        synchronized (controllers) {
                            controllers.put(udpPacketType, controller);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Unable to add controller", e);
            }
        }
    }
}
