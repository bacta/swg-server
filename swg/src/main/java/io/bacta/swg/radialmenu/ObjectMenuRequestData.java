package io.bacta.swg.radialmenu;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/29/2016.
 */
@Getter
public final class ObjectMenuRequestData implements ByteBufferWritable {
    /**
     * Flag indicating if this option on the radial menu is enabled or disabled.
     */
    public static final byte ENABLED = 0x01;
    /**
     * Flag indicating if the server should be notified when this item is selected.
     */
    public static final byte SERVER_NOTIFY = 0x02;
    /**
     * Flag indicating if this item should be disabled when out of range.
     */
    public static final byte OUT_OF_RANGE = 0x04;

    private final byte id;
    private final byte parentId;
    private final short menuItemType; //Menu item action id. (i.e. ITEM_ROTATE_RIGHT)
    private final byte flags;
    private final String label; //unicode
    //private final StringId labelId;

    public ObjectMenuRequestData(final byte id,
                                 final byte parentId,
                                 final short menuItemType,
                                 final String label,
                                 final boolean enabled,
                                 final boolean serverNotify) {
        this.id = id;
        this.parentId = parentId;
        this.menuItemType = menuItemType;
        this.label = label;

        byte tempFlags = 0;

        if (enabled)
            tempFlags |= ENABLED;

        if (serverNotify)
            tempFlags |= SERVER_NOTIFY;

        this.flags = tempFlags;
    }

    public ObjectMenuRequestData(final ByteBuffer buffer) {
        id = buffer.get();
        parentId = buffer.get();
        menuItemType = buffer.getShort();
        flags = buffer.get();
        label = BufferUtil.getUnicode(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, id);
        BufferUtil.put(buffer, parentId);
        BufferUtil.put(buffer, menuItemType);
        BufferUtil.put(buffer, flags);
        BufferUtil.putUnicode(buffer, label);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ObjectMenuRequestData that = (ObjectMenuRequestData) o;

        return getId() == that.getId() &&
                getParentId() == that.getParentId() &&
                getMenuItemType() == that.getMenuItemType();
    }

    @Override
    public int hashCode() {
        int result = (int) getId();
        result = 31 * result + (int) getParentId();
        result = 31 * result + (int) getMenuItemType();
        return result;
    }
}
