package io.bacta.game.waypoint;

/**
 * Created by crush on 5/8/2016.
 */
public final class WaypointType {
    public static final byte INVISIBLE = 0;
    public static final byte BLUE = 1;
    public static final byte GREEN = 2;
    public static final byte ORANGE = 3;
    public static final byte YELLOW = 4;
    public static final byte PURPLE = 5;
    public static final byte WHITE = 6;
    public static final byte SPACE = 7;
    public static final byte SMALL = 8;
    public static final byte ENTRANCE = 9;
    public static final byte NUM_COLORS = 10;

    public static String getWaypointColorName(final byte color) {
        if (color < 0 || color >= NUM_COLORS)
            throw new IndexOutOfBoundsException("Color is not valid.");

        return COLOR_NAMES[color];
    }

    private static final String[] COLOR_NAMES = new String[]{
            "invisible",
            "blue",
            "green",
            "orange",
            "yellow",
            "purple",
            "white",
            "space",
            "small",
            "entrance"
    };
}
