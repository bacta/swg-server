package io.bacta.shared.container;

import gnu.trove.list.TIntList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import io.bacta.shared.object.GameObject;

/**
 * Created by crush on 8/26/2014.
 */
public class SlottedContainer extends Container {
    public static int getClassPropertyId() {
        return 0x7ED71F2E;
    }

    /**
     * This is the map of slot ids to contents. It is initialized with -1 values.
     * Anything greater than or equal to 0 indicates a position in the contents list.
     */
    private final TIntIntMap slotMap = new TIntIntHashMap();

    public SlottedContainer(final GameObject owner, final TIntList validSlots) {
        super(getClassPropertyId(), owner);

        //Initialize the slot map with "empty" values.
        for (int i = 0, size = validSlots.size(); i < size; ++i)
            slotMap.put(validSlots.get(i), -1);
    }
}
