package io.bacta.game.container;

import io.bacta.game.object.ServerObject;
import io.bacta.game.player.VeteranRewardsService;
import io.bacta.shared.container.*;
import io.bacta.shared.foundation.CrcString;
import io.bacta.shared.math.Transform;
import io.bacta.shared.object.GameObject;
import io.bacta.shared.portal.CellProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;


/**
 * Created by crush on 4/28/2016.
 * <p>
 * All container transfers should go through this service rather than the containers methods themselves.
 */
@Slf4j
@Service
public final class ContainerTransferService {
    private final SlotIdManager slotIdManager;
    private final VeteranRewardsService veteranRewardService;

    @Inject
    public ContainerTransferService(
            final SlotIdManager slotIdManager,
            final VeteranRewardsService veteranRewardService) {
        this.slotIdManager = slotIdManager;
        this.veteranRewardService = veteranRewardService;
    }

    public boolean canTransferTo(final ServerObject destination, final ServerObject item, final ServerObject actor)
            throws ContainerTransferException {
        //skipping authoritative check

        //Cannot move a reward item that is in the middle of the trade in process.
        if (veteranRewardService.isTradeInProgress(item)) {
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.BLOCKED_BY_ITEM_BEING_TRANSFERRED);
        }

        //skipping over the lot limit check.

        //TODO: validate transfer script hook

        if (destination != null) {
            final Container container = destination.getContainerProperty();

            if (container == null) {
                LOGGER.debug("Destination {} is not a container.", destination.getNetworkId());

                throw new ContainerTransferException(
                        item.getNetworkId(),
                        destination.getNetworkId(),
                        ContainerErrorCode.NO_CONTAINER);
            }
        }

        return true;
    }

    public boolean canTransferToSlot(final ServerObject destination, final ServerObject item, final int slotId, final ServerObject actor)
            throws ContainerTransferException {
        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer == null) {
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.NO_CONTAINER);
        }

        if (veteranRewardService.isTradeInProgress(item)) {
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.BLOCKED_BY_ITEM_BEING_TRANSFERRED);
        }

        //TODO: check transfer script hook.

        return true;
    }

    public boolean transferItemToGeneralContainer(final ServerObject destination, final ServerObject item, final ServerObject actor)
            throws ContainerTransferException {
        return transferItemToGeneralContainer(destination, item, actor, false);
    }

    public boolean transferItemToGeneralContainer(final ServerObject destination, final ServerObject item, final ServerObject actor, final boolean allowOverloaded)
            throws ContainerTransferException {
        final VolumeContainer volumeContainer = destination.getVolumeContainerProperty();

        if (volumeContainer != null)
            return transferItemToVolumeContainer(destination, item, actor, allowOverloaded);

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer != null) {
            final int arrangementIndex = slottedContainer.getFirstUnoccupiedArrangement(item);

            if (arrangementIndex != -1)
                return transferItemToSlottedContainer(destination, item, actor, arrangementIndex);
        }

        return false;
    }

    public boolean transferItemToSlottedContainer(final ServerObject destination, final ServerObject item, final ServerObject actor, final int arrangementIndex)
            throws ContainerTransferException {

        if (arrangementIndex < 0) {
            LOGGER.warn("Invalid arrangement index in transferItemToSlottedContainer");
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.INVALID_ARRANGEMENT);
        }

        if (actor != null) {
            final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

            if (slottedProperty == null) {
                LOGGER.warn("Invalid item (no slottedContainmentProperty)");
                throw new ContainerTransferException(
                        item.getNetworkId(),
                        destination.getNetworkId(),
                        ContainerErrorCode.WRONG_TYPE);
            }

            if (!slottedProperty.canManipulateArrangement(slotIdManager, arrangementIndex)) {
                throw new ContainerTransferException(
                        item.getNetworkId(),
                        destination.getNetworkId(),
                        ContainerErrorCode.NO_PERMISSION);
            }
        }

        final SharedTransfer sharedTransfer = sharedTransferBegin(item);

        if (!sharedTransfer.result)
            return false;

        final ServerObject sourceObject = sharedTransfer.sourceObject;
        final Container sourceContainer = sharedTransfer.sourceContainer;

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer == null) {
            LOGGER.warn("This destination is not a slot container");
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.NO_CONTAINER);
        }

        //First checks if the container service will allow the transfer...
        if (!canTransferTo(destination, item, actor))
            return false;

        //Then checks if the Slotted Container Service will allow the transfer...
