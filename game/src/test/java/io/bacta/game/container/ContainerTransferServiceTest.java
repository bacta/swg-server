package io.bacta.game.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.player.VeteranRewardsService;
import io.bacta.shared.container.*;
import io.bacta.shared.foundation.CrcLowerString;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContainerTransferServiceTest {
    private static final AtomicLong networkIdGenerator = new AtomicLong(0);

    private static final CrcLowerString INVENTORY_SLOT_NAME = new CrcLowerString("inventory");
    private static final CrcLowerString HAT_SLOT_NAME = new CrcLowerString("hat");

    private static final int INVENTORY_SLOT_ID = 4;
    private static final int DATAPAD_SLOT_ID = 5;
    private static final int GHOST_SLOT_ID = 19;
    private static final int HAT_SLOT_ID = 20;

    private final SlotIdManager slotIdManager = mockSlotIdManager();
    private final ContainerTransferService containerTransferService = mockContainerTransferService();

    @Test
    @DisplayName("Should transfer item to slotted container")
    public void shouldTransferItemToSlottedContainer() {
        final ServerObject player = mockPlayer();
        final ServerObject inventory = mockInventory();
        final ServerObject item = mockItem();

        //Add our item to the inventory.
        final VolumeContainer volumeContainer = inventory.getVolumeContainerProperty();
        assertDoesNotThrow(() -> volumeContainer.add(item));

        //Add our inventory to our player.
        final SlottedContainer slottedContainer = player.getSlottedContainerProperty();
        assertDoesNotThrow(() -> slottedContainer.addToSlot(inventory, INVENTORY_SLOT_ID));

        //Now, we want to transfer our item from the inventory to the player
        int arrangementIndex = assertDoesNotThrow(() -> slottedContainer.getFirstUnoccupiedArrangement(item));

        assertDoesNotThrow(() ->
                containerTransferService.transferItemToSlottedContainer(player, item, null, arrangementIndex));

        final ContainedByProperty containedBy = item.getContainedByProperty();

        //Inventory should be empty.
        //The item should be contained by the player now.
        //The item player's hat slot should not be empty.
        assertEquals(0, volumeContainer.getCurrentVolume());
        assertFalse(slottedContainer.isSlotEmpty(HAT_SLOT_ID));
        assertEquals(player, containedBy.getContainedBy());
    }

    @Test
    @DisplayName("Should transfer item to specific slot of slotted container")
    public void shouldTransferItemToSpecificSlotOfSlottedContainer() {
        final ServerObject player = mockPlayer();
        final ServerObject inventory = mockInventory();
        final ServerObject item = mockItem();

        //Add our item to the inventory.
        final VolumeContainer volumeContainer = inventory.getVolumeContainerProperty();
        assertDoesNotThrow(() -> volumeContainer.add(item));

        //Add our inventory to our player.
        final SlottedContainer slottedContainer = player.getSlottedContainerProperty();
        assertDoesNotThrow(() -> slottedContainer.addToSlot(inventory, INVENTORY_SLOT_ID));

        assertDoesNotThrow(() ->
                containerTransferService.transferItemToSlottedContainerSlotId(player, item, null, HAT_SLOT_NAME));

        final ContainedByProperty containedBy = item.getContainedByProperty();

        //Inventory should be empty.
        //The item should be contained by the player now.
        //The item player's hat slot should not be empty.
        assertEquals(0, volumeContainer.getCurrentVolume());
        assertFalse(slottedContainer.isSlotEmpty(HAT_SLOT_ID));
        assertEquals(player, containedBy.getContainedBy());
    }

    private ServerObject mockPlayer() {
        final ServerObject player = mock(CreatureObject.class);
        final long networkId = networkIdGenerator.getAndIncrement();

        final TIntList validSlots = new TIntArrayList(2);
        validSlots.add(INVENTORY_SLOT_ID);
        validSlots.add(HAT_SLOT_ID);

        final SlottedContainer slottedContainer = new SlottedContainer(slotIdManager, player, validSlots);
        final ContainedByProperty containedBy = new ContainedByProperty(player, null);

        when(player.getNetworkId()).thenReturn(networkId);
        when(player.getSlottedContainerProperty()).thenReturn(slottedContainer);
        when(player.getContainerProperty()).thenReturn(slottedContainer);
        when(player.getContainedByProperty()).thenReturn(containedBy);

        return player;
    }

    private ServerObject mockInventory() {
        final ServerObject inventory = mock(TangibleObject.class);
        final long networkId = networkIdGenerator.getAndIncrement();

        final VolumeContainer volumeContainer = new VolumeContainer(inventory, 80);
        final SlottedContainmentProperty containment = new SlottedContainmentProperty(inventory, slotIdManager);
        final ContainedByProperty containedBy = new ContainedByProperty(inventory, null);

        final TIntList arrangement = new TIntArrayList(1);
        arrangement.add(INVENTORY_SLOT_ID);
        containment.addArrangement(arrangement);

        when(inventory.getNetworkId()).thenReturn(networkId);
        when(inventory.getVolumeContainerProperty()).thenReturn(volumeContainer);
        when(inventory.getContainerProperty()).thenReturn(volumeContainer);
        when(inventory.getContainedByProperty()).thenReturn(containedBy);
        when(inventory.getProperty(SlottedContainmentProperty.getClassPropertyId())).thenReturn(containment);

        return inventory;
    }

    private ServerObject mockItem() {
        final ServerObject item = mock(TangibleObject.class);
        final long networkId = networkIdGenerator.getAndIncrement();

        final VolumeContainmentProperty volumeContainment = new VolumeContainmentProperty(item, 1);
        final SlottedContainmentProperty slottedContainment = new SlottedContainmentProperty(item, slotIdManager);
        final ContainedByProperty containedBy = new ContainedByProperty(item, null);

        final TIntList arrangement = new TIntArrayList(1);
        arrangement.add(HAT_SLOT_ID);
        slottedContainment.addArrangement(arrangement);

        when(item.getNetworkId()).thenReturn(networkId);
        when(item.getContainedByProperty()).thenReturn(containedBy);
        when(item.getProperty(VolumeContainmentProperty.getClassPropertyId())).thenReturn(volumeContainment);
        when(item.getProperty(SlottedContainmentProperty.getClassPropertyId())).thenReturn(slottedContainment);

        return item;
    }

    private SlotIdManager mockSlotIdManager() {
        final SlotIdManager slotIdManager = mock(SlotIdManager.class);
        when(slotIdManager.getSlotsThatHoldAnything()).thenReturn(Lists.list("anythingNoMod1", "anythingNoMod2", "anythingCanMod1", "anythingCanMod2"));
        when(slotIdManager.findSlotId(new CrcLowerString("anythingNoMod1"))).thenReturn(0);
        when(slotIdManager.findSlotId(new CrcLowerString("anythingNoMod2"))).thenReturn(1);
        when(slotIdManager.findSlotId(new CrcLowerString("anythingCanMod1"))).thenReturn(2);
        when(slotIdManager.findSlotId(new CrcLowerString("anythingCanMod2"))).thenReturn(3);
        when(slotIdManager.findSlotId(new CrcLowerString("inventory"))).thenReturn(INVENTORY_SLOT_ID);
        when(slotIdManager.findSlotId(new CrcLowerString("datapad"))).thenReturn(DATAPAD_SLOT_ID);
        when(slotIdManager.findSlotId(new CrcLowerString("ghost"))).thenReturn(GHOST_SLOT_ID);
        when(slotIdManager.findSlotId(new CrcLowerString("hat"))).thenReturn(HAT_SLOT_ID);

        return slotIdManager;
    }

    private ContainerTransferService mockContainerTransferService() {
        final VeteranRewardsService veteransRewardService = mock(VeteranRewardsService.class);
        final ContainerTransferService service = new ContainerTransferService(slotIdManager, veteransRewardService);

        return service;
    }
}
