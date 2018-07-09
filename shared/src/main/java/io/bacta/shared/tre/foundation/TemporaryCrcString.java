package io.bacta.shared.tre.foundation;

/**
 * Created by crush on 11/22/2015.
 */
public class TemporaryCrcString extends CrcString {
    private static final int BUFFER_SIZE = 512;

    private final StringBuilder buffer = new StringBuilder(BUFFER_SIZE);

    public TemporaryCrcString() {
    }

    public TemporaryCrcString(final TemporaryCrcString copy) {
        set(copy.getString(), copy.getCrc());
    }

    public TemporaryCrcString(final String string, final boolean applyNormalize) {
        set(string, applyNormalize);
    }

    public TemporaryCrcString(final String string, final int crc) {
        set(string, crc);
    }

    public String getString() {
        return buffer.toString();
    }

    public void clear() {
        buffer.setLength(0);
        crc = Crc.NULL;
    }

    public void set(final String string, final boolean applyNormalize) {
        internalSet(string, applyNormalize);
        calculateCrc();
    }

    public void set(final String string, final int crc) {
        internalSet(string, false);
        this.crc = crc;
    }

    private void internalSet(final String string, final boolean applyNormalize) {
        clear();

        if (applyNormalize)
            buffer.append(normalize(string));
        else
            buffer.append(string);

    }


}
