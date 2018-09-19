package io.bacta.game.region;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Region {
    private String name; //unicode
    private int nameCrc;
    private String planet;
    private int pvp;
    private int geography;
    private int minDifficulty;
    private int maxDifficulty;
    private int spawn;
    private int mission;
    private int buildable;
    private int municipal;
    private boolean visible;
    private boolean notify;
    private int environmentFlags;
}
