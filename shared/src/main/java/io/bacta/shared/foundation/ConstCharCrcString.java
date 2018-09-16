package io.bacta.shared.foundation;

/**
 * Created by crush on 11/22/2015.
 */
public class ConstCharCrcString extends CrcString {
    public static final ConstCharCrcString EMPTY = new ConstCharCrcString("", 0);

    private final String string;

    public ConstCharCrcString(final String string) {
        this.string = string;

        calculateCrc();
    }

    public ConstCharCrcString(final String string, final int crc) {
        this.string = string;

        calculateCrc();

        if (this.crc != crc)
            throw new IllegalStateException("Calculated and specified crc values do not match");
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear a ConstCharCrcString");
    }

    @Override
    public void set(String string, boolean applyNormalize) {
        throw new UnsupportedOperationException("Cannot set a ConstCharCrcString");
    }

    @Override
    public void set(String string, int crc) {
        throw new UnsupportedOperationException("Cannot set a ConstCharCrcString");
    }
}
