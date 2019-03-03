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

package io.bacta.soe.serialize;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.utils.SOECRC32;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.game.ObjControllerMessage;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.util.MessageHashUtil;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.netty.util.collection.IntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kyle on 5/4/2016.
 */

@Slf4j
@Component
public class DefaultGameNetworkMessageSerializer implements GameNetworkMessageSerializer, ApplicationContextAware {

    private final ObjControllerMessageSerializer objControllerMessageSerializer;
    private final IntObjectHashMap<Constructor<? extends GameNetworkMessage>> messageConstructorMap;
    private final Map<Class<? extends GameNetworkMessage>, MessageData> messageDataMap;
    private final Histogram histogram;

    private ApplicationContext context;

    @Inject
    public DefaultGameNetworkMessageSerializer(final MetricRegistry metricRegistry, final ObjControllerMessageSerializer objControllerMessageSerializer) {

        this.objControllerMessageSerializer = objControllerMessageSerializer;
        this.histogram = metricRegistry.histogram("GameNetworkMessageBufferAllocations");
        this.messageDataMap = new HashMap<>();
        this.messageConstructorMap = new IntObjectHashMap<>();
    }

    @PostConstruct
    protected void loadMessages() {
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .whitelistPackages("io.bacta")
                             .scan()) {
            ClassInfoList controlClasses = scanResult.getSubclasses(GameNetworkMessage.class.getName());
            List<Class<GameNetworkMessage>> controlClassRefs = controlClasses.loadClasses(GameNetworkMessage.class);
            controlClassRefs.forEach(this::loadMessageClass);
        }
    }

    public void loadMessageClass( Class<? extends GameNetworkMessage> messageClass) {

        final int hash = MessageHashUtil.getHash(messageClass);

        if (messageConstructorMap.containsKey(hash)) {
            LOGGER.error("Message already exists in class map {}", messageClass.getSimpleName());
        }

        try {
            final Constructor<? extends GameNetworkMessage> constructor = messageClass.getConstructor(ByteBuffer.class);

            LOGGER.debug("Putting {} {} in message factory", hash, messageClass.getName());
            messageConstructorMap.put(hash, constructor);

        } catch (NoSuchMethodException e) {
            LOGGER.error("{} does not have a constructor taking ByteBuffer", messageClass.getName(), e);
        }

    }

    @Override
    public <T extends GameNetworkMessage> ByteBuffer writeToBuffer(final T message) {

        // TODO: Better buffer player
        final ByteBuffer buffer = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
        histogram.update(4096);

        MessageData data = messageDataMap.get(message.getClass());

        if (data == null) {
            final Priority priority = message.getClass().getAnnotation(Priority.class);
            final short value = priority == null ? 0x02 : priority.value();

            data = new MessageData(value, SOECRC32.hashCode(message.getClass().getSimpleName()));
            messageDataMap.put(message.getClass(), data);
        }

        return writeToBufferInternal(buffer, data, message);
    }

    private <T extends GameNetworkMessage> ByteBuffer writeToBufferInternal(final ByteBuffer buffer, final MessageData data, final T message) {

        try {
            buffer.putShort(data.getPriority());
            buffer.putInt(data.getType());

            message.writeToBuffer(buffer);

            buffer.limit(buffer.position());
            buffer.position(0);
            return buffer;

        } catch (BufferOverflowException e) {

            final ByteBuffer newBuffer = ByteBuffer.allocate(buffer.limit() * 2).order(ByteOrder.LITTLE_ENDIAN);
            histogram.update(buffer.limit() * 2);
            return writeToBufferInternal(newBuffer, data, message);

        }
    }

    private <T extends GameNetworkMessage> T create(final Constructor<? extends GameNetworkMessage> messageConstructor, final ByteBuffer buffer) {
        try {
            return (T) messageConstructor.newInstance(buffer);
        } catch (Exception e) {
            LOGGER.error("Unable to construct message {}", messageConstructor.getName(), e);
            return null;
        }
    }

    @Override
    public <T extends GameNetworkMessage> T readFromBuffer(int gameMessageType, final ByteBuffer buffer) {
        final Constructor<? extends GameNetworkMessage> messageConstructor = messageConstructorMap.get(gameMessageType);

        if (messageConstructor == null)
            throw new GameNetworkMessageTypeNotFoundException(gameMessageType);

        final T message = create(messageConstructor, buffer);

        //ObjController hack.
        if (message != null && message.getClass() == ObjControllerMessage.class) {
            final ObjControllerMessage objControllerMessage = (ObjControllerMessage) message;
            final GameControllerMessageType type = GameControllerMessageType.from(objControllerMessage.getMessageType());
            final MessageQueueData data = objControllerMessageSerializer.createMessageQueueData(buffer, type);
            objControllerMessage.setData(data);
        }

        return message;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Getter
    @AllArgsConstructor
    private final class MessageData {
        private final short priority;
        private final int type;
    }
}
