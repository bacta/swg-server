package io.bacta.shared.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.shared.foundation.CrcLowerString;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 4/22/2016.
 */
public class ArrangementDescriptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrangementDescriptor.class);

    private static final int TAG_ARG = Tag.convertStringToTag("ARG ");
    private static final int TAG_ARGD = Tag.convertStringToTag("ARGD");

    @Getter
    private final CrcLowerString name;
    private final List<TIntList> arrangements = new ArrayList<>();
    @Getter
    private volatile int referenceCount;

    public ArrangementDescriptor(final SlotIdManager slotIdManager, final Iff iff, final CrcLowerString name) {
        this.name = name;

        iff.enterForm(TAG_ARGD);
        {
            final int version = iff.getCurrentName();

            if (version == Tag.TAG_0000) {
                load0000(slotIdManager, iff);
            } else {
                LOGGER.error("Unsupported ArrangementDescriptor version [{}]", Tag.convertTagToString(version));
            }
        }
        iff.exitForm(TAG_ARGD);
    }

    public int getArrangementCount() {
        return arrangements.size();
    }

    public TIntList getArrangement(final int index) {
        return arrangements.get(index);
    }

    public void fetch() {
        ++referenceCount;
    }

    public void release(final ArrangementDescriptorList arrangementDescriptorList) {
        --referenceCount;

        if (referenceCount < 1) {
            arrangementDescriptorList.stopTracking(this);
        }
    }

    private void load0000(final SlotIdManager slotIdManager, final Iff iff) {
        iff.enterForm(Tag.TAG_0000);
        {
            final CrcLowerString slotName = new CrcLowerString("");

            while (!iff.isAtEndOfForm()) {
                iff.enterChunk(TAG_ARG);
                {
                    final TIntArrayList arrangement = new TIntArrayList();

                    while (iff.getChunkLengthLeft() > 0) {
                        final String slotNameString = iff.readString();
                        slotName.setString(slotNameString);

                        final int slotId = slotIdManager.findSlotId(slotName);

                        if (slotId == SlotId.INVALID) {
                            LOGGER.warn("ArrangementDescriptor [{}] specified invalid slot name [{}], ignoring",
                                    name.getString(), slotName.getString());
                        } else {
                            arrangement.add(slotId);
                        }
                    }

                    arrangement.trimToSize();
                    arrangements.add(arrangement);
                }
                iff.exitChunk(TAG_ARG);
            }
        }
        iff.exitForm(Tag.TAG_0000);
    }
}
