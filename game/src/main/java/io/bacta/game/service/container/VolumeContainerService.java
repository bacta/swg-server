package io.bacta.game.service.container;

import io.bacta.engine.utils.ReflectionUtil;
import io.bacta.game.object.ServerObject;
import io.bacta.shared.container.*;
import io.bacta.shared.object.GameObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Created by crush on 5/3/2016.
 */
@Slf4j
@Service
public class VolumeContainerService {
    private static final Field currentVolumeField = ReflectionUtil.getFieldOrNull(VolumeContainer.class, "currentVolume");
    private static final Field totalVolumeField = ReflectionUtil.getFieldOrNull(VolumeContainer.class, "totalVolume");

    private final ContainerService containerService;

    @Inject
    public VolumeContainerService(final ContainerService containerService) {
        this.containerService = containerService;
    }

    public boolean add(final VolumeContainer container, final GameObject item, final ContainerResult containerResult) {
        return add(container, item, containerResult, false);
    }

    public boolean add(final VolumeContainer container, final GameObject item, final ContainerResult containerResult, boolean allowOverloaded) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final int oldVolume = container.getCurrentVolume();

        final VolumeContainmentProperty property = item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (property == null) {
            LOGGER.warn("Cannot add an item to a volume container without a containment property.");
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        if (!allowOverloaded && !checkVolume(container, property)) {
            containerResult.setError(ContainerErrorCode.FULL);
            return false;
        }

        if (containerService.addToContents(container, item, containerResult) == -1)
            return false;

        insertNewItem(container, item, property);

        final int currentVolume = container.getCurrentVolume();

        if (currentVolume != oldVolume) {
            final VolumeContainer parent = getVolumeContainerParent(container);

            if (parent != null)
                childVolumeChanged(parent, currentVolume - oldVolume, true);
        }

        return true;
    }

    private boolean checkVolume(final VolumeContainer container, final VolumeContainmentProperty property) {
        return checkVolume(container, property.getVolume());
    }

    public boolean checkVolume(final VolumeContainer container, final int addedVolume) {
        final int totalVolume = container.getTotalVolume();

        if (totalVolume == VolumeContainer.NO_VOLUME_LIMIT)
            return true;

        final int currentVolume = container.getCurrentVolume();

        boolean returnValue = (currentVolume + addedVolume <= totalVolume);

        if (returnValue) {
            final VolumeContainer parent = getVolumeContainerParent(container);

            if (parent != null)
                returnValue = checkVolume(parent, addedVolume);
        }

        return returnValue;
    }

