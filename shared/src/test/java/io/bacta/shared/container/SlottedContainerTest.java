package io.bacta.shared.container;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import io.bacta.shared.foundation.CrcLowerString;
import io.bacta.shared.object.GameObject;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlottedContainerTest {
    private static final AtomicLong networkIdGenerator = new AtomicLong(0);

    private final int INVENTORY_SLOT_ID = 4;
    private final int DATAPAD_SLOT_ID = 5;

    private final SlotIdManager slotIdManager = mockSlotIdManager();

    @Test
    @DisplayName("Adding to slot succeeds")
    public void addingToSlotSucceeds() {
        final GameObject parent = mockGameObject();
        final GameObject item = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();
        assertDoesNotThrow(() -> container.addToSlot(item, INVENTORY_SLOT_ID));
    }

    @Test
    @DisplayName("Removing from slot succeeds")
    public void removingFromSlotSucceeds() {
        final GameObject parent = mockGameObject();
        final GameObject item = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();
        assertDoesNotThrow(() -> container.addToSlot(item, INVENTORY_SLOT_ID));

        final boolean actual = assertDoesNotThrow(() -> container.remove(item));
        assertTrue(actual);
    }

    @Test
    @DisplayName("Removing item that is not contained throws exception")
    public void removingNonContainedItemThrowsException() {
        final GameObject parent = mockGameObject();
        final GameObject item = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();
        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.remove(item));

        assertEquals(ContainerErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Adding to self throws exception")
    public void addingToSelfThrowsException() {
        final GameObject parent = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();

        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.addToSlot(parent, INVENTORY_SLOT_ID));

        assertEquals(ContainerErrorCode.ADD_SELF, ex.getErrorCode());
    }

    @Test
    @DisplayName("Adding to occupied slot throws exception")
    public void addingToOccupiedSlotThrowsException() {
        final GameObject parent = mockGameObject();
        final GameObject existingItem = mockGameObject();
        final GameObject incomingItem = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();
        assertDoesNotThrow(() -> container.addToSlot(existingItem, INVENTORY_SLOT_ID));

        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.addToSlot(incomingItem, INVENTORY_SLOT_ID));

        assertEquals(ContainerErrorCode.SLOT_OCCUPIED, ex.getErrorCode());
    }

    @Test
    @DisplayName("Adding to non-existent slot throws exception")
    public void addingToNonExistentSlotThrowsException() {
        final GameObject parent = mockGameObject();
        final GameObject item = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();

        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.addToSlot(item, DATAPAD_SLOT_ID));

        assertEquals(ContainerErrorCode.NO_SLOT, ex.getErrorCode());
    }

    @Test
    @DisplayName("Adding to invalid arrangement throws exception")
    public void addingToInvalidArrangementThrowsException() {
        final int arrangementIndex = -1;

        final GameObject parent = mockGameObject();
        final GameObject item = mockGameObject();

        final SlottedContainer container = parent.getSlottedContainerProperty();

        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.add(item, arrangementIndex));

        assertEquals(ContainerErrorCode.INVALID_ARRANGEMENT, ex.getErrorCode());
    }

    private GameObject mockGameObject() {
        final TIntList validSlots = new TIntArrayList();
        validSlots.add(INVENTORY_SLOT_ID);

        final GameObject object = mock(GameObject.class);
        final SlottedContainer slottedContainer = new SlottedContainer(slotIdManager, object, validSlots);
        final SlottedContainmentProperty slottedContainmentProperty = new SlottedContainmentProperty(object, slotIdManager);
        final ContainedByProperty containedByProperty = new ContainedByProperty(object, null);

        final TIntList inventoryArrangement = new TIntArrayList(1);
        inventoryArrangement.add(INVENTORY_SLOT_ID);

        final TIntList datapadArrangement = new TIntArrayList(1);
        datapadArrangement.add(DATAPAD_SLOT_ID);

        slottedContainmentProperty.addArrangement(inventoryArrangement);
        slottedContainmentProperty.addArrangement(datapadArrangement);

        when(object.getNetworkId()).thenReturn(networkIdGenerator.getAndIncrement());
        when(object.getSlottedContainerProperty()).thenReturn(slottedContainer);
        when(object.getContainedByProperty()).thenReturn(containedByProperty);
        when(object.getProperty(SlottedContainmentProperty.getClassPropertyId())).thenReturn(slottedContainmentProperty);

        return object;
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

        return slotIdManager;
    }
}