//        if (!slottedContainerService.mayAdd(slottedContainer, item, arrangementIndex))
//            return false;

        //Finally, attempts to make the transfer...
        if (!handleTransferSource(sourceContainer, item))
            return false;

        //Adds the actual items to the slotted container.
        //slottedContainerService.add(slottedContainer, item, arrangementIndex);
        slottedContainer.add(item, arrangementIndex);

        //item.onContainerTransferComplete(sourceObject, destination);
        //TODO: Post transfer script hook
        return true;
    }

    public boolean transferItemToSlottedContainerSlotId(final ServerObject destination, final ServerObject item, final ServerObject actor, final CrcString slotName)
            throws ContainerTransferException {
        final int slotId = slotIdManager.findSlotId(slotName);

        if (slotId == SlotId.INVALID) {
            LOGGER.warn("Invalid slot {}", slotName.getString());
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.NO_SLOT);
        }

        return transferItemToSlottedContainerSlotId(destination, item, actor, slotId);
    }

    public boolean transferItemToSlottedContainerSlotId(final ServerObject destination, final ServerObject item, final ServerObject actor, final int slotId)
            throws ContainerTransferException {
        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedProperty == null) {
            LOGGER.warn("Invalid item (no slottedContainmentProperty)");
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.NO_SLOT);
        }

        final int arrangementIndex = slottedProperty.getBestArrangementForSlot(slotId);
        return transferItemToSlottedContainer(destination, item, actor, arrangementIndex);
    }

    public boolean transferItemToUnknownContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final int arrangementIndex, final Transform transform)
            throws ContainerTransferException {
        final VolumeContainer volumeContainer = destination.getVolumeContainerProperty();

        if (volumeContainer != null)
            return transferItemToVolumeContainer(destination, item, transferer);

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer != null)
            return transferItemToSlottedContainer(destination, item, transferer, arrangementIndex);

        final CellProperty cellProperty = destination.getCellProperty();

        if (cellProperty != null)
            return transferItemToCell(destination, item, transferer, transform);

        throw new ContainerTransferException(
                item.getNetworkId(),
                destination.getNetworkId(),
                ContainerErrorCode.NO_CONTAINER);
    }

    public boolean transferItemToVolumeContainer(final ServerObject destination, final ServerObject item, final ServerObject actor)
            throws ContainerTransferException {
        return transferItemToVolumeContainer(destination, item, actor, false);
    }

    public boolean transferItemToVolumeContainer(final ServerObject destination,
                                                 final ServerObject item,
                                                 final ServerObject actor,
                                                 final boolean allowOverloaded)
            throws ContainerTransferException {

        final SharedTransfer sharedTransfer = sharedTransferBegin(item);

        if (!sharedTransfer.result)
            return false;

        final VolumeContainer volumeContainer = destination.getVolumeContainerProperty();

        if (volumeContainer == null) {
            LOGGER.warn("This destination is not a volume container.");
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.WRONG_TYPE);
        }


        if (item.isPlayerControlled()) {
            //Trying to put a player inside a volume container.
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.BLOCKED_BY_ITEM_BEING_TRANSFERRED);
        }


        //TODO: Revisit overloading
//        if (!canTransferTo(destination, item, actor)) {
//            if (allowOverloaded) {
//                if (!(containerResult.getError() == ContainerErrorCode.FULL || containerResult.getError() == ContainerErrorCode.TOO_LARGE))
//                    return false;
//            } else {
//                return false;
//            }
//        }

        if (!handleTransferSource(sharedTransfer.sourceContainer, item))
            return false;

        if (allowOverloaded)
            LOGGER.warn("May need to implement some hacks here to allowOverload when transfering to volume container.");

        final boolean success = volumeContainer.add(item, allowOverloaded);

        if (!success)
            LOGGER.warn("Checks to add an item succeeded, but the add failed.");

        //item.onContainerTransferComplete(sharedTransfer.sourceObject, destination);
        //TODO: Post transfer script hooks.
        return true;
    }

    public boolean transferItemToCell(final ServerObject destination, final ServerObject item, final ServerObject actor)
            throws ContainerTransferException {
        final Transform transform = destination.getTransformObjectToWorld().rotateTranslateLocalToParent(item.getTransformObjectToParent());
        return transferItemToCell(destination, item, actor, transform);
    }

    public boolean transferItemToCell(final ServerObject destination, final ServerObject item, final ServerObject actor, final Transform transform)
            throws ContainerTransferException {
        final SharedTransfer sharedTransfer = sharedTransferBegin(item);

        if (!sharedTransfer.result) {
            LOGGER.debug("Could not transfer from source to cell.");
            return false;
        }

        final CellProperty cellProperty = destination.getCellProperty();

        if (cellProperty == null) {
            LOGGER.warn("This destination is not a cell.");
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.NO_CONTAINER);
        }
