package io.bacta.swg.foundation;

/**
 * Created by crush on 11/22/2015.
 */
public class CrcLowerString extends PersistentCrcString {
    public static final CrcLowerString EMPTY = new CrcLowerString("");

    public static int calculateCrc(final String newString) {
        return new TemporaryCrcString(newString, true).getCrc();
    }

    public CrcLowerString() {
        super();
    }

    public CrcLowerString(final String newString) {
        super(newString, true);
    }

    public CrcLowerString(final String newString, final int newCrc) {
        super(newString, newCrc);
    }

    public void setString(final String newString) {
        set(newString, true);
    }

    public int compare(final String otherString, final int otherCrc) {
        if (otherString == null)
            throw new IllegalArgumentException("otherString must not be null.");

        final int crc = getCrc();

        if (crc < otherCrc)
            return -1;

        if (crc > otherCrc)
            return 1;

        final String string = getString();

        return string.compareTo(otherString);
    }

    public int compare(final CrcLowerString otherString) {
        return compare(otherString.getString(), otherString.getCrc());
    }
}
