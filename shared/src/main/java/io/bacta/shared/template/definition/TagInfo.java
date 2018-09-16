package io.bacta.shared.template.definition;


import io.bacta.shared.foundation.Tag;

/**
 * Created by crush on 4/19/2016.
 * <p>
 * TagInfo is a helper object for writing templates and should not be used outside of the template definition package.
 */
final class TagInfo {
    static final TagInfo None = new TagInfo(Tag.TAG_0000, convertStringToTagString("0000"));

    final int tag;
    final String tagString;

    TagInfo(final int tag, final String tagString) {
        this.tag = tag;
        this.tagString = tagString;
    }

    TagInfo(final String tag) {
        this.tag = Tag.convertStringToTag(tag);
        this.tagString = TagInfo.convertStringToTagString(tag);
    }

    TagInfo(final int tag) {
        this.tag = tag;
        this.tagString = TagInfo.convertStringToTagString(Tag.convertTagToString(tag));
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
