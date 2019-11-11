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

package io.bacta.soe.network.dispatch;

import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.controller.SoeController;
import io.bacta.soe.network.controller.SoeMessageController;
import io.bacta.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kyle on 4/22/2016.
 */
@Slf4j
public final class SoeControllerLoader {

    private SoeControllerLoader() {}

    public static Map<SoeMessageType, SoeMessageController> loadControllers(final ApplicationContext applicationContext,
                                                                            final SoeUdpConnectionCache connectionCache,
                                                                            final SoeMessageHandler handler) {

        final Map<SoeMessageType, SoeMessageController> controllers = new HashMap<>();

        String[] controllerBeanNames = applicationContext.getBeanNamesForType(SoeMessageController.class);

        for (String controllerBeanName : controllerBeanNames) {

            SoeMessageController controller = (SoeMessageController) applicationContext.getBean(controllerBeanName);
            controller.setSoeHandler(handler);
            controller.setSoeConnectionCache(connectionCache);

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
        return controllers;
    }
}
