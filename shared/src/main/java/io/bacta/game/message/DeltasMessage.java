package io.bacta.game.message;

import io.bacta.archive.delta.AutoDeltaByteStream;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.swg.object.GameObject;
import lombok.Getter;

import java.nio.ByteBuffer;

@Priority(0x5)
public final class DeltasMessage extends GameNetworkMessage {
    public static final byte DELTAS_CLIENT_ONLY = 0;
    public static final byte DELTAS_CLIENT_SERVER = 1;
    public static final byte DELTAS_SERVER = 2;
    public static final byte DELTAS_SHARED = 3;
    public static final byte DELTAS_CLIENT_SERVER_NP = 4;
    public static final byte DELTAS_SERVER_NP = 5;
    public static final byte DELTAS_SHARED_NP = 6;
    public static final byte DELTAS_UI = 7;
    public static final byte DELTAS_FIRST_PARENT_CLIENT_SERVER = 8;
    public static final byte DELTAS_FIRST_PARENT_CLIENT_SERVER_NP = 9;

    @Getter
    private final long target;
    @Getter
    private final int typeId;
    @Getter
    private final ByteBuffer packageBuffer;
    @Getter
    private final byte packageId;

    public DeltasMessage(final GameObject object, final AutoDeltaByteStream stream, final int packageId) {
        this.target = object.getNetworkId();
        this.typeId = object.getObjectType();
        this.packageId = (byte) packageId;

        this.packageBuffer = ByteBuffer.allocate(4096); //Need to figure out a way to optimize this?
        stream.packDeltas(this.packageBuffer);
    }

    public DeltasMessage(final ByteBuffer buffer) {
        this.target = buffer.getLong();
        this.typeId = buffer.getInt();
        this.packageId = buffer.get();

        final int size = buffer.getInt();

        this.packageBuffer = ByteBuffer.allocate(size);
        this.packageBuffer.put(buffer.array(), buffer.position(), size);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putLong(target);
        buffer.putInt(typeId);
        buffer.put(packageId);

        buffer.putInt(packageBuffer.position());
        buffer.put(packageBuffer.array(), 0, packageBuffer.position());
    }
}
