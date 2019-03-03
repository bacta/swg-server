package io.bacta.game.service.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import io.bacta.engine.utils.ReflectionUtil;
import io.bacta.shared.container.*;
import io.bacta.shared.object.GameObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 5/3/2016.
 */
@Slf4j
@Service
public class SlottedContainerService {
    private static final Field slotMapField = ReflectionUtil.getFieldOrNull(SlottedContainer.class, "slotMap");

    private final ContainerService containerService;
    private final SlotIdManager slotIdManager;

    @Inject
    public SlottedContainerService(ContainerService containerService, SlotIdManager slotIdManager) {
        this.containerService = containerService;
        this.slotIdManager = slotIdManager;
    }

    public boolean isContentItemObservedWith(final SlottedContainer container,
                                             final GameObject item) {
        //-- Rule 1: if base container claims that the item is visible with the container,
        //   we stick with that.  This prevents us from changing any existing behavior at
        //   the time this code is written.
        final boolean observedWithBaseContainer = containerService.isContentItemObservedWith(container, item);

        if (observedWithBaseContainer)
            return true;

        //-- Rule 2: if the item is in this container, check if any of the current arrangement's
        //   slots have the observeWithParent attribute set.  If so, return true, if not, return false.
        final ContainedByProperty containedByProperty = item.getContainedByProperty();

        if (containedByProperty == null)
            return false;

        final SlottedContainmentProperty slottedContainmentProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainmentProperty == null)
            return false;

        int arrangementIndex = slottedContainmentProperty.getCurrentArrangement();

        // Note: when checking if item is in container, we must also check
        // that contained item's arrangement is set to a valid arrangement.
        // This function can be called during container transfers prior to
        // the arrangementIndex being set.  When this occurs, handle this
        // case as if the item is not in the container because it's not really
        // there in its entirety yet.

        final GameObject containedByObject = containedByProperty.getContainedBy();
        final boolean isInThisContainer = containedByObject == container.getOwner() && arrangementIndex >= 0;

        if (isInThisContainer) {
            final TIntList slots = slottedContainmentProperty.getSlotArrangement(arrangementIndex);

            for (int i = 0; i < slots.size(); ++i) {
                final boolean observeWithParent = slotIdManager.getSlotObserveWithParent(slots.get(i));

                if (observeWithParent)
                    return true;
            }

            return false;
        }

        //-- Rule 3: if the item is not in this container, determine which arrangement it would
        //   use if it went in this slot.  If no arrangement is valid, return false.  If an arrangement
        //   is valid, check each slot in the arrangement.  If any slot has observeWithParent set true,
        //   return true; otherwise, return false.
        final ContainerResult containerResult = new ContainerResult();
        arrangementIndex = getFirstUnoccupiedArrangement(container, item, containerResult);

        if (arrangementIndex == -1 || containerResult.getError() != ContainerErrorCode.SUCCESS)
            return false;

        final TIntList slots = slottedContainmentProperty.getSlotArrangement(arrangementIndex);

        for (int i = 0; i < slots.size(); ++i) {
            final boolean observeWithParent = slotIdManager.getSlotObserveWithParent(slots.get(i));

            if (observeWithParent)
                return true;
        }

