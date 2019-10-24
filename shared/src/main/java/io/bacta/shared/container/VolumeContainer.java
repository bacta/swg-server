package io.bacta.shared.container;

import io.bacta.shared.object.GameObject;
import lombok.Getter;

/**
 * Created by crush on 8/26/2014.
 * <p>
 * A volume container is a container that holds items, limited to a maximum volume. It represents things like chests,
 * bags, etc.
 */
public class VolumeContainer extends Container {
    public static final int NO_VOLUME_LIMIT = -1;

    public static int getClassPropertyId() {
        return 0xA5193F23;
    }

    @Getter
    private int currentVolume;
    @Getter
    private int totalVolume;

    public VolumeContainer(final GameObject owner, int totalVolume) {
        super(getClassPropertyId(), owner);

        this.currentVolume = 0;
        this.totalVolume = totalVolume;
    }

    public boolean add(final GameObject item) throws ContainerTransferException {
        return add(item, false);
    }

    public boolean add(final GameObject item, final boolean allowOverload) throws ContainerTransferException {
        final int startingVolume = currentVolume;

        final VolumeContainmentProperty containmentProperty = item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (containmentProperty == null)
            throw containerException(item, ContainerErrorCode.UNKNOWN);

        if (!allowOverload && !hasAvailableVolume(containmentProperty))
            throw containerException(item, ContainerErrorCode.FULL);

        final int position = addToContents(item);

        if (position < 0)
            return false;

        if (totalVolume != NO_VOLUME_LIMIT)
            currentVolume += containmentProperty.getVolume();

        if (currentVolume != startingVolume) {
            final VolumeContainer parentContainer = getVolumeContainerParent(this);

            if (parentContainer != null)
                parentContainer.childVolumeChanged(currentVolume - startingVolume, true);
        }

        return true;
    }

    public boolean remove(final GameObject item) throws ContainerTransferException {
        final int startingVolume = currentVolume;

        final VolumeContainmentProperty containmentProperty = item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (containmentProperty == null)
            throw containerException(item, ContainerErrorCode.UNKNOWN);

        boolean success = super.remove(item);

        if (!success)
            return false;

        if (totalVolume != NO_VOLUME_LIMIT)
            currentVolume = currentVolume - containmentProperty.getVolume();

        if (currentVolume != startingVolume) {
            final VolumeContainer parentContainer = getVolumeContainerParent(this);

            if (parentContainer != null)
                parentContainer.childVolumeChanged(currentVolume - startingVolume, true);
        }

        return true;
    }

    public boolean hasAvailableVolume(final VolumeContainmentProperty containmentProperty) {
        return hasAvailableVolume(containmentProperty.getVolume());
    }

    public boolean hasAvailableVolume(int addedVolume) {
        if (totalVolume == NO_VOLUME_LIMIT)
            return true;

        boolean hasVolume = (currentVolume + addedVolume) <= totalVolume;

        if (hasVolume) {
            final VolumeContainer parentContainer = getVolumeContainerParent(this);

            if (parentContainer != null)
                hasVolume = parentContainer.hasAvailableVolume(addedVolume);
        }

        return hasVolume;
    }

    public int recalculateVolume() {
        int volume = 0;

        if (totalVolume != NO_VOLUME_LIMIT) {
            for (int i = 0; i < count(); ++i) {
                final GameObject object = get(i);
                final VolumeContainmentProperty containmentProperty = object.getProperty(VolumeContainmentProperty.getClassPropertyId());

                volume += containmentProperty.getVolume();
            }
        }

        currentVolume = volume;

        final VolumeContainer parentContainer = getVolumeContainerParent(this);

        if (parentContainer != null)
            parentContainer.recalculateVolume();

        return currentVolume;
    }

    private void childVolumeChanged(final int volume, final boolean updateParent) {
        if (totalVolume != NO_VOLUME_LIMIT)
            currentVolume += volume;

        if (updateParent) {
            final VolumeContainer parentContainer = getVolumeContainerParent(this);

            if (parentContainer != null)
                parentContainer.childVolumeChanged(volume, true);
        }
    }

    public static VolumeContainer getVolumeContainerParent(final VolumeContainer container) {
        final GameObject owner = container.getOwner();
        final ContainedByProperty containedBy = owner.getContainedByProperty();

        if (containedBy != null) {
            final GameObject parent = containedBy.getContainedBy();

            if (parent != null)
                return parent.getVolumeContainerProperty();
        }

        return null;
    }
}
