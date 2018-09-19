package io.bacta.game.region;

public enum RegionGeometry {
    RECTANGLE(0),
    CIRCLE(1);

    public final int value;

    RegionGeometry(final int value) {
        this.value = value;
    }

    public static RegionGeometry from(int value) {
        switch (value) {
            case 0:
                return RECTANGLE;
            case 1:
                return CIRCLE;
            default:
                throw new ArrayIndexOutOfBoundsException(value);
        }
    }
}
