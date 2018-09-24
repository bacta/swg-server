package io.bacta.game.region;

public class RegionPvp extends RegionCircle {

    public enum PvpType {
        NORMAL(0),
        TRUCE(1),
        PVP_BATTLEFIELD(2),
        PVE_BATTLEFIELD(3),
        PVP_ADVANCED(4);

        public final int value;

        PvpType(final int value) {
            this.value = value;
        }

        public static PvpType from(int value) {
            switch (value) {
                case 0:
                    return NORMAL;
                case 1:
                    return TRUCE;
                case 2:
                    return PVP_BATTLEFIELD;
                case 3:
                    return PVE_BATTLEFIELD;
                case 4:
                    return PVP_ADVANCED;
                default:
                    throw new ArrayIndexOutOfBoundsException(value);
            }
        }
    }
}