//
//        if (!item.canDropInWorld()) {
//            containerResult.setError(ContainerErrorCode.WRONG_TYPE);
//            return false;
//        }

        if (!canTransferTo(destination, item, actor)) {
            LOGGER.debug("Could not transfer to dest cell.");
            return false;
        }

        if (!handleTransferSource(sharedTransfer.sourceContainer, item)) {
            LOGGER.debug("Could not transfer from source.");
            return false;
        }

        cellProperty.addObjectToWorld(item);
        item.setTransformObjectToParent(transform);

        //item.onContainerTransferComplete(sharedTransfer.sourceObject, destination);
        //TODO: Post transfer script hooks.
        return true;
    }

    public boolean transferItemToWorld(final ServerObject destination, final ServerObject item, final ServerObject transferer, final Transform transform)
            throws ContainerTransferException {
//        if (transferer != null) {
//            final SoeRequestContext client = transferer.getConnection();
//
//            if (client != null && !client.isGod()) {
//                LOGGER.debug("Player tried to drop something in world, but they are not a god.");
//                containerResult.setError(ContainerErrorCode.NO_PERMISSION);
//                return false;
//            }
//        }

//        if (!item.canDropInWorld()) {
//            containerResult.setError(ContainerErrorCode.WRONG_TYPE);
//            return false;
//        }

        if (!canTransferTo(null, item, transferer)) {
            return false;
        }

        final SharedTransfer sharedTransfer = sharedTransferBegin(item);

        if (!sharedTransfer.result) {
            LOGGER.debug("Could not transfer to world because source disallowed.");
            return false;
        }

        if (sharedTransfer.sourceContainer == null) {
            //Already in world
            LOGGER.debug("Failed transfer to world because it was already there.");
            throw new ContainerTransferException(
                    item.getNetworkId(),
                    destination.getNetworkId(),
                    ContainerErrorCode.UNKNOWN);
        }

        if (!handleTransferSource(sharedTransfer.sourceContainer, item)) {
            LOGGER.debug("Could not transfer to world because source disallowed.");
            return false;
        }

        item.setTransformObjectToParent(transform);

        //item.onContainerTransferComplete(sharedTransfer.sourceObject, null);
        //TODO: Post transfer script hook.
        return true;
    }

    public boolean canPlayerManipulateSlot(final int slotId) {
        return slotIdManager.isSlotPlayerModifiable(slotId);
    }

    public void sendContainerMessageToClient(final ServerObject player, final ContainerErrorCode errorCode) {
        sendContainerMessageToClient(player, errorCode, null);
    }

    public void sendContainerMessageToClient(final ServerObject player, final ContainerErrorCode errorCode, final ServerObject target) {
        final boolean sendMessage = errorCode != ContainerErrorCode.BLOCKED_BY_SCRIPT && errorCode != ContainerErrorCode.SILENT_ERROR;
//
//        if (sendMessage || (player.getConnection() != null && player.getConnection().isGod())) {
//            final String message;
//
//            if (target != null && (!target.getAssignedObjectName().isEmpty() || target.getNameStringId().isValid()))
//                message = String.format("container%02d_prose", errorCode.value);
//            else
//                message = String.format("container%02d", errorCode.value);
//
//            final StringId code = new StringId("container_error_message", message);
//
//            gameChatService.sendSystemMessageSimple(player, code, target);
//        }
    }

    private SharedTransfer sharedTransferBegin(final GameObject item) {
        final ContainedByProperty containedBy = item.getContainedByProperty();

        if (containedBy == null || containedBy.getContainedBy() == null) {
            //This item is not contained by anything!
            //This means it is in the world!
            //Return null for the source container, but succeed.
            return new SharedTransfer(null, null, true);
        }

        final ServerObject source = (ServerObject) containedBy.getContainedBy();

        if (source == null) {
            LOGGER.warn("This item's source was not found!");
            //containerResult.setError(ContainerErrorCode.NOT_FOUND);
            return new SharedTransfer(null, null, false);
        }

        final Container sourceContainer = source.getContainerProperty();

        if (sourceContainer == null) {
            //containerResult.setError(ContainerErrorCode.NO_CONTAINER);
            LOGGER.warn("This source contains stuff, but has no property!");
            return new SharedTransfer(source, null, false);
        }

        return new SharedTransfer(source, sourceContainer, true);
    }

    private boolean handleTransferSource(final Container source, final ServerObject item)
            throws ContainerTransferException {
        return source == null || source.remove(item);
    }

    public static ContainedByProperty getContainedByProperty(final GameObject obj) {
        return obj.getContainedByProperty();
    }


    @AllArgsConstructor
    private static final class SharedTransfer {
        public final ServerObject sourceObject;
        public final Container sourceContainer;
        public final boolean result;
    }
}