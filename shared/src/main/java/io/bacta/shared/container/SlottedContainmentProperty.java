package io.bacta.shared.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.shared.foundation.CrcLowerString;
import io.bacta.shared.object.GameObject;
import io.bacta.shared.property.Property;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 4/22/2016.
 */
public class SlottedContainmentProperty extends Property {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlottedContainmentProperty.class);

    public static int MAX_ARRANGEMENT_SIZE = 0xFFF;

    public static int getClassPropertyId() {
        return 0xED3067A9;
    }

    private final List<TIntList> arrangementList = new ArrayList<>();
    @Getter
    private int currentArrangement;

    public SlottedContainmentProperty(final GameObject owner, final SlotIdManager slotIdManager) {
        super(getClassPropertyId(), owner);

        currentArrangement = -1;

        final List<String> anythingSlots = slotIdManager.getSlotsThatHoldAnything();

        for (final String anythingSlot : anythingSlots) {
            final TIntList slotArrangement = new TIntArrayList(1);
            final CrcLowerString anythingSlotCrc = new CrcLowerString(anythingSlot);
            final int slotId = slotIdManager.findSlotId(anythingSlotCrc);
            slotArrangement.add(slotId);
            addArrangement(slotArrangement);
        }
    }

    public void addArrangement(final TIntList arrangement) {
        if (arrangement.size() >= MAX_ARRANGEMENT_SIZE) {
            LOGGER.warn("Cannot add this arrangement. It is too big.");
            return;
        }

        arrangementList.add(arrangement);
    }

    public boolean canManipulateArrangement(final SlotIdManager slotIdManager, final int arrangementIndex) {
        if (arrangementIndex < 0) {
            LOGGER.warn("Passed -1 to canManipulateArrangement");
            return true;
        }

        final int numArrangements = getNumberOfArrangements();

        if (numArrangements == 0)
            return false;

        if (arrangementIndex >= numArrangements) {
            final GameObject owner = getOwner();
            LOGGER.warn("Tried to pass an invalid arrangement index {} for object {}",
                    arrangementIndex, owner.getNetworkId());
            return false;
        }

        final TIntList slotArrangement = arrangementList.get(arrangementIndex);

        //TODO: How can we get a reference to slot id manager here? Have to either hold a reference on the class, or method inject.
        for (int i = 0, size = slotArrangement.size(); i < size; ++i) {
            if (slotIdManager.isSlotPlayerModifiable(slotArrangement.get(i)))
                return false;
        }

        return true;
    }

    /**
     * This function returns the "best" arrangement that has a given slotId.
     * The best arrangement is the one that has the given slotId nearest to the front of
     * the slot list.
     */
    public int getBestArrangementForSlot(final int slotId) {
        int bestValue = MAX_ARRANGEMENT_SIZE;
        int bestArrangement = -1;
        int arrangementIndex = 0;

        for (int i = 0, size = arrangementList.size(); i < size; ++i, ++arrangementIndex) {
            int slotIndex = 0;
            final TIntList slotArrangement = arrangementList.get(i);

            for (int j = 0; j < slotArrangement.size(); ++j, ++slotIndex) {
                if (slotId == slotArrangement.get(j) && slotIndex < bestValue) {
                    bestValue = slotIndex;
                    bestArrangement = arrangementIndex;
                    break;
                }
            }

            if (bestValue == 0)
                break;
        }

        return bestArrangement;
    }

    public int getNumberOfArrangements() {
        return arrangementList.size();
    }

    public int getNumberOfSlots(final int arrangementIndex) {
        final int numArrangements = getNumberOfArrangements();

        if (numArrangements == 0)
            return 0;

        if (arrangementIndex < 0 || arrangementIndex >= numArrangements) {
            LOGGER.warn("Tried to pass an invalid arrangement index to this slotted container.");
            return 0;
        }

        return arrangementList.get(arrangementIndex).size();
    }

    public TIntList getSlotArrangement(final int arrangementIndex) {
        if (arrangementIndex < 0 || arrangementIndex >= getNumberOfArrangements()) {
            LOGGER.warn("Tried to pass an invalid arrangement index to getSlotArrangement");
            return arrangementList.get(0);
        }

        return arrangementList.get(arrangementIndex);
    }

    public int getSlotId(final int arrangementIndex, final int slotIndex) {
        if (arrangementIndex < 0) {
            LOGGER.warn("Tried to pass a negative arrangement index {} to this slotted container.", arrangementIndex);
            return SlotId.INVALID;
        }

        if (slotIndex < 0) {
            LOGGER.warn("Tried to pass a negative slotIndex [%d] to this slotted container", slotIndex);
            return SlotId.INVALID;
        }

        int num = getNumberOfArrangements();

        if (num == 0)
            return SlotId.INVALID;

        if (num < arrangementIndex) {
            LOGGER.warn("Tried to pass an invalid arrangement index to this slotted container");
            return SlotId.INVALID;
        }

        num = getNumberOfSlots(arrangementIndex);

        if (num == 0)
            return SlotId.INVALID;

        if (num < slotIndex) {
            LOGGER.warn("Tried to pass an invalid slot index to this slotted container");
            return SlotId.INVALID;
        }

        return arrangementList.get(arrangementIndex).get(slotIndex);
    }

    public boolean isInAppearanceSlot(final SlotIdManager slotIdManager) {
        final int arrangement = getCurrentArrangement();
        final int numSlots = getNumberOfSlots(arrangement);

        for (int i = 0; i < numSlots; ++i) {
            final int slotId = getSlotId(arrangement, i);

            if (slotIdManager.isSlotAppearanceRelated(slotId))
                return true;
        }

        return false;
    }

    public void setCurrentArrangement(final int arrangement) {
        setCurrentArrangement(arrangement, true);
    }

    public void setCurrentArrangement(final int arrangement, final boolean local) {
        if (currentArrangement != arrangement) {
            final int oldValue = currentArrangement;
            currentArrangement = arrangement;
            getOwner().arrangementModified(oldValue, arrangement, local);
        }
    }
}
