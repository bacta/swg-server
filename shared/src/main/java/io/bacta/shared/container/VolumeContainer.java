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
}
