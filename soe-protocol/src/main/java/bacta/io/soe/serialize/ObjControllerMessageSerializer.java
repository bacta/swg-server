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

package bacta.io.soe.serialize;


import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by crush on 5/29/2016.
 */
@Slf4j
public final class ObjControllerMessageSerializer {

    private final Map<GameControllerMessageType, Constructor<? extends MessageQueueData>> messageQueueDataConstructorMap;

    public ObjControllerMessageSerializer() {
        this.messageQueueDataConstructorMap = new HashMap<>();
        loadMessages();
    }

    private void loadMessages() {
        final Reflections reflections = new Reflections();
        final Set<Class<? extends MessageQueueData>> subTypes = reflections.getSubTypesOf(MessageQueueData.class);
        subTypes.forEach(this::loadMessageClass);
    }

    private void loadMessageClass(final Class<? extends MessageQueueData> messageClass) {
        final GameControllerMessage gameControllerMessage = messageClass.getAnnotation(GameControllerMessage.class);

        for (final GameControllerMessageType type : gameControllerMessage.value()) {
            if (messageQueueDataConstructorMap.containsKey(type)) {
                LOGGER.error("{} cannot handle GameControllerMessageType {} because it is already being handled by class {}.",
                        messageClass.getName(),
                        type,
                        messageQueueDataConstructorMap.get(type).getClass().getName());
            }

            try {
                final Constructor<? extends MessageQueueData> constructor = messageClass.getConstructor(ByteBuffer.class);

                LOGGER.debug("Setting {} to handle GameControllerMessageType {}.", messageClass.getName(), type);
                messageQueueDataConstructorMap.put(type, constructor);

            } catch (NoSuchMethodException e) {
                LOGGER.error("{} does not have a constructor taking ByteBuffer", messageClass.getName(), e);
            }
        }
    }

    public MessageQueueData createMessageQueueData(final ByteBuffer buffer, final GameControllerMessageType type) {
        final Constructor<? extends MessageQueueData> messageConstructor = messageQueueDataConstructorMap.get(type);

        if (messageConstructor == null)
            throw new MessageQueueDataTypeNotFoundException(type);

        try {
            return messageConstructor.newInstance(buffer);
        } catch (Exception e) {
            LOGGER.error("Unable to construct message {}", messageConstructor.getName(), e);
            return null;
        }
    }
}
