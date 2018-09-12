package io.bacta.shared.tre.foundation;


import io.bacta.shared.util.SOECRC32;

/**
 * Created by crush on 11/22/2015.
 */
public abstract class CrcString implements Comparable<CrcString> {
    /**
     * Normalize a string.
     * <p>
     * This function will clean up a string.  All alpha characters will be
     * changed to lower case, all backslashes will be converted to forward
     * slashes, all dots following a slash will be removed, and consecutive
     * slashes will be converted into a single slash.
     */
    public static String normalize(final String input) {
        boolean previousIsSlash = true;
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); ++i) {

            final char c = input.charAt(i);

            if (c == '\\' || c == '/') {
                if (!previousIsSlash) {
                    // convert all backslashes to forward slashes and disallow multiple slashes in a row
                    output.append('/');
                    previousIsSlash = true;
                }
            } else if (c == '.') {
                // disallow dots after slashes.  this will also handle multiple dots, and slashes following the dots
                if (!previousIsSlash)
                    output.append('.');
            } else {
                // lowercase all other characters
                output.append(Character.toLowerCase(c));
                previousIsSlash = false;
            }
        }
        return output.toString();
    }

    protected int crc;

    public boolean isEmpty() {
        final String str = getString();
        return str == null && str.isEmpty();
    }

    public int getCrc() {
        return crc;
    }

    public abstract String getString();

    public abstract void clear();

    public abstract void set(final String string, boolean applyNormalize);

    public abstract void set(final String string, int crc);

    protected CrcString() {
    }

    protected CrcString(int crc) {
        this.crc = crc;
    }

    protected void calculateCrc() {
        this.crc = SOECRC32.hashCode(getString());
    }

    @Override
    public int compareTo(final CrcString o) {
        return Integer.compare(crc, o.crc);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;

        final CrcString crcString = (CrcString) o;

        if (getCrc() != crcString.getCrc()) return false;
        return getString() != null ? getString().equals(crcString.getString()) : crcString.getString() == null;
    }

    @Override
    public int hashCode() {
        int result = getCrc();
        result = 31 * result + (getString() != null ? getString().hashCode() : 0);
        return result;
    }
}
