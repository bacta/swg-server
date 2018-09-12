package io.bacta.shared.utility;

import lombok.Getter;

/**
 * Created by crush on 4/21/2016.
 */
public class TriggerVolumeData {
    @Getter
    private String name;
    @Getter
    private float radius;

    public TriggerVolumeData() {
        this.name = null;
        this.radius = 0.0f;
    }

    public TriggerVolumeData(final String name, final float radius) {
        this.name = name;
        this.radius = radius;
    }
}
