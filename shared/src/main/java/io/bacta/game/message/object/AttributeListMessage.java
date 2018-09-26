package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.swg.object.AttributeList;
import io.bacta.swg.util.NetworkId;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
public final class AttributeListMessage extends GameNetworkMessage {
    private final long networkId;
    private final String staticItemName;
    private final AttributeList attributeList;
    private final int revision;

    public AttributeListMessage(final long networkId, final AttributeList attributeList, final int revision) {
        this.networkId = networkId;
        this.attributeList = attributeList;
        this.revision = revision;

        this.staticItemName = "";
    }

    public AttributeListMessage(final String staticItemName, final AttributeList attributeList, final int revision) {
        this.attributeList = attributeList;
        this.revision = revision;
        this.staticItemName = staticItemName;

        this.networkId = NetworkId.INVALID;
    }

    public AttributeListMessage(ByteBuffer buffer) {
        networkId = buffer.getLong();
        staticItemName = BufferUtil.getAscii(buffer);
        attributeList = new AttributeList(buffer);
        revision = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, networkId);
        BufferUtil.putAscii(buffer, staticItemName);
        BufferUtil.put(buffer, attributeList);
        BufferUtil.put(buffer, revision);
    }
}
