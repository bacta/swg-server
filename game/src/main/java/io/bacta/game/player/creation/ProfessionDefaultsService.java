package io.bacta.game.player.creation;


import com.google.common.collect.ImmutableMap;
import io.bacta.shared.foundation.Tag;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.tre.TreeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static io.bacta.shared.foundation.Tag.TAG_0000;

/**
 * Created by crush on 3/29/14.
 */
@Service
public final class ProfessionDefaultsService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String FILENAME = "creation/profession_defaults.iff";

    private final int TAG_PFDT = Tag.convertStringToTag("PFDT");
    private final int TAG_DATA = Tag.convertStringToTag("DATA");

    private final TreeFile treeFile;
    private Map<String, ProfessionInfo> professionDefaults = new HashMap<>();

    @Inject
    public ProfessionDefaultsService(final TreeFile treeFile) {
        this.treeFile = treeFile;

        final Iff iff = new Iff(FILENAME, treeFile.open(FILENAME));
        load(iff);
    }

    public ProfessionInfo getDefaults(final String profession) {
        final ProfessionInfo info = professionDefaults.get(profession);

        if (info == null) {
            LOGGER.error("Could not find profession defaults for profession {}.", profession);
            return null;
        }

        return info;
    }

    private void load(final Iff iff) {
        LOGGER.trace("Loading default professions.");

        iff.enterForm(TAG_PFDT);
        {
            iff.enterForm(TAG_0000);
            {
                final Map<String, ProfessionInfo> localMap = new HashMap<>();

                while (iff.enterChunk(TAG_DATA, true)) {
                    final String profession = iff.readString();
                    final String profFilename = iff.readString();

                    final byte[] bytes = treeFile.open(profFilename);

                    if (bytes == null) {
                        LOGGER.error("Could not open profession file {}", profFilename);
                        continue;
                    }

                    final ProfessionInfo professionInfo = new ProfessionInfo(new Iff(profFilename, bytes));
                    localMap.put(profession, professionInfo);
                    iff.exitChunk(TAG_DATA);
                }

                professionDefaults = ImmutableMap.copyOf(localMap);
            }
            iff.exitForm(TAG_0000);
        }
        iff.exitForm(TAG_PFDT);

        LOGGER.debug("Loaded {} default professions.", professionDefaults.size());
    }
}
