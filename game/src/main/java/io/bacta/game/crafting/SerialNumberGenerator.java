package io.bacta.game.crafting;

public final class SerialNumberGenerator {
    public static String generate(final long networkId) {
        final char[] buffer = new char[]{'(', 0, 0, 0, 0, 0, 0, 0, 0, ')', 0};
        long value = networkId & 0x000000FFFFFFFFFFL;
        long mask;

        for (int i = 0; i < 10; ++i) {
            mask = ((value >> 4) | ((value & 0x0F) << 36));
            value ^= mask;
        }

        for (int i = 1; i < 9; ++i) {
            char c = (char) (value & 0x1F);

            value >>= 5;

            if (c < 10)
                c += '0';
            else
                c += ('a' - 10);

            buffer[i] = c;
        }

        return new String(buffer);
    }
}
