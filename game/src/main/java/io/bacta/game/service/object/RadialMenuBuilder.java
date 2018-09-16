package io.bacta.game.service.object;

import com.google.common.collect.ImmutableList;
import io.bacta.shared.radialmenu.ObjectMenuRequestData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 5/30/2016.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RadialMenuBuilder {
    private static final int DEFAULT_SIZE = 10;

    private final List<ObjectMenuRequestData> menu;

    public static RadialMenuBuilder newBuilder() {
        return newBuilder(DEFAULT_SIZE);
    }

    public static RadialMenuBuilder newBuilder(final int size) {
        return new RadialMenuBuilder(new ArrayList<>(size));
    }

    public RadialSubMenuBuilder root(final short menuItemType, final String label, final boolean notifyServer) {
        final byte id = getNextId();
        final byte parentId = 0;
        final ObjectMenuRequestData root = new ObjectMenuRequestData(id, parentId, menuItemType, label, true, notifyServer);
        menu.add(root);

        return new RadialSubMenuBuilder(this, id);
    }

    public List<ObjectMenuRequestData> build() {
        return ImmutableList.copyOf(menu);
    }

    private byte getNextId() {
        byte id = 1;
        for (final ObjectMenuRequestData data : menu) {
            id = id > data.getId() ? id : (byte) (data.getId() + 1);
        }
        return id;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RadialSubMenuBuilder {
        private final RadialMenuBuilder parent;
        private final byte parentId;

        /**
         * Adds an item to the root menu item of which this instance is related.
         *
         * @param menuItemType The item type for the menu entry. {@link ObjectMenuService#getMenuTypeByName} allows you to gather
         *                     these by name.
         * @param label        The custom label to show for the option. This will override any default label that is associated
         *                     with the client-side radial menu item type.
         * @param notifyServer Enables the item to notify the server when it has been selected.
         * @return The RadialSubMenuBuilder instance to which this item was appended.
         */
        public RadialSubMenuBuilder item(final short menuItemType, final String label, final boolean notifyServer) {
            final byte id = parent.getNextId();
            final ObjectMenuRequestData item = new ObjectMenuRequestData(id, parentId, menuItemType, label, true, notifyServer);
            parent.menu.add(item);

            return this;
        }

        public RadialSubMenuBuilder root(final short menuItemType, final String label, final boolean notifyServer) {
            return parent.root(menuItemType, label, notifyServer);
        }

        /**
         * Links back to the RadialMenuBuilder instance to which this belongs. Once you are back at the parent,
         * no method exists to re-enter. You may store a reference to this builder though, and continue to use it
         * to add items later.
         *
         * @return The parent RadialMenuBuilder instance.
         */
        public RadialMenuBuilder parent() {
            return parent;
        }

        public List<ObjectMenuRequestData> build() {
            return parent.build();
        }
    }
}
