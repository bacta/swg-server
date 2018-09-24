package io.bacta.swg.foundation;

/**
 * Created by crush on 4/18/2016.
 */
public class Tag {
    public static final int TAG_0000 = convertStringToTag("0000");
    public static final int TAG_0001 = convertStringToTag("0001");
    public static final int TAG_0002 = convertStringToTag("0002");
    public static final int TAG_0003 = convertStringToTag("0003");
    public static final int TAG_0004 = convertStringToTag("0004");
    public static final int TAG_0005 = convertStringToTag("0005");
    public static final int TAG_0006 = convertStringToTag("0006");
    public static final int TAG_0007 = convertStringToTag("0007");
    public static final int TAG_0008 = convertStringToTag("0008");
    public static final int TAG_0009 = convertStringToTag("0009");
    public static final int TAG_0010 = convertStringToTag("0010");
    public static final int TAG_0011 = convertStringToTag("0011");
    public static final int TAG_0012 = convertStringToTag("0012");
    public static final int TAG_0013 = convertStringToTag("0013");
    public static final int TAG_0014 = convertStringToTag("0014");
    public static final int TAG_0015 = convertStringToTag("0015");

    public static final int TAG_DATA = convertStringToTag("DATA");
    public static final int TAG_ENTR = convertStringToTag("ENTR");
    public static final int TAG_FORM = convertStringToTag("FORM");
    public static final int TAG_INFO = convertStringToTag("INFO");

    public static final int TAG_ARRY = convertStringToTag("ARRY");
    public static final int TAG_NAME = convertStringToTag("NAME");
    public static final int TAG_PARM = convertStringToTag("PARM");

    public static final int TAG_XXXX = convertStringToTag("XXXX");
    public static final int TAG_DERV = convertStringToTag("DERV");

    /**
     * Convert a text string to a Tag
     * <p>
     * This routine will convert a text string, such as "ABCD", to the tag value 'ABCD'.
     * If the string is less than 4 characters long, it will be padded with spaces.
     * If the string is greater than 4 characters long, it will be truncated.
     *
     * @param value string to convert to a tag.
     * @return The tag value.
     */
    public static int convertStringToTag(final String value) {
        int result = 0;
        int length = value.length();

        for (int i = 0; i < 4; ++i) {
            int ch = (int) (i >= length ? ' ' : value.charAt(i));
            result = (result << 8) | ch;
        }

        return result;
    }

    /**
     * Convert an integer to a tag
     * <p>
     * This routine will convert an int, such as 4, to the tag value '0004'.
     *
     * @param value The value to convert to tag format.
     * @return The tag value.
     */
    public static int convertIntToTag(int value) {
        int result = 0;

        for (int i = 0; i < 4; ++i) {
            final int digit = value % 10;
            value /= 10;
            result |= (digit + '0') << (i * 8);
        }

        return result;
    }

    public static String convertTagToString(int tag) {
        final StringBuilder sb = new StringBuilder(4);
        int i, j, ch;

        for (i = 0, j = 24; i < 4; ++i, j -= 8) {
            ch = ((tag) >> j) & 0xff;

            if (!Character.isISOControl(ch))
                sb.append((char) ch);
            else
                sb.append('?');
        }

        return sb.toString();
    }
}
