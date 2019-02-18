package io.bacta.swg.snapshot;

import io.bacta.swg.foundation.Crc;
import io.bacta.swg.foundation.Tag;
import io.bacta.swg.iff.Iff;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static io.bacta.swg.foundation.Tag.TAG_0001;

/**
 * Reads the contents of a WorldSnapshot IFF file. These files typically exist on the client only, but the server
 * must have these objects in its object database for reference. Therefore, this reader call can be used to read the
 * world snapshot files to seed to the object database on galaxy initialization.
 */
@Slf4j
@Getter
public final class WorldSnapshotIffReader {
    public static final int TAG_NODE = Tag.convertStringToTag("NODE"); //Node
    public static final int TAG_NODS = Tag.convertStringToTag("NODS"); //Node List
    public static final int TAG_OTNL = Tag.convertStringToTag("OTNL"); //ObjectTemplateNameList
    public static final int TAG_WSNP = Tag.convertStringToTag("WSNP"); //WorldSnapshot

    public WorldSnapshot load(final Iff iff) {
        final WorldSnapshot snapshot;

        iff.enterForm(TAG_WSNP);
        {
            final int version = iff.getCurrentName();

            if (version == TAG_0001) {
                snapshot = loadVersion0001(iff);
            } else {
                LOGGER.warn("World snapshot file with version {} unsupported.", Tag.convertTagToString(version));
                snapshot = null;
            }
        }
        iff.exitForm(TAG_WSNP);

        return snapshot;
    }

    private WorldSnapshot loadVersion0001(final Iff iff) {
        final WorldSnapshot snapshot = new WorldSnapshot();

        iff.enterForm(TAG_0001);
        {
            iff.enterForm(TAG_NODS);
            {
                while (iff.getNumberOfBlocksLeft() != 0) {
                    final WorldSnapshotNode node = new WorldSnapshotNode();
                    node.load(iff);

                   snapshot.nodeList.add(node);
                }
            }

            iff.exitForm(TAG_NODS);

            iff.enterForm(TAG_OTNL);
            {
                final int totalTemplateNames = iff.readInt();

                for (int i = 0; i < totalTemplateNames; ++i) {
                    final String objectTemplateName = iff.readString();
                    final int crc = Crc.calculate(objectTemplateName);

                    snapshot.objectTemplateNames.add(objectTemplateName);
                    snapshot.objectTemplateCrcToNameIndexMap.put(crc, i);
                }
            }
            iff.exitForm(TAG_OTNL);
        }
        iff.exitForm(TAG_0001);

        return snapshot;
    }
}