        return false;
    }

    public boolean isContentItemExposedWith(final SlottedContainer container,
                                            final GameObject item) {
        //-- Rule 1: if base container claims that the item is visible with the container,
        //   we stick with that.  This prevents us from changing any existing behavior at
        //   the time this code is written.
        final boolean exposedWithBaseContainer = containerService.isContentItemExposedWith(container, item);

        if (exposedWithBaseContainer)
            return true;

        //-- Rule 2: if the item is in this container, check if any of the current arrangement's
        //   slots have the exposeWithParent attribute set.  If so, return true, if not, return false.
        final ContainedByProperty containedByProperty = item.getContainedByProperty();

        if (containedByProperty == null)
            return false;

        final SlottedContainmentProperty slottedContainmentProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainmentProperty == null)
            return false;

        int arrangementIndex = slottedContainmentProperty.getCurrentArrangement();

        // Note: when checking if item is in container, we must also check
        // that contained item's arrangement is set to a valid arrangement.
        // This function can be called during container transfers prior to
        // the arrangementIndex being set.  When this occurs, handle this
        // case as if the item is not in the container because it's not really
        // there in its entirety yet.

        final GameObject containedByObject = containedByProperty.getContainedBy();
        final boolean isInThisContainer = (containedByObject == container.getOwner()) && (arrangementIndex >= 0);

        if (isInThisContainer) {
            final TIntList slots = slottedContainmentProperty.getSlotArrangement(arrangementIndex);

            for (int i = 0; i < slots.size(); ++i) {
                final boolean exposeWithParent = slotIdManager.getSlotExposeWithParent(slots.get(i));
                if (exposeWithParent)
                    return true;
            }

            return false;
        }

        //-- Rule 3: if the item is not in this container, determine which arrangement it would
        //   use if it went in this slot.  If no arrangement is valid, return false.  If an arrangement
        //   is valid, check each slot in the arrangement.  If any slot has exposeWithParent set true,
        //   return true; otherwise, return false.
        final ContainerResult containerResult = new ContainerResult();
        arrangementIndex = getFirstUnoccupiedArrangement(container, item, containerResult);

        if (arrangementIndex == -1 || (containerResult.getError() != ContainerErrorCode.SUCCESS))
            return false;

        final TIntList slots = slottedContainmentProperty.getSlotArrangement(arrangementIndex);

        for (int i = 0; i < slots.size(); ++i) {
            final boolean exposeWithParent = slotIdManager.getSlotExposeWithParent(slots.get(i));
            if (exposeWithParent)
                return true;
        }

        return false;
    }

    public boolean canContentsBeObservedWith(final SlottedContainer container) {
        // Content items can be observed with this container if any of our slots are marked for observe with parent.
        final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);

        for (int slotId : slotMap.keys()) {
            if (slotIdManager.getSlotObserveWithParent(slotId))
                return true;
        }

        return false;
    }

    public boolean add(final SlottedContainer container,
                       final GameObject item,
                       final int arrangementIndex,
                       final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (arrangementIndex < 0) {
            containerResult.setError(ContainerErrorCode.INVALID_ARRANGEMENT);
            return false;
        }

        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedProperty == null) {
            LOGGER.warn("Tried to add an item {} to slot container with no slotted property. Make sure its shared object template has a valid arrangement.",
                    item.getNetworkId());
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        if (!internalCheckSlottedAdd(container, item, arrangementIndex, containerResult))
            return false;

        int position = containerService.addToContents(container, item, containerResult);

        if (position < 0)
            return false;

        if (!internalDoSlottedAdd(container, item, position, arrangementIndex)) {
            LOGGER.warn("Internal check add worked, but trying to do it failed.");
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        slottedProperty.setCurrentArrangement(arrangementIndex);

        return false;
    }

    public boolean addToSlot(final SlottedContainer container,
                             final GameObject item,
                             final int slotId,
                             final ContainerResult containerResult) {

        containerResult.setError(ContainerErrorCode.SUCCESS);

        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedProperty == null) {
            LOGGER.warn("Tried to add an item {} to a slot container with no slotted property. Make sure its shared object template has a valid arrangement.",
                    item.getNetworkId());
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        return add(container, item, slottedProperty.getBestArrangementForSlot(slotId), containerResult);
    }

    private boolean internalCheckSlottedAdd(final SlottedContainer container,
                                            final GameObject item,
                                            final int arrangementIndex,
                                            final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (arrangementIndex < 0) {
            containerResult.setError(ContainerErrorCode.INVALID_ARRANGEMENT);
            return false;
        }

        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedProperty == null) {
            LOGGER.warn("Tried to add an item {} to a slot container with no slotted property. Make sure its shared object template has a valid arrangement.",
                    item.getNetworkId());
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        if (!mayAdd(container, item, arrangementIndex, containerResult))
            return false;

        return true;
    }

    private boolean internalDoSlottedAdd(final SlottedContainer container,
                                         final GameObject item,
                                         final int position,
                                         final int arrangementIndex) {
        if (arrangementIndex < 0)
            return false;

        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());
        final int numSlots = slottedProperty.getNumberOfSlots(arrangementIndex);
        final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);

        for (int i = 0; i < numSlots; ++i) {
            final int slotId = slottedProperty.getSlotId(arrangementIndex, i);

            if (!slotMap.containsKey(slotId)) {
                LOGGER.error("We're trying to add an item to a slotted container that does not have a slot that this item {} with this arrangement {} can go into or has something in it already.",
                        item.getNetworkId(),
                        arrangementIndex);
            } else {
                slotMap.put(slotId, position);
            }
        }

        return true;
    }

    private int find(final SlottedContainer container, final int slotId) {
        final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);

        if (slotMap.containsKey(slotId))
            return slotMap.get(slotId);

        LOGGER.warn("Tried to find from an invalid slot on this container {}. Check the container's slot descriptor file to make sure it has slot {}.",
                container.getOwner().getNetworkId(),
                slotIdManager.getSlotName(slotId));
        return -1;
    }

    public GameObject getObjectInSlot(final SlottedContainer container,
                                      final int slotId,
                                      final ContainerResult containerResult) {

        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (!hasSlot(container, slotId)) {
            containerResult.setError(ContainerErrorCode.NO_SLOT);
            return null;
        }

        final int position = find(container, slotId);

        if (position < 0) {
            containerResult.setError(ContainerErrorCode.NOT_FOUND);
            return null;
        }

        return containerService.getContents(container, position);
    }

    public List<GameObject> getObjectsForCombatBone(final SlottedContainer container,
                                                    final int bone) {
        final TIntList slots = slotIdManager.findSlotIdsForCombatBone(bone);
        final List<GameObject> objects = new ArrayList<>(slots.size()); //Can't be more bones than slots.

        final ContainerResult containerResult = new ContainerResult();

        for (int i = 0, size = slots.size(); i < size; ++i) {
            final GameObject item = getObjectInSlot(container, i, containerResult);

            if (item != null)
                objects.add(item);
        }

        return objects;
    }

    public int getFirstUnoccupiedArrangement(final SlottedContainer container,
                                             final GameObject item,
                                             final ContainerResult containerResult) {

        containerResult.setError(ContainerErrorCode.SUCCESS);

        final TIntList validArrangements = getValidArrangements(container, item, containerResult, true, true);

        if (validArrangements.isEmpty())
            return -1;

        return validArrangements.get(0);
    }

    public TIntList getValidArrangements(final SlottedContainer container, final GameObject item, final ContainerResult containerResult) {
        return getValidArrangements(container, item, containerResult, false, false);
    }

    public TIntList getValidArrangements(final SlottedContainer container, final GameObject item, final ContainerResult containerResult, final boolean returnOnFirst) {
        return getValidArrangements(container, item, containerResult, returnOnFirst, false);
    }

    public TIntList getValidArrangements(final SlottedContainer container,
                                         final GameObject item,
                                         final ContainerResult containerResult,
                                         final boolean returnOnFirst,
                                         final boolean unoccupiedArrangementsOnly) {

        containerResult.setError(ContainerErrorCode.SUCCESS);

        final TIntList returnList = new TIntArrayList();

        final SlottedContainmentProperty slottedContainment = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainment == null) {
            containerResult.setError(ContainerErrorCode.WRONG_TYPE);
            return returnList;
        }

        final int numArrangements = slottedContainment.getNumberOfArrangements();

        for (int i = 0; i < numArrangements; ++i) {
            final int numSlots = slottedContainment.getNumberOfSlots(i);
            int availableSlots = 0;
            boolean slotPresent = true;

            for (int j = 0; j < numSlots; ++j) {
                final int slot = slottedContainment.getSlotId(i, j);

                if (!hasSlot(container, slot)) {
                    slotPresent = false;
                    break;
                }

                if (unoccupiedArrangementsOnly && isSlotEmpty(container, slot, containerResult))
                    ++availableSlots;
            }

            if (slotPresent) {
                if (!unoccupiedArrangementsOnly || (availableSlots == numSlots)) {
                    returnList.add(i);

                    if (returnOnFirst)
                        return returnList;
                }
            }
        }

        containerResult.setError(ContainerErrorCode.SLOT_OCCUPIED);
        return returnList;
    }

    public boolean hasSlot(final SlottedContainer container, final int slotId) {
        final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);
        return slotMap.containsKey(slotId);
    }

    public TIntList getSlotIdList(final SlottedContainer container) {
        final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);
        return new TIntArrayList(slotMap.keySet());
    }

    public boolean isSlotEmpty(final SlottedContainer container, final int slotId, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (!hasSlot(container, slotId)) {
            containerResult.setError(ContainerErrorCode.NO_SLOT);
            return false;
        }

        final boolean empty = find(container, slotId) == -1;

        if (!empty)
            containerResult.setError(ContainerErrorCode.SLOT_OCCUPIED);

        return empty;
    }

    public boolean mayAdd(final SlottedContainer container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final TIntList validArrangements = getValidArrangements(container, item, containerResult);

        for (int i = 0, size = validArrangements.size(); i < size; ++i) {
            if (mayAdd(container, item, validArrangements.get(i), containerResult))
                return true;
        }

        return false;
    }

    public boolean mayAdd(final SlottedContainer container, final GameObject item, final int arrangementIndex, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (arrangementIndex < 0) {
            containerResult.setError(ContainerErrorCode.INVALID_ARRANGEMENT);
            return false;
        }

        if (item == container.getOwner()) {
            containerResult.setError(ContainerErrorCode.ADD_SELF);
            return false;
        }

        final SlottedContainmentProperty slottedContainment = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainment == null) {
            LOGGER.warn("Tried to check slots with an item with no slotted containment property.");
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        final int numSlots = slottedContainment.getNumberOfSlots(arrangementIndex);

        for (int i = 0; i < numSlots; ++i) {
            final int slotId = slottedContainment.getSlotId(arrangementIndex, i);

            if (!isSlotEmpty(container, slotId, containerResult))
                return false;
        }

        return containerService.mayAdd(container, item, containerResult);
    }

    public boolean mayAddToSlot(final SlottedContainer container, final GameObject item, final int slotId, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (!isSlotEmpty(container, slotId, containerResult))
            return false;

        final SlottedContainmentProperty slottedContainment = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainment == null) {
            LOGGER.warn("Tried to check slots with an item with no slotted containment property.");
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        return mayAdd(container, item, slottedContainment.getBestArrangementForSlot(slotId), containerResult);
    }

    public boolean remove(final SlottedContainer container, final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedProperty == null) {
            LOGGER.warn("Tried to remove an item {} to a slot container with no slotted property.",
                    item.getNetworkId());
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        //Check for the item's position in contents.
        final int position = containerService.find(container, item, containerResult);

        if (position == -1) {
            LOGGER.debug("Called with item {} from container {}, but the item was not found int he base container's contents. This means the item was not in the container.",
                    item.getNetworkId(),
                    container.getOwner().getNetworkId());
            containerResult.setError(ContainerErrorCode.NOT_FOUND);
            return false;
        }

        //try to remove it from contents.

        if (!internalRemove(container, item, -1)) {
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        if (!containerService.remove(container, item, containerResult))
            return false;

        slottedProperty.setCurrentArrangement(-1);
        return true;
    }

    public boolean remove(final SlottedContainer container, final int position, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final GameObject object = containerService.getContents(container, position);

        if (object != null)
            return remove(container, object, containerResult);

        containerResult.setError(ContainerErrorCode.UNKNOWN);
        return false;
    }

    public void removeItemFromSlotOnly(final SlottedContainer container, final GameObject item) {
        internalRemove(container, item, -1);
    }

    public int findFirstSlotIdForObject(final SlottedContainer container, final GameObject item) {
        final ContainerResult containerResult = new ContainerResult();
        final int position = containerService.find(container, item, containerResult);

        if (position >= 0) {
            final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);
            final int[] keys = slotMap.keys();

            for (int i = 0, size = keys.length; i < size; ++i) {
                final int objPosition = slotMap.get(keys[i]);

                if (objPosition == position)
                    return keys[i];
            }
        }

        return SlotId.INVALID;
    }

    public void updateArrangement(final SlottedContainer container, final GameObject item, final int oldArrangement, final int newArrangement) {
        final ContainerResult containerResult = new ContainerResult();
        if (!internalRemove(container, item, oldArrangement)) {
            LOGGER.debug("Remove part of update failed in slotted container");
        }

        final int position = containerService.find(container, item, containerResult);

        if (position < 0) {
            LOGGER.warn("could not find object in update slotted container");
        } else if (newArrangement >= 0 && !internalDoSlottedAdd(container, item, position, newArrangement)) {
            LOGGER.warn("Updating slotted contained failed.");
        }
    }

    private boolean internalRemove(final SlottedContainer container, final GameObject item, final int overrideArrangement) {
        final ContainerResult containerResult = new ContainerResult();

        final int position = containerService.find(container, item, containerResult);

        if (position == -1) {
            LOGGER.warn("called with an invalid item: container owner id={}, template={}; item id={}, template={}",
                    container.getOwner().getNetworkId(),
                    container.getOwner().getObjectTemplateName(),
                    item.getNetworkId(),
                    item.getObjectTemplateName());
            return false;
        }

        final TIntIntMap slotMap = ReflectionUtil.getFieldValue(slotMapField, container);
        final int[] keys = slotMap.keys();

        for (int i = 0, size = keys.length; i < size; ++i) {
            final int pos = slotMap.get(keys[i]);

            if (pos == position)
                slotMap.put(i, -1); //Pretty sure we should be able to do this more efficiently.
        }

        return true;
    }

}
