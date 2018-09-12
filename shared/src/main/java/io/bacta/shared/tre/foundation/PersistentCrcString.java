package io.bacta.shared.tre.foundation;

import com.google.common.base.Preconditions;

/**
 * Created by crush on 11/22/2015.
 */
public class PersistentCrcString extends CrcString {
    public static final PersistentCrcString EMPTY = new PersistentCrcString("", true);

    private String string;

    public PersistentCrcString() {
    }

    public PersistentCrcString(final CrcString rhs) {
        set(rhs.getString(), rhs.getCrc());
    }

    public PersistentCrcString(final String string, boolean applyNormalize) {
        set(string, applyNormalize);
    }

    public PersistentCrcString(final String string, int crc) {
        set(string, crc);
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public void clear() {
        crc = Crc.NULL;
    }

    @Override
    public void set(final String string, boolean applyNormalize) {
        Preconditions.checkNotNull(string);
        internalSet(string, applyNormalize);
        calculateCrc();
    }

    @Override
    public void set(final String string, int crc) {
        Preconditions.checkNotNull(string);
        internalSet(string, false);
        this.crc = crc;
    }

    private void internalSet(final String string, boolean applyNormalize) {
        this.string = string;
        //TODO: normalize?
        //TODO: Check system filename max length?
    }
}
