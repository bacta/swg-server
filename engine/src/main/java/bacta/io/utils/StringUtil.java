package bacta.io.utils;

/**
 * Created by crush on 4/19/2016.
 */
public class StringUtil {
    public static String convertUnderscoreToUpper(final String filename) {
        final StringBuilder sb = new StringBuilder(filename.length());

        for (int i = 0; i < filename.length(); ++i) {
            char c = filename.charAt(i);

            if (i > 0) {
                if (c != '_') {
                    sb.append(c);
                } else {
                    sb.append(Character.toUpperCase(filename.charAt(++i)));
                }
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }

        return sb.toString();
    }

    public static String upperFirst(final String string) {
        final char c = string.charAt(0);

        if (Character.isUpperCase(c))
            return string; //already has first letter uppercase.

        return Character.toUpperCase(c) + string.substring(1);
    }

    /**
     * Gets the first, white-space bound word in a string.
     *
     * @param string The input string.
     * @return Returns the first word in the string.
     */
    public static String getFirstWord(final String string) {
        //Input string could be something like
        //"    \tHouston we have a problem    "
        //We need to extract Houston from that string.
        final int len = string.length();
        //It's unlikely that a "word" will exceed 200 characters. In case the input is something like an entire file
        //we will limit the initial capacity of the StringBuilder to 200 bytes, or len, whichever is smaller.
        final StringBuilder sb = new StringBuilder(len > 200 ? 200 : len);

        for (int i = 0; i < len; ++i) {
            final char c = string.charAt(i);

            if (Character.isWhitespace(c)) {
                //If the StringBuilder has been written to then we've got our first word.
                if (sb.length() > 0)
                    break;

                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }
}
