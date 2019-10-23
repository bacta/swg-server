package io.bacta.game.player.creation;

import lombok.Getter;

/**
 * Created by crush on 6/3/2016.
 */
@Getter
public final class EquipmentInfo {
    private final int arrangementIndex;
    private final String sharedTemplateName;
    private final String serverTemplateName;

    public EquipmentInfo(final int arrangementIndex, final String sharedTemplateName, final String serverTemplateName) {
        this.arrangementIndex = arrangementIndex;
        this.sharedTemplateName = sharedTemplateName;
        this.serverTemplateName = serverTemplateName.isEmpty()
                ? sharedTemplateName.replace("/shared_", "/")
                : serverTemplateName;
    }
}
