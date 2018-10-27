package io.bacta.swg.snapshot;

import io.bacta.swg.foundation.Tag;
import io.bacta.swg.iff.Iff;
import io.bacta.swg.math.Quaternion;
import io.bacta.swg.math.Transform;
import io.bacta.swg.math.Vector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.bacta.swg.foundation.Tag.TAG_0000;
import static io.bacta.swg.foundation.Tag.TAG_DATA;
import static io.bacta.swg.snapshot.WorldSnapshotIffReader.TAG_NODE;

/**
 * Represents a node in the world snapshot. A node may have a parent, and a number of child nodes.
 */
@Slf4j
@Getter
public final class WorldSnapshotNode {
    private long networkId;
    private long containerId;
    private int templateNameIndex;
    private int cellIndex;
    /**
     * The object's transform in relation to its parent.
     * If there is no parent, then it will reflect its position in the world.
     */
    private Transform transform;
    private float radius;
    private int portalLayoutCrc;

    private WorldSnapshotNode parent;
    private List<WorldSnapshotNode> children;

    private boolean deleted;

    public void load(final Iff iff) {
        iff.enterForm(TAG_NODE);
        {
            final int version = iff.getCurrentName();

            if (version == TAG_0000) {
                loadVersion0000(iff);
            } else {
                LOGGER.warn("Unsupported snapshot node version {}.", Tag.convertTagToString(version));
            }
        }
        iff.exitForm(TAG_NODE);
    }

    private void loadVersion0000(final Iff iff) {
        iff.enterForm(TAG_0000);
        {
            //Read the node's data.
            iff.enterChunk(TAG_DATA);
            {
                networkId = iff.readInt();
                containerId = iff.readInt();
                templateNameIndex = iff.readInt();
                cellIndex = iff.readInt();

                final Quaternion quaternion = new Quaternion(iff);
                final Vector vector = new Vector(iff);

                final Transform localTransform = new Transform();
                quaternion.getTransform(localTransform);
                localTransform.setPositionInParentSpace(vector);

                transform = localTransform;
                radius = iff.readFloat();
                portalLayoutCrc = iff.readInt();
            }
            iff.exitChunk(TAG_DATA);

            //Read any children for the node.
            final List<WorldSnapshotNode> children = new ArrayList<>();

            while (iff.getNumberOfBlocksLeft() > 0) {
                final WorldSnapshotNode node = new WorldSnapshotNode();
                node.load(iff);
                node.parent = this;

                children.add(node);
            }

            if (children.size() > 0) {
                this.children = children;
            } else {
                this.children = Collections.emptyList();
            }
        }
        iff.exitForm(TAG_0000);
    }
}
