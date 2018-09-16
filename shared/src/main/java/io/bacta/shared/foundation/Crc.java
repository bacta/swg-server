package io.bacta.shared.foundation;


import io.bacta.shared.util.SOECRC32;

/**
 * Created by crush on 11/22/2015.
 */
public class Crc {
    public static final int NULL = 0x00000000;
    public static final int INIT = 0xFFFFFFFF;

    public static int calculate(final String string) {
        return SOECRC32.hashCode(string);
    }

    public static int normalizeAndCalculate(final String string) {
        return new TemporaryCrcString(string, true).getCrc();
    }
}
