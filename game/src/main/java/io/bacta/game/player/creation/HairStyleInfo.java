package io.bacta.game.player.creation;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import io.bacta.swg.iff.Iff;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static io.bacta.swg.foundation.Tag.TAG_NAME;

/**
 * Created by crush on 6/4/2016.
 */
@Getter
public final class HairStyleInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(HairStyleInfo.class);

    private static final int TAG_PTMP = Iff.createChunkId("PTMP");
    private static final int TAG_DEFA = Iff.createChunkId("DEFA");
    private static final int TAG_ITMS = Iff.createChunkId("ITMS");

    private final String speciesGender;
    private final String defaultHairStyle;
    private final Set<String> hairStyles;

    public HairStyleInfo(final Iff iff) {
        iff.enterForm(TAG_PTMP);
        {
            //Template name
            iff.enterChunk(TAG_NAME);
            {
                final String playerTemplate = iff.readString();
                //We are just going to store the speciesGender string portion.
                speciesGender = Files.getNameWithoutExtension(playerTemplate).replace("shared_", "");
            }
            iff.exitChunk(TAG_NAME);

            //Default hair style
            if (iff.enterChunk(TAG_DEFA, true)) {
                defaultHairStyle = iff.readString().replace("shared_", "");
                iff.exitChunk(TAG_DEFA);
            } else {
                defaultHairStyle = "";
            }

            //Hair styles
            if (iff.enterChunk(TAG_ITMS, true)) {
                final Set<String> localHairStyles = new HashSet<>(30);

                while (iff.getChunkLengthLeft() > 0)
                    localHairStyles.add(iff.readString().replace("shared_", ""));

                hairStyles = ImmutableSet.copyOf(localHairStyles);

                iff.exitChunk(TAG_ITMS);
            } else {
                hairStyles = ImmutableSet.of();
            }
        }
        iff.exitForm(TAG_PTMP);

        LOGGER.debug("Loaded hair styles for species_gender [{}] with default [{}] and [{}] hair styles.",
                speciesGender,
                defaultHairStyle,
                hairStyles.size());
    }
}
