package io.bacta.swg.template.definition;



/**
 * Created by crush on 4/19/2016.
 * <p>
 * TagInfo is a helper object for writing templates and should not be used outside of the template definition package.
 */
final class TagInfo {
    static final TagInfo None = new TagInfo(convertStringToTag("0000"), convertStringToTagString("0000"));

    final int tag;
    final String tagString;

    TagInfo(final int tag, final String tagString) {
        this.tag = tag;
        this.tagString = tagString;
    }

    TagInfo(final String tag) {
        this.tag = TagInfo.convertStringToTag(tag);
        this.tagString = TagInfo.convertStringToTagString(tag);
    }

    TagInfo(final int tag) {
        this.tag = tag;
        this.tagString = TagInfo.convertStringToTagString(TagInfo.convertTagToString(tag));
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

    public static int convertStringToTag(final String value) {
        int result = 0;
        int length = value.length();

        for (int i = 0; i < 4; ++i) {
            int ch = (int) (i >= length ? ' ' : value.charAt(i));
            result = (result << 8) | ch;
        }

        return result;
    }

    static final String convertStringToTagString(final String string) {
        if (string.length() != 4)
            throw new IllegalArgumentException("The tag string must be 4 characters");

        return String.format("TAG(%c,%c,%c,%c)",
                string.charAt(0),
                string.charAt(1),
                string.charAt(2),
                string.charAt(3));
    }
}
