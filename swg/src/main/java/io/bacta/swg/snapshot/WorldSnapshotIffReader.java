package io.bacta.swg.snapshot;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.swg.foundation.Crc;
import io.bacta.swg.foundation.Tag;
import io.bacta.swg.iff.Iff;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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

    private final List<WorldSnapshotNode> nodeList = new ArrayList<>();
    private final List<String> objectTemplateNames = new ArrayList<>();
    private final TIntIntMap objectTemplateCrcToNameIndexMap = new TIntIntHashMap();
    private final TLongObjectMap<WorldSnapshotNode> networkIdNodeMap = new TLongObjectHashMap<>();

    public void clear() {
        nodeList.clear();
        objectTemplateNames.clear();
        objectTemplateCrcToNameIndexMap.clear();
        networkIdNodeMap.clear();
    }

    public void load(final Iff iff) {
        iff.enterForm(TAG_WSNP);
        {
            final int version = iff.getCurrentName();

            if (version == TAG_0001) {
                loadVersion0001(iff);
            } else {
                LOGGER.warn("World snapshot file with version {} unsupported.", Tag.convertTagToString(version));
            }
        }
        iff.exitForm(TAG_WSNP);
    }

    private void loadVersion0001(final Iff iff) {
        iff.enterForm(TAG_0001);
        {
            iff.enterForm(TAG_NODS);
            {
                while (iff.getNumberOfBlocksLeft() != 0) {
                    final WorldSnapshotNode node = new WorldSnapshotNode();
                    node.load(iff);

                    nodeList.add(node);
                }
            }

            iff.exitForm(TAG_NODS);

            iff.enterForm(TAG_OTNL);
            {
                final int totalTemplateNames = iff.readInt();

                for (int i = 0; i < totalTemplateNames; ++i) {
                    final String objectTemplateName = iff.readString();
                    final int crc = Crc.calculate(objectTemplateName);

                    objectTemplateNames.add(objectTemplateName);
                    objectTemplateCrcToNameIndexMap.put(crc, i);
                }
            }
            iff.exitForm(TAG_OTNL);
        }
        iff.exitForm(TAG_0001);
    }
}
