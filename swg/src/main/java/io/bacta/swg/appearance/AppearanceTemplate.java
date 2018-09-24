package io.bacta.swg.appearance;

import io.bacta.swg.collision.extent.Extent;
import io.bacta.swg.foundation.CrcLowerString;
import io.bacta.swg.foundation.Tag;
import lombok.Getter;

/**
 * Created by crush on 4/22/2016.
 */
public class AppearanceTemplate {
    public static final int TAG_APPR = Tag.convertStringToTag("APPR");
    public static final int TAG_HPTS = Tag.convertStringToTag("HPTS");
    public static final int TAG_HPNT = Tag.convertStringToTag("HPNT");
    public static final int TAG_FLOR = Tag.convertStringToTag("FLOR");

    private CrcLowerString crcName;
    @Getter
    private Extent extent;
    @Getter
    private Extent collisionExtent;
    //List<Hardpoint> hardpoints
    @Getter
    private String floorName;

    public AppearanceTemplate(final String name) {
        this.crcName = new CrcLowerString(name);
    }

    public String getName() {
        return crcName.getString();
    }
}