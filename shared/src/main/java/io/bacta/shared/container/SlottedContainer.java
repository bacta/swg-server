package io.bacta.shared.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
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

    private final SlotIdManager slotIdManager;

    /**
     * This is the map of slot ids to contents. It is initialized with -1 values.
     * Anything greater than or equal to 0 indicates a position in the contents list.
     */
    private final TIntIntMap slotMap = new TIntIntHashMap();

    public SlottedContainer(final SlotIdManager slotIdManager, final GameObject owner, final TIntList validSlots) {
        super(getClassPropertyId(), owner);

        this.slotIdManager = slotIdManager;

        //Initialize the slot map with "empty" values.
        for (int i = 0, size = validSlots.size(); i < size; ++i)
            slotMap.put(validSlots.get(i), -1);
    }

    public boolean addToSlot(final GameObject item, final int slotId) throws
            ContainerTransferException {

        final SlottedContainmentProperty slottedContainmentProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainmentProperty == null)
            throw containerException(item, ContainerErrorCode.UNKNOWN);

        final int arrangementIndex = slottedContainmentProperty.getBestArrangementForSlot(slotId);

        return add(item, arrangementIndex);
    }

    public boolean add(final GameObject item, final int arrangementIndex) throws
            ContainerTransferException {

        if (arrangementIndex < 0)
            throw containerException(item, ContainerErrorCode.INVALID_ARRANGEMENT);

        //TODO: Can we remove this since its in the base container?
        //We could, but short circuiting here could save some cycles.
        if (item.getNetworkId() == getOwner().getNetworkId())
            throw containerException(item, ContainerErrorCode.ADD_SELF);

        final SlottedContainmentProperty slottedContainmentProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainmentProperty == null)
            throw containerException(item, ContainerErrorCode.UNKNOWN);

        final int numSlots = slottedContainmentProperty.getNumberOfSlots(arrangementIndex);

        //We unfortunately need to check all the slots before we actually set any slots.
        //If one all fails, we need to fail the transfer. Therefore, we loop over them all here.
        //If no exception is thrown, then we will loop again after the container contents add.
        for (int i = 0; i < numSlots; ++i) {
            final int slotId = slottedContainmentProperty.getSlotId(arrangementIndex, i);
            ensureSlotAvailable(item, slotId);
        }

        final int position = addToContents(item);

        if (position < 0)
            return false;

        //It's safe to add the item now.
        for (int i = 0; i < numSlots; ++i) {
            final int slotId = slottedContainmentProperty.getSlotId(arrangementIndex, i);
            slotMap.put(slotId, position);
        }

        slottedContainmentProperty.setCurrentArrangement(arrangementIndex);

        return true;
    }

    public boolean hasSlot(final int slotId) {
        return slotMap.containsKey(slotId);
    }

    public boolean isSlotEmpty(final int slotId) {
        return hasSlot(slotId) && slotMap.get(slotId) == -1;
    }

    public int getFirstUnoccupiedArrangement(final GameObject item)
            throws ContainerTransferException {

        final TIntList validArrangements = getValidArrangements(item, true, true);

        return validArrangements.size() > 0 ? validArrangements.get(0) : -1;
    }

    public TIntList getValidArrangements(final GameObject item, boolean returnOnFirst, boolean unoccupedArrangementsOnly)
            throws ContainerTransferException {

        final SlottedContainmentProperty slottedContainmentProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainmentProperty == null)
            throw containerException(item, ContainerErrorCode.WRONG_TYPE);

        final int totalArrangements = slottedContainmentProperty.getNumberOfArrangements();
        final TIntList validArrangements = new TIntArrayList(totalArrangements);

        for (int arrangementIndex = 0; arrangementIndex < totalArrangements; ++arrangementIndex) {
            final int numSlots = slottedContainmentProperty.getNumberOfSlots(arrangementIndex);
            int availableSlots = 0;
            boolean slotPresent = true;

            for (int slotIndex = 0; slotIndex < numSlots; ++slotIndex) {
                final int slotId = slottedContainmentProperty.getSlotId(arrangementIndex, slotIndex);

                if (!hasSlot(slotId)) {
                    slotPresent = false;
                    break;
                }

                if (unoccupedArrangementsOnly && isSlotEmpty(slotId))
                    ++availableSlots;
            }

            if (slotPresent) {
                if (!unoccupedArrangementsOnly || (availableSlots == numSlots)) {
                    validArrangements.add(arrangementIndex);

                    if (returnOnFirst)
                        return validArrangements;
                }
            }
        }

        //If we found some arrangements, return them.
        if (validArrangements.size() > 0)
            return validArrangements;

        //Otherwise, slot is occupied.
        throw containerException(item, ContainerErrorCode.SLOT_OCCUPIED);
    }

    private void ensureSlotAvailable(final GameObject item, final int slotId) throws
            ContainerTransferException {

        if (!hasSlot(slotId))
            throw containerException(item, ContainerErrorCode.NO_SLOT);

        if (!isSlotEmpty(slotId))
            throw containerException(item, ContainerErrorCode.SLOT_OCCUPIED);
    }
}
