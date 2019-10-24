package io.bacta.shared.container;

import io.bacta.shared.object.GameObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VolumeContainerTest {
    private static final AtomicLong networkIdGenerator = new AtomicLong(0);

    @Test
    @DisplayName("Adding item when full throws exception")
    public void addingItemWhenFullThrowsException() {
        final GameObject parent = mockVolumeContainer(1, 1);
        final GameObject item1 = mockGameObject(1);
        final GameObject item2 = mockGameObject(1);

        final VolumeContainer container = parent.getVolumeContainerProperty();
        assertDoesNotThrow(() -> container.add(item1));

        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.add(item2));

        assertEquals(ContainerErrorCode.FULL, ex.getErrorCode());
    }

    @Test
    @DisplayName("Adding to self throws exception")
    public void addingToSelfThrowsException() {
        final GameObject parent = mockVolumeContainer(1, 1);

        final VolumeContainer container = parent.getVolumeContainerProperty();

        final ContainerTransferException ex =
                assertThrows(ContainerTransferException.class,
                        () -> container.add(parent));

        assertEquals(ContainerErrorCode.ADD_SELF, ex.getErrorCode());
    }

    private GameObject mockVolumeContainer(int totalVolume, int volume) {
        final GameObject object = mock(GameObject.class);
        final VolumeContainer container = new VolumeContainer(object, totalVolume);
        final VolumeContainmentProperty containmentProperty = new VolumeContainmentProperty(object, volume);
        final ContainedByProperty containedByProperty = new ContainedByProperty(object, null);

        when(object.getNetworkId()).thenReturn(networkIdGenerator.getAndIncrement());
        when(object.getVolumeContainerProperty()).thenReturn(container);
        when(object.getContainedByProperty()).thenReturn(containedByProperty);
        when(object.getProperty(VolumeContainmentProperty.getClassPropertyId())).thenReturn(containmentProperty);

        return object;
    }

    private GameObject mockGameObject(int volume) {
        final GameObject object = mock(GameObject.class);
        final VolumeContainmentProperty containmentProperty = new VolumeContainmentProperty(object, volume);
        final ContainedByProperty containedByProperty = new ContainedByProperty(object, null);

        when(object.getNetworkId()).thenReturn(networkIdGenerator.getAndIncrement());
        when(object.getContainedByProperty()).thenReturn(containedByProperty);
        when(object.getProperty(VolumeContainmentProperty.getClassPropertyId())).thenReturn(containmentProperty);

        return object;
    }
}
