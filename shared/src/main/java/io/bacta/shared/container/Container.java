package io.bacta.shared.container;

import io.bacta.shared.object.GameObject;
import io.bacta.shared.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by crush on 8/26/2014.
 */
public abstract class Container extends Property {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Container.class);

    public static int getClassPropertyId() {
        return 0x55DC5726;
    }

    private final List<GameObject> contents = new ArrayList<>();
    private boolean changed;

    public Container(final int propertyId, final GameObject owner) {
        super(propertyId, owner);
    }

    public Iterator<GameObject> iterator() {
        return contents.iterator();
    }

    public boolean hasChanged() {
        return changed;
    }

    public void clearChanged() {
        changed = false;
    }

    public GameObject get(int position) {
        return contents.get(position);
    }

    /**
     * Finds the index of an item in this container.
     *
     * @param networkId The id of the item for which to search.
     * @return The index of the item if it is found. Otherwise, -1.
     */
    public int find(final long networkId) {
        for (int i = 0; i < contents.size(); ++i) {
            final GameObject item = contents.get(i);

            if (item.getNetworkId() == networkId)
                return i;
        }

        return -1;
    }

    public boolean isContentItemObservedWith(final GameObject item) {
        return false;
    }

    public boolean isContentItemExposedWith(final GameObject item) {
        return false;
    }

    public boolean canContentsByObservedWith() {
        return false;
    }

    public int count() {
        return contents.size();
    }

    public boolean remove(final GameObject item) throws
            ContainerTransferException {

        final ContainedByProperty containedByProperty = item.getContainedByProperty();

        if (containedByProperty != null) {
            final GameObject containedBy = containedByProperty.getContainedBy();

            if (containedBy != getOwner())
                throw containerException(item, ContainerErrorCode.NOT_FOUND);

            final boolean success = contents.remove(item);

            if (success)
                containedByProperty.setContainedBy(null);

            return success;
        }

        throw containerException(item, ContainerErrorCode.UNKNOWN);
    }

    protected int addToContents(final GameObject item) throws
            ContainerTransferException {

        if (item.getNetworkId() == getOwner().getNetworkId())
            throw containerException(item, ContainerErrorCode.ADD_SELF);

        final ContainedByProperty containedByProperty = item.getContainedByProperty();

        if (containedByProperty == null)
            throw containerException(item, ContainerErrorCode.UNKNOWN);

        final GameObject containedBy = containedByProperty.getContainedBy();

        //Is the item in another container still? It must be removed first, but we don't do that here.
        if (containedBy != null && containedBy != getOwner())
            throw containerException(item, ContainerErrorCode.ALREADY_IN);

        //TODO: container loop check

        final int depth = ContainerUtil.getDepth(this);
        final int maxDepth = 6; //TODO: Config value...

        if (depth > maxDepth)
            throw containerException(item, ContainerErrorCode.TOO_DEEP);

        containedByProperty.setContainedBy(getOwner());
        contents.add(item);

        return contents.size() - 1; //Return the index we just added.
    }

    /**
     * Helper function for creating a container exception for this container. Reduces some boilerplate.
     *
     * @param item      The item that was being transferred to this container.
     * @param errorCode The error code that was produced.
     * @return A new exception to be thrown.
     */
    final ContainerTransferException containerException(final GameObject item, final ContainerErrorCode errorCode) {
        return new ContainerTransferException(
                getOwner().getNetworkId(),
                item.getNetworkId(),
                errorCode);
    }
}
