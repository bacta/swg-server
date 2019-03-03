package io.bacta.shared.container;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.shared.foundation.CrcString;
import io.bacta.shared.foundation.PersistentCrcString;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.tre.TreeFile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static io.bacta.shared.foundation.Tag.TAG_0006;
import static io.bacta.shared.foundation.Tag.TAG_DATA;

/**
 * Created by crush on 4/22/2016.
 * <p>
 * The SlotIdManager class maps valid slot names to SlotId objects.
 * For clients, it can also provide the Appearance-related hardpoint names
 * associated with a given slot.
 */
@Singleton
public class SlotIdManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlotIdManager.class);

    private static final String defaultSlotDefinitionFilename = "abstract/slot/slot_definition/slot_definitions.iff";
    private static final boolean defaultLoadHardpointNameData = false;

    private final List<Slot> slots = new ArrayList<>();
    private final Multimap<Integer, Slot> slotsSortedByBone = TreeMultimap.create();
    @Getter
    private final List<String> slotsThatHoldAnything = new ArrayList<>();

    /**
     * Clients should specify true for loadHardpointNameData.  Servers will
     * not need this Appearance-related data loaded.
     *
     * @param treeFile      TreeFile archive contained the Iff file to load the slots.
     * @param configuration BactaConfiguration file containing configuration details.
     */
    @Inject
    public SlotIdManager(final TreeFile treeFile,
                         final BactaConfiguration configuration) {

        final String slotDefinitionsFilename = configuration.getStringWithDefault("SharedObject", "slotDefinitionsFilename", defaultSlotDefinitionFilename);
        final boolean loadHardpointNameData = configuration.getBooleanWithDefault("SharedObject", "loadHardpointNameData", defaultLoadHardpointNameData);

        final Iff iff = new Iff(slotDefinitionsFilename, treeFile.open(slotDefinitionsFilename));

        final int version = iff.getCurrentName();

        if (version == TAG_0006) {
            load0006(iff, loadHardpointNameData);
        } else {
            LOGGER.warn("Unsupported SlotIdManager file format {}", Tag.convertTagToString(version));
        }
    }

    /**
     * Retrieve a SlotId instance for the Slot corresponding to the given
     * slot name.
     * <p>
     * If the specified slot name does not exist, the returned SlotId will be
     * equivalent to SlotId::invalid.
     *
     * @param slotName the name of the slot to lookup.
     * @return a SlotId instance that may be used in future calls to retrieve
     * information regarding the slot.  Also used by the SlottedContainer.
     */
    public int findSlotId(final CrcString slotName) {
        for (int i = 0; i < slots.size(); ++i) {
            final Slot slot = slots.get(i);

            if (slot.slotName.equals(slotName))
                return i;
        }

        return SlotId.INVALID;
    }

    public TIntList findSlotIdsForCombatBone(final int bone) {
        final Collection<Slot> slots = slotsSortedByBone.get(bone);
        final TIntList slotIds = new TIntArrayList(slots.size());

        for (final Slot slot : slots)
            slotIds.add(findSlotId(slot.slotName));

        return slotIds;
    }

    /**
     * Retrieve the name of a slot given the slot's slot id.
     *
     * @param slotId a slot id for the slot under question.
     * @return the name of the slot.
     */
    public CrcString getSlotName(final int slotId) {
        if (slotId == SlotId.INVALID)
            return PersistentCrcString.EMPTY;

        if (slotId < 0 || slotId > slots.size())
            throw new IndexOutOfBoundsException("The slotId was out of bounds.");

        final Slot slot = slots.get(slotId);

        return slot.getSlotName();
    }

    /**
     * Retrieve whether the given slot is allowed to be modified directly by the
     * player.
     *
     * @param slotId a SlotId instance for the slot under question.
     * @return true if the player may modify a slot directly, false otherwise.
     */
    public boolean isSlotPlayerModifiable(final int slotId) {
        if (slotId == SlotId.INVALID)
            return false;

        final Slot slot = slots.get(slotId);

        if (slot != null)
            return slot.isPlayerModifiable();

        return false;
    }

    /**
     * Retrieve whether the given slot corresponds to an appearance slot with
     * a hardpoint.
     * <p>
     * If the slot can have something put in it that directly affects what you
     * see on the client, this will return true.  If this returns false,
     * getSlotHardpointName() will return a NULL string.
     *
     * @param slotId a SlotId instance for the slot under question.
     * @return true if the given slot corresponds to an appearance slot with
     * a hardpoint, false otherwise.
     */
    public boolean isSlotAppearanceRelated(final int slotId) {
        if (slotId == SlotId.INVALID)
            return false;

        final Slot slot = slots.get(slotId);

        return slot != null && slot.isAppearanceRelated();
    }

    public CrcString getSlotHardpointName(final int slotId) {
        if (slotId == SlotId.INVALID)
            return PersistentCrcString.EMPTY;

        final Slot slot = slots.get(slotId);

        if (slot != null)
            return slot.getHardpointName();

        return PersistentCrcString.EMPTY;
    }

    public boolean getSlotObserveWithParent(final int slotId) {
        if (slotId == SlotId.INVALID)
            return false;

        final Slot slot = slots.get(slotId);

        return slot != null && slot.isObserveWithParent();
    }

    public boolean getSlotExposeWithParent(final int slotId) {
        if (slotId == SlotId.INVALID)
            return false;

        final Slot slot = slots.get(slotId);

        return slot != null && slot.isExposeWithParent();
    }

    private void load0006(final Iff iff, boolean loadHardpoitnNameData) {

        iff.enterForm(TAG_0006);
        {
            iff.enterChunk(TAG_DATA);
            {
                while (iff.getChunkLengthLeft() > 0) {
                    final String slotName = iff.readString();
                    final boolean canAcceptAnyItem = iff.readBoolean();
                    final boolean isPlayerModifiable = iff.readBoolean();
                    final boolean isAppearanceRelated = iff.readBoolean();
                    final String hardpointName = iff.readString();
                    final short combatBone = iff.readShort();
                    final boolean observeWithParent = iff.readBoolean();
                    final boolean exposeWithParent = iff.readBoolean();

                    final Slot slot = new Slot(
                            slotName,
                            isPlayerModifiable,
                            isAppearanceRelated,
                            loadHardpoitnNameData ? hardpointName : "",
                            combatBone,
                            observeWithParent,
                            exposeWithParent);

                    slots.add(slot);

                    if (canAcceptAnyItem)
                        slotsThatHoldAnything.add(slotName);

                    for (int mask = 0x8000; mask != 0; mask >>= 1) {
                        if ((mask & combatBone) != 0)
                            slotsSortedByBone.put(mask & combatBone, slot);
                    }
                }
            }
            iff.exitChunk(TAG_DATA);
        }
        iff.exitForm(TAG_0006);

        //sort
        Collections.sort(slots);
    }

    private static class Slot implements Comparable<Slot> {
        @Getter
        private final PersistentCrcString slotName;
        @Getter
        private final boolean isPlayerModifiable;
        @Getter
        private final boolean isAppearanceRelated;
        @Getter
        private final PersistentCrcString hardpointName;
        @Getter
        private final short combatBone;
        @Getter
        private final boolean observeWithParent;
        @Getter
        private final boolean exposeWithParent;

        public Slot(final String slotName,
                    final boolean isPlayerModifiable,
                    final boolean isAppearanceRelated,
                    final String hardpointName,
                    final short combatBone,
                    final boolean observeWithParent,
                    final boolean exposeWithParent) {
            this.slotName = new PersistentCrcString(slotName, true);
            this.isPlayerModifiable = isPlayerModifiable;
            this.isAppearanceRelated = isAppearanceRelated;
            this.hardpointName = new PersistentCrcString(hardpointName, true);
            this.combatBone = combatBone;
            this.observeWithParent = observeWithParent;
            this.exposeWithParent = exposeWithParent;
        }

        @Override
        public int compareTo(final Slot o) {
            return slotName.getString().compareTo(o.slotName.getString());
        }
    }
}
