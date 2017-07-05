package com.ocdsoft.bacta.soe.serialize;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.network.message.game.GameNetworkMessage;
import com.ocdsoft.bacta.network.message.game.GameControllerMessageType;
import com.ocdsoft.bacta.network.message.game.MessageQueueData;
import com.ocdsoft.bacta.network.message.game.ObjControllerMessage;
import com.ocdsoft.bacta.network.message.game.Priority;
import com.ocdsoft.bacta.soe.util.MessageHashUtil;
import com.ocdsoft.bacta.soe.util.SOECRC32;

import io.netty.util.collection.IntObjectHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kyle on 5/4/2016.
 */

@Slf4j
@Component
@Scope("prototype")
public class GameNetworkMessageSerializerImpl implements GameNetworkMessageSerializer {

    private final ObjControllerMessageSerializer objControllerMessageSerializer;
    private final IntObjectHashMap<Constructor<? extends GameNetworkMessage>> messageConstructorMap;
    private final Map<Class<? extends GameNetworkMessage>, MessageData> messageDataMap;
    private final Histogram histogram;

    @Inject
    public GameNetworkMessageSerializerImpl(final MetricRegistry metricRegistry) {

        this.objControllerMessageSerializer = new ObjControllerMessageSerializer();
        this.histogram = metricRegistry.histogram("GameNetworkMessageBufferAllocations");
        this.messageDataMap = new HashMap<>();
        this.messageConstructorMap = new IntObjectHashMap<>();

        loadMessages();
    }

    private void loadMessages() {
        final Reflections reflections = new Reflections();
        final Set<Class<? extends GameNetworkMessage>> subTypes = reflections.getSubTypesOf(GameNetworkMessage.class);
        subTypes.forEach(this::loadMessageClass);
    }

    public void loadMessageClass(Class<? extends GameNetworkMessage> messageClass) {

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
            buffer.rewind();
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

    @Getter
    @AllArgsConstructor
    private final class MessageData {
        private final short priority;
        private final int type;
    }
}
