package io.bacta.game.player.creation;

import com.google.common.collect.ImmutableMap;
import io.bacta.swg.iff.Iff;
import io.bacta.swg.tre.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.bacta.swg.foundation.Tag.TAG_0000;

/**
 * Created by crush on 3/28/14.
 * <p>
 * Loads the creation data used by the client to allow a player to select a hair style during character creation.
 * SOE didn't use this data to validate the hair styles server side, but we learned at SWGEmu that players could
 * easily hack this setting to assign hair to creatures for which it didn't belong. Therefore, we continue the
 * defense of loading this data and checking a player's selection at character creation.
 */
@Slf4j
@Service
public class HairStylesService {
    private static final String HAIRSTYLES_FILENAME = "creation/default_pc_hairstyles.iff";
    private static final String HAIRSTYLES_ASSET_FILENAME = "datatables/customization/hair_assets_skill_mods.iff";

    private static final int TAG_HAIR = Iff.createChunkId("HAIR");

    private Map<String, HairStyleInfo> hairStyles;

    @Inject
    public HairStylesService(final TreeFile treeFile) {
        final Iff hairStylesIff = new Iff(HAIRSTYLES_FILENAME, treeFile.open(HAIRSTYLES_FILENAME));

        loadHairStyles(hairStylesIff);
    }

    private void loadHairStyles(final Iff iff) {
        iff.enterForm(TAG_HAIR);
        {
            iff.enterForm(TAG_0000);
            {
                final int count = iff.getNumberOfBlocksLeft();
                final Map<String, HairStyleInfo> localHairStyles = new HashMap<>(count);

                for (int i = 0; i < count; ++i) {
                    final HairStyleInfo info = new HairStyleInfo(iff);
                    localHairStyles.put(info.getSpeciesGender(), info);
                }

                hairStyles = ImmutableMap.copyOf(localHairStyles);
            }
            iff.exitForm(TAG_0000);
        }
        iff.exitForm(TAG_HAIR);

        LOGGER.debug("Loaded {} hair style mappings.", hairStyles.size());
    }

    /**
     * Checks if the given hair style template is valid for the given player template.
     *
     * @param speciesGender     The speciesGender string for which to check the hair style. i.e. human_male
     * @param hairStyleTemplate The template for the hair style.
     * @return True if the hair style is valid for the player template. Otherwise, false.
     */
    public boolean isValidForPlayerTemplate(final String speciesGender, final String hairStyleTemplate) {
        final HairStyleInfo hairStyleInfo = hairStyles.get(speciesGender);

        if (hairStyleInfo == null)
            return false;

        final Set<String> templateHairStyles = hairStyleInfo.getHairStyles();
        return templateHairStyles != null && templateHairStyles.contains(hairStyleTemplate);
    }

    /**
     * Gets the default hair style for the player template, or returns an empty string if none existed.
     *
     * @param speciesGender The speciesGender for which to lookup the default hair style. i.e. human_male
     * @return The default hair style template or an empty string if it didn't exist.
     */
    public String getDefaultHairStyle(final String speciesGender) {
        final HairStyleInfo hairStyleInfo = hairStyles.get(speciesGender);
        final String defaultHairStyle = hairStyleInfo.getDefaultHairStyle();

        return defaultHairStyle != null ? defaultHairStyle : "";
    }
}
