package io.bacta.shared.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.shared.foundation.CrcLowerString;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 4/22/2016.
 * <p>
 * Describes a collection of slots.
 * <p>
 * ObjectTemplate instances that have slotted containers will reference
 * a SlotDescriptor instance.  The SlotDescriptor instance will list
 * the slots available in the slotted container.
 * <p>
 * This class is a shared resource since (1) both server and client
 * need access to this data, (2) we don't want to need to send this
 * data around needlessly, and (3) several ObjectTemplate instances
 * may reference a single SlotDescriptor instance.  For example,
 * most (if not all) player species have the same slots available
 * for equipping.
 */
public class SlotDescriptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlotDescriptor.class);

    private static final int TAG_SLTD = Tag.convertStringToTag("SLTD");

    @Getter
    private final CrcLowerString name;
    @Getter
    private final TIntList slots = new TIntArrayList();
    @Getter
    private volatile int referenceCount;

    public SlotDescriptor(final SlotIdManager slotIdManager, final Iff iff, final CrcLowerString name) {
        this.name = name;

        iff.enterForm(TAG_SLTD);

        final int version = iff.getCurrentName();

        if (version == Tag.TAG_0000) {
            load0000(slotIdManager, iff);
        } else {
            LOGGER.error("Unsupported SlotDescriptor version {}", Tag.convertTagToString(version));
        }

        iff.exitForm(TAG_SLTD);
    }

    public void fetch() {
        ++referenceCount;
    }

    public void release(final SlotDescriptorList slotDescriptorList) {
        --referenceCount;

        if (referenceCount < 1)
            slotDescriptorList.stopTracking(this);
    }

    private void load0000(final SlotIdManager slotIdManager, final Iff iff) {
        iff.enterForm(Tag.TAG_0000);
        {
            iff.enterChunk(Tag.TAG_DATA);
            {
                final CrcLowerString slotName = new CrcLowerString("");

                while (iff.getChunkLengthLeft() > 0) {
                    final String slotNameString = iff.readString();
                    slotName.setString(slotNameString);

                    final int slotId = slotIdManager.findSlotId(slotName);

                    if (slotId == SlotId.INVALID)
                        LOGGER.warn("SlotDescriptor {} specified invalid slot {}, ignoring.", name.getString(), slotNameString);
                    else
                        slots.add(slotId);
                }
            }
            iff.exitChunk(Tag.TAG_DATA);
        }
        iff.exitForm(Tag.TAG_0000);
    }
}