    public boolean mayAdd(final VolumeContainer container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (item == container.getOwner()) {
            containerResult.setError(ContainerErrorCode.ADD_SELF);
            return false;
        }

        final VolumeContainmentProperty property = item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (property == null) {
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        final VolumeContainer itemVolumeContainer = item.getVolumeContainerProperty();

        if (itemVolumeContainer != null) {
            final int totalVolume = container.getTotalVolume();

            if (totalVolume != VolumeContainer.NO_VOLUME_LIMIT && itemVolumeContainer.getTotalVolume() >= totalVolume) {
                //Some BS about moving a holocron into another one...we aren't doing that.
                containerResult.setError(ContainerErrorCode.TOO_LARGE);
                return false;
            }
        }

        if (!checkVolume(container, property)) {
            containerResult.setError(ContainerErrorCode.FULL);
            return false;
        }

        return containerService.mayAdd(container, item, containerResult);
    }

    public int recalculateVolume(final VolumeContainer container) {
        int volume = 0;
        final Iterator<GameObject> contentsIterator = container.iterator();

        while (contentsIterator.hasNext()) {
            final ServerObject obj = (ServerObject) contentsIterator.next();

            if (obj == null) {
                LOGGER.warn("Container with non-existent object");
            } else {
                final VolumeContainmentProperty property = obj.getProperty(VolumeContainmentProperty.getClassPropertyId());

                if (property == null) {
                    LOGGER.warn("We have an item in a volume container with no property {}", obj.getNetworkId());
                } else if (container.getTotalVolume() != VolumeContainer.NO_VOLUME_LIMIT) {
                    volume += property.getVolume();
                }
            }
        }

        ReflectionUtil.setFieldValue(currentVolumeField, container, volume);

        //If our volume is recalculated, we must inform our parent if contained by a volume container.
        final VolumeContainer parent = getVolumeContainerParent(container);

        if (parent != null)
            recalculateVolume(parent);

        return container.getCurrentVolume();
    }

    public boolean remove(final VolumeContainer container, final GameObject item, final ContainerResult containerResult) {
        final int oldVolume = container.getCurrentVolume();

        containerResult.setError(ContainerErrorCode.SUCCESS);

        final VolumeContainmentProperty prop = item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (prop == null) {
            LOGGER.warn("Cannot remove an item from a volume container without a containment property.");
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        boolean returnValue = containerService.remove(container, item, containerResult);

        if (!returnValue) {
            return false;
        }

        if (!internalRemove(container, item, prop)) {
            LOGGER.warn("Tried to remove item {} from volume container in internal routine but failed.", item.getNetworkId());
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        //if volume has changed, update parent volume count.
        if (container.getCurrentVolume() != oldVolume) {
            final VolumeContainer parent = getVolumeContainerParent(container);

            if (parent != null)
                childVolumeChanged(parent, (container.getCurrentVolume() - oldVolume), true);
        }

        return true;
    }

    private boolean internalRemove(final VolumeContainer container, final GameObject item, final VolumeContainmentProperty property) {
        final VolumeContainmentProperty prop = property != null ? property : item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (prop == null) {
            LOGGER.warn("ITem {} has no volume property.", item.getNetworkId());
            return false;
        }

        if (container.getTotalVolume() != VolumeContainer.NO_VOLUME_LIMIT)
            ReflectionUtil.setFieldValue(currentVolumeField, container, container.getCurrentVolume() - prop.getVolume());

        return true;
    }

    private void insertNewItem(final VolumeContainer container, final GameObject item, final VolumeContainmentProperty property) {
        final VolumeContainmentProperty prop = property != null ? property : item.getProperty(VolumeContainmentProperty.getClassPropertyId());

        if (prop == null) {
            LOGGER.warn("Item {} has no volume property.", item.getNetworkId());
            return;
        }

        final int totalVolume = ReflectionUtil.getFieldValue(totalVolumeField, container);
        final int currentVolume = ReflectionUtil.getFieldValue(currentVolumeField, container);

        if (totalVolume != VolumeContainer.NO_VOLUME_LIMIT)
            ReflectionUtil.setFieldValue(currentVolumeField, container, currentVolume + prop.getVolume());
    }

    private void childVolumeChanged(final VolumeContainer container, final int volume, final boolean updateParent) {
        final int totalVolume = ReflectionUtil.getFieldValue(totalVolumeField, container);
        final int currentVolume = ReflectionUtil.getFieldValue(currentVolumeField, container);

        if (totalVolume != VolumeContainer.NO_VOLUME_LIMIT)
            ReflectionUtil.setFieldValue(currentVolumeField, container, currentVolume + volume);

        if (updateParent) {
            final VolumeContainer parent = getVolumeContainerParent(container);

            if (parent != null)
                childVolumeChanged(parent, volume, true);
        }
    }

    private VolumeContainer getVolumeContainerParent(final VolumeContainer container) {
        final GameObject owner = container.getOwner();
        final ContainedByProperty containedByProperty = owner.getContainedByProperty();

        if (containedByProperty != null) {
            final GameObject parent = containedByProperty.getContainedBy();

            if (parent != null)
                return parent.getVolumeContainerProperty();
        }

        return null;
    }

}
