package io.bacta.game.player.creation;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.bacta.shared.foundation.Tag.TAG_0000;


/**
 * Created by crush on 6/3/2016.
 */
public final class ProfessionInfo {
    private static final int TAG_PRFI = Tag.convertStringToTag("PRFI");
    private static final int TAG_SKLS = Tag.convertStringToTag("SKLS");
    private static final int TAG_SKIL = Tag.convertStringToTag("SKIL");
    private static final int TAG_PTMP = Tag.convertStringToTag("PTMP");
    private static final int TAG_NAME = Tag.convertStringToTag("NAME");
    private static final int TAG_ITEM = Tag.convertStringToTag("ITEM");

    @Getter
    private List<String> skills;
    private Map<String, List<EquipmentInfo>> equipment;

    public List<EquipmentInfo> getEquipmentForTemplate(final String sharedTemplateName) {
        return equipment.get(sharedTemplateName);
    }

    public ProfessionInfo(final Iff iff) {
        load(iff);
    }

    private void load(final Iff iff) {
        iff.enterForm(TAG_PRFI);
        {
            iff.enterForm(TAG_0000);
            {
                loadSkills(iff);
                loadEquipment(iff);
            }
            iff.exitForm(TAG_0000);
        }
        iff.exitForm(TAG_PRFI);
    }

    private void loadSkills(final Iff iff) {
        iff.enterForm(TAG_SKLS);
        {
            final int size = iff.getNumberOfBlocksLeft();
            final List<String> localSkills = new ArrayList<>(size);

            while (iff.enterChunk(TAG_SKIL, true)) {
                localSkills.add(iff.readString());
                iff.exitChunk(TAG_SKIL);
            }

            skills = ImmutableList.copyOf(localSkills);
        }
        iff.exitForm(TAG_SKLS);
    }

    private void loadEquipment(final Iff iff) {
        final Map<String, List<EquipmentInfo>> localMap = new HashMap<>();

        while (iff.enterForm(TAG_PTMP, true)) {

            iff.enterChunk(TAG_NAME);
            final String playerTemplateString = iff.readString();
            iff.exitChunk(TAG_NAME);

            final int equipmentSize = iff.getNumberOfBlocksLeft();
            final List<EquipmentInfo> localEquipment = new ArrayList<>(equipmentSize);

            while (iff.enterChunk(TAG_ITEM, true)) {
                final int arrangementIndex = iff.readInt();
                final String sharedTemplateName = iff.readString();
                final String serverTemplateName = iff.readString();

                localEquipment.add(new EquipmentInfo(arrangementIndex, sharedTemplateName, serverTemplateName));
                iff.exitChunk(TAG_ITEM);
            }

            localMap.put(playerTemplateString, ImmutableList.copyOf(localEquipment));
            iff.exitForm();
        }

        equipment = ImmutableMap.copyOf(localMap);
    }
}
