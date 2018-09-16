package io.bacta.shared.appearance;

import io.bacta.shared.math.Transform;
import io.bacta.shared.object.GameObject;

/**
 * Created by crush on 4/22/2016.
 */
public class Appearance {
    //protected Extent extent;
    private AppearanceTemplate appearanceTemplate;
    private GameObject owner;
    private volatile int rendereredFrameNumber;
    //private Vector scale;
    private boolean keepAlive;
    private boolean useRenderEffectsFlag;
    private Transform appearanceToWorld;

    public AppearanceTemplate getAppearanceTemplate() {
        return appearanceTemplate;
    }

    public String getAppearanceTemplateName() {
        return appearanceTemplate != null
                ? appearanceTemplate.getName()
                : null;
    }
}