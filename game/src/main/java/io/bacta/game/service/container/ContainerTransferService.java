package io.bacta.game.service.container;

import io.bacta.game.chat.GameChatService;
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
    private final GameChatService gameChatService;
    private final SlotIdManager slotIdManager;
    private final ContainerService containerService;
    private final VolumeContainerService volumeContainerService;
    private final SlottedContainerService slottedContainerService;
    private final VeteranRewardsService veteranRewardService;

    @Inject
    public ContainerTransferService(
            GameChatService gameChatService,
            SlotIdManager slotIdManager,
            ContainerService containerService,
            VolumeContainerService volumeContainerService,
            SlottedContainerService slottedContainerService,
            VeteranRewardsService veteranRewardService) {
        this.gameChatService = gameChatService;
        this.slotIdManager = slotIdManager;
        this.containerService = containerService;
        this.volumeContainerService = volumeContainerService;
        this.slottedContainerService = slottedContainerService;
        this.veteranRewardService = veteranRewardService;
    }

    public boolean canTransferTo(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        //skipping authoritative check

        //Cannot move a reward item that is in the middle of the trade in process.
        if (veteranRewardService.isTradeInProgress(item)) {
            containerResult.setError(ContainerErrorCode.BLOCKED_BY_ITEM_BEING_TRANSFERRED);
            return false;
        }

        //skipping over the lot limit check.

        //TODO: validate transfer script hook

        if (destination != null) {
            final Container container = destination.getContainerProperty();

            if (container != null) {
                final boolean mayAdd = containerService.mayAdd(container, item, containerResult);

                if (!mayAdd)
                    LOGGER.debug("Container prevented transfer.");

                return mayAdd;
            } else {
                LOGGER.debug("Destination {} is not a container.", destination.getNetworkId());
                containerResult.setError(ContainerErrorCode.NO_CONTAINER);
                return false;
            }
        }

        return true;
    }

    public boolean canTransferToSlot(final ServerObject destination, final ServerObject item, final int slotId, final ServerObject transferer, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer == null) {
            containerResult.setError(ContainerErrorCode.NO_CONTAINER);
            return false;
        }

        if (!slottedContainerService.mayAddToSlot(slottedContainer, item, slotId, containerResult)) {
            LOGGER.debug("Container {} prevented transfoer for item {}.", destination.getDebugInformation(), item.getDebugInformation());
            return false;
        }

        if (veteranRewardService.isTradeInProgress(item)) {
            containerResult.setError(ContainerErrorCode.BLOCKED_BY_ITEM_BEING_TRANSFERRED);
            return false;
        }

        //TODO: check transfer script hook.

        return true;
    }

    public boolean transferItemToGeneralContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult) {
        return transferItemToGeneralContainer(destination, item, transferer, containerResult, false);
    }

    public boolean transferItemToGeneralContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult, final boolean allowOverloaded) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final VolumeContainer volumeContainer = destination.getVolumeContainerProperty();

        if (volumeContainer != null)
            return transferItemToVolumeContainer(destination, item, transferer, containerResult, allowOverloaded);

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer != null) {
            final int arrangement = slottedContainerService.getFirstUnoccupiedArrangement(slottedContainer, item, containerResult);

            if (arrangement != -1)
                return transferItemToSlottedContainer(destination, item, transferer, arrangement, containerResult);
        }

        return false;
    }

    public boolean transferItemToSlottedContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final int arrangementIndex, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        if (arrangementIndex < 0) {
            LOGGER.warn("Invalid arrangement index in transferItemToSlottedContainer");
            containerResult.setError(ContainerErrorCode.INVALID_ARRANGEMENT);
            return false;
        }

        if (transferer != null) {
            final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

            if (slottedProperty == null) {
                LOGGER.warn("Invalid item (no slottedContainmentProperty)");
                containerResult.setError(ContainerErrorCode.WRONG_TYPE);
                return false;
            }

            if (!slottedProperty.canManipulateArrangement(slotIdManager, arrangementIndex)) {
                containerResult.setError(ContainerErrorCode.NO_PERMISSION);
                return false;
            }
        }

        final SharedTransfer sharedTransfer = sharedTransferBegin(item, containerResult);

        if (!sharedTransfer.result)
            return false;

        final ServerObject sourceObject = sharedTransfer.sourceObject;
        final Container sourceContainer = sharedTransfer.sourceContainer;

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer == null) {
            LOGGER.warn("This destination is not a slot container");

            if (containerResult.getError() == ContainerErrorCode.SUCCESS)
                containerResult.setError(ContainerErrorCode.NO_CONTAINER);

            return false;
        }

        if (!canTransferTo(destination, item, transferer, containerResult))
            return false;

        if (!slottedContainerService.mayAdd(slottedContainer, item, arrangementIndex, containerResult))
            return false;

        if (!handleTransferSource(sourceContainer, item, containerResult))
            return false;

        if (slottedContainerService.add(slottedContainer, item, arrangementIndex, containerResult)) {
            LOGGER.warn("Checks to add an item succeeded, but the add failed.");
            return false;
        }

        //item.onContainerTransferComplete(sourceObject, destination);
        //TODO: Post transfer script hook
        return true;
    }

    public boolean transferItemToSlottedContainerSlotId(final ServerObject destination, final ServerObject item, final ServerObject transferer, final CrcString slotName, final ContainerResult containerResult) {
        final int slotId = slotIdManager.findSlotId(slotName);

        if (slotId == SlotId.INVALID) {
            LOGGER.warn("Invalid slot {}", slotName.getString());
            containerResult.setError(ContainerErrorCode.NO_SLOT);
            return false;
        }

        return transferItemToSlottedContainerSlotId(destination, item, transferer, slotId, containerResult);
    }

    public boolean transferItemToSlottedContainerSlotId(final ServerObject destination, final ServerObject item, final ServerObject transferer, final int slotId, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final SlottedContainmentProperty slottedProperty = item.getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedProperty == null) {
            LOGGER.warn("Invalid item (no slottedContainmentProperty)");
            containerResult.setError(ContainerErrorCode.WRONG_TYPE);
            return false;
        }

        return transferItemToSlottedContainer(destination, item, transferer, slottedProperty.getBestArrangementForSlot(slotId), containerResult);
    }

    public boolean transferItemToUnknownContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult, final int arrangementIndex, final Transform transform) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final VolumeContainer volumeContainer = destination.getVolumeContainerProperty();

        if (volumeContainer != null)
            return transferItemToVolumeContainer(destination, item, transferer, containerResult);

        final SlottedContainer slottedContainer = destination.getSlottedContainerProperty();

        if (slottedContainer != null)
            return transferItemToSlottedContainer(destination, item, transferer, arrangementIndex, containerResult);

        final CellProperty cellProperty = destination.getCellProperty();

        if (cellProperty != null)
            return transferItemToCell(destination, item, transferer, containerResult, transform);

        containerResult.setError(ContainerErrorCode.NO_CONTAINER);
        return false;
    }

    public boolean transferItemToVolumeContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult) {
        return transferItemToVolumeContainer(destination, item, transferer, containerResult, false);
    }

    public boolean transferItemToVolumeContainer(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult, final boolean allowOverloaded) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final SharedTransfer sharedTransfer = sharedTransferBegin(item, containerResult);

        if (!sharedTransfer.result)
            return false;

        final VolumeContainer volumeContainer = destination.getVolumeContainerProperty();

        if (volumeContainer == null) {
            LOGGER.warn("This destination is not a volume container.");
            containerResult.setError(ContainerErrorCode.WRONG_TYPE);
            return false;
        }

        /*
        if (item.isPlayerControlled()) {
            //Trying to put a player inside a volume container.
            containerResult.setError(ContainerErrorCode.BLOCKED_BY_ITEM_BEING_TRANSFERRED);
            return false;
        }
        */

        if (!canTransferTo(destination, item, transferer, containerResult)) {
            if (allowOverloaded) {
                if (!(containerResult.getError() == ContainerErrorCode.FULL || containerResult.getError() == ContainerErrorCode.TOO_LARGE))
                    return false;
            } else {
                return false;
            }
        }

        if (!handleTransferSource(sharedTransfer.sourceContainer, item, containerResult))
            return false;

        if (allowOverloaded)
            LOGGER.warn("May need to implement some hacks here to allowOverload when transfering to volume container.");

        final boolean success = volumeContainerService.add(volumeContainer, item, containerResult, allowOverloaded);

        if (!success)
            LOGGER.warn("Checks to add an item succeeded, but the add failed.");

        //item.onContainerTransferComplete(sharedTransfer.sourceObject, destination);
        //TODO: Post transfer script hooks.
        return true;
    }

    public boolean transferItemToCell(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult) {
        final Transform transform = destination.getTransformObjectToWorld().rotateTranslateLocalToParent(item.getTransformObjectToParent());
        return transferItemToCell(destination, item, transferer, containerResult, transform);
    }

    public boolean transferItemToCell(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult, final Transform transform) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

        final SharedTransfer sharedTransfer = sharedTransferBegin(item, containerResult);

        if (!sharedTransfer.result) {
            LOGGER.debug("Could not transfer from source to cell.");
            return false;
        }

        final CellProperty cellProperty = destination.getCellProperty();

        if (cellProperty == null) {
            LOGGER.warn("This destination is not a cell.");
            containerResult.setError(ContainerErrorCode.NO_CONTAINER);
            return false;
        }
