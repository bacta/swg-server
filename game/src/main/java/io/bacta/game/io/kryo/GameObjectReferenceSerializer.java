package io.bacta.game.io.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.bacta.game.object.ServerObjectService;
import io.bacta.swg.object.GameObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by kyle on 6/6/2016.
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GameObjectReferenceSerializer extends Serializer<GameObject> {
    private final ServerObjectService serverObjectService;

    @Inject
    public GameObjectReferenceSerializer(final ServerObjectService serverObjectService) {
        this.serverObjectService = serverObjectService;
    }


    @Override
    public void write(Kryo kryo, Output output, GameObject object) {

        if (object == null) {
            kryo.writeClassAndObject(output, -1);
        } else {
            kryo.writeClassAndObject(output, object.getNetworkId());
        }
    }

    @Override
    public GameObject read(Kryo kryo, Input input, Class<GameObject> type) {

        final Registration registration = kryo.readClass(input);
        assert registration.getType() == Long.TYPE;
        final long networkId = kryo.readObject(input, Long.TYPE);
        if (networkId >= 0) {
            return serverObjectService.get(networkId);
        }
        return null;
    }
}