package com.ocdsoft.bacta.soe.network.dispatch;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.network.controller.SoeController;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.SoeMessageController;
import com.ocdsoft.bacta.soe.network.message.SoeMessage;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import com.ocdsoft.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Scope("prototype")
public final class SoeDevMessageDispatcher implements SoeMessageDispatcher, ApplicationContextAware {

    private final Map<SoeMessageType, SoeMessageController> controllers = new HashMap<>();
    private final GameNetworkMessageDispatcher gameNetworkMessageDispatcher;

    public SoeDevMessageDispatcher(final GameNetworkMessageDispatcher gameNetworkMessageDispatcher) {
        this.gameNetworkMessageDispatcher = gameNetworkMessageDispatcher;
    }

    @Override
    public void dispatch(SoeUdpConnection client, ByteBuffer buffer) {

        byte zeroByte = buffer.get();
        byte type = buffer.get();
        if(type < 0 || type > 0x1E) {
            throw new RuntimeException("Type out of range: {} {}" + type + " " + buffer.toString() + " " + SoeMessageUtil.bytesToHex(buffer));
        }

        SoeMessageType packetType = SoeMessageType.values()[type];

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

    private void load(final ApplicationContext applicationContext) {

        String[] controllerBeanNames = applicationContext.getBeanNamesForType(SoeMessageController.class);
        controllers.clear();

        for (String controllerBeanName : controllerBeanNames) {

            SoeMessageController controller = (SoeMessageController) applicationContext.getBean(controllerBeanName);

            try {
                
                Class<? extends SoeMessageController> controllerClass = controller.getClass();
                
                if(Modifier.isAbstract(controllerClass.getModifiers())) {
                    continue;
                }
                
                SoeController controllerAnnotation = controllerClass.getAnnotation(SoeController.class);

                if (controllerAnnotation == null) {
                    LOGGER.info("Missing @SoeController annotation, discarding: {}", controllerClass.getName());
                    continue;
                }

                SoeMessageType[] types = controllerAnnotation.handles();
                LOGGER.debug("Loading SoeMessageController: {}", controllerClass.getSimpleName());

                controller.setSoeMessageDispatcher(this);
                controller.setGameNetworkMessageDispatcher(gameNetworkMessageDispatcher);

                for(SoeMessageType udpPacketType : types) {

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

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        load(applicationContext);
    }
}