//
//        if (!item.canDropInWorld()) {
//            containerResult.setError(ContainerErrorCode.WRONG_TYPE);
//            return false;
//        }

        if (!canTransferTo(destination, item, transferer, containerResult)) {
            LOGGER.debug("Could not transfer to dest cell.");
            return false;
        }

        if (!handleTransferSource(sharedTransfer.sourceContainer, item, containerResult)) {
            LOGGER.debug("Could not transfer from source.");
            return false;
        }

        cellProperty.addObjectToWorld(item);
        item.setTransformObjectToParent(transform);

        //item.onContainerTransferComplete(sharedTransfer.sourceObject, destination);
        //TODO: Post transfer script hooks.
        return true;
    }

    public boolean transferItemToWorld(final ServerObject destination, final ServerObject item, final ServerObject transferer, final ContainerResult containerResult, final Transform transform) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

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

        if (!canTransferTo(null, item, transferer, containerResult)) {
            return false;
        }

        final SharedTransfer sharedTransfer = sharedTransferBegin(item, containerResult);

        if (!sharedTransfer.result) {
            LOGGER.debug("Could not transfer to world because source disallowed.");
            return false;
        }

        if (sharedTransfer.sourceContainer == null) {
            //Already in world
            LOGGER.debug("Failed transfer to world because it was already there.");
            containerResult.setError(ContainerErrorCode.UNKNOWN);
            return false;
        }

        if (!handleTransferSource(sharedTransfer.sourceContainer, item, containerResult)) {
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

    private SharedTransfer sharedTransferBegin(final GameObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);

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
            containerResult.setError(ContainerErrorCode.NOT_FOUND);
            return new SharedTransfer(null, null, false);
        }

        final Container sourceContainer = source.getContainerProperty();

        if (sourceContainer == null) {
            containerResult.setError(ContainerErrorCode.NO_CONTAINER);
            LOGGER.warn("This source contains stuff, but has no property!");
            return new SharedTransfer(source, null, false);
        }

        return new SharedTransfer(source, sourceContainer, true);
    }

    private boolean handleTransferSource(final Container source, final ServerObject item, final ContainerResult containerResult) {
        containerResult.setError(ContainerErrorCode.SUCCESS);
        return source == null || containerService.remove(source, item, containerResult);
    }

    public static GameObject getContainedByObject(final GameObject obj) {
        final ContainedByProperty containedBy = getContainedByProperty(obj);
        return containedBy != null ? containedBy.getContainedBy() : null;
    }

    public static ContainedByProperty getContainedByProperty(final GameObject obj) {
        return obj.getContainedByProperty();
    }

    public static SlottedContainmentProperty getSlottedContainmentProperty(final GameObject obj) {
        return obj.getProperty(SlottedContainmentProperty.getClassPropertyId());
    }

    public static VolumeContainmentProperty getVolumeContainmentProperty(final GameObject obj) {
        return obj.getProperty(VolumeContainmentProperty.getClassPropertyId());
    }

    @AllArgsConstructor
    private static final class SharedTransfer {
        public final ServerObject sourceObject;
        public final Container sourceContainer;
        public final boolean result;
    }
}