package io.bacta.game.message;


import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.tre.foundation.Crc;
import io.bacta.shared.tre.math.Transform;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Priority(0x5)
public final class SceneCreateObjectByCrc extends GameNetworkMessage {

    private final long networkId;
    private final Transform transform;
    private final int crc;

    // This byte has to do with hyper space
    private final byte hyperspace;

    public SceneCreateObjectByCrc(long objectId) {

        networkId = objectId;//scno.getNetworkId();
        transform = new Transform();//scno.getTransformObjectToParent();
        crc = Crc.calculate("object/creature/player/shared_human_male.iff");//scno.getSharedTemplate().getCrcName().getCrc();
        hyperspace = 0;
    }

    public SceneCreateObjectByCrc(final ByteBuffer buffer) {
        networkId = buffer.getLong();
        transform = new Transform(buffer);
        crc = buffer.getInt();
        hyperspace = buffer.get();
    }
    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

        //NetworkId networkId
        //Transform transform
        //int templateCrc
        //bool hyperspace

        buffer.putLong(networkId);  // NetworkID
        transform.writeToBuffer(buffer);
        buffer.putInt(crc); // Client ObjectCRC
        buffer.put(hyperspace);
    }


}
