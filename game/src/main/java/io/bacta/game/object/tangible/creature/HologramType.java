package io.bacta.game.object.tangible.creature;

/**
 * Created by crush on 6/4/2016.
 */
public enum HologramType {
    NONE(-1),

    BLUE_GREEN_1(0),
    BLUE_GREEN_2(1),
    BLUE_GREEN_3(2),
    BLUE_GREEN_4(3),

    PURPLE_1(4),
    PURPLE_2(5),
    PURPLE_3(6),
    PURPLE_4(7),

    ORANGE_1(8),
    ORANGE_2(9),
    ORANGE_3(10),
    ORANGE_4(11);

    private static final HologramType[] values = values();

    public final int value;

    HologramType(final int value) { this.value = value; }

    public static HologramType from(final int value) {
        if (value < 0 || value >= ORANGE_4.value)
            return NONE;

        return values[value];
    }
}
