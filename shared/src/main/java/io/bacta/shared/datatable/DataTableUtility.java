package io.bacta.shared.datatable;

/**
 * Created by crush on 11/20/2015.
 */
public final class DataTableUtility {
    public static String getDelimitedString(final String input, final char openingDelimiter, final char closingDelimiter) {
        int leftIndex = input.indexOf(openingDelimiter);
        int rightIndex = input.lastIndexOf(closingDelimiter);

        if (leftIndex == -1 || rightIndex == -1)
            return ""; //Couldn't find the string.

        final String result = input.substring(leftIndex + 1, rightIndex); //Everything inside of the delimiters.

        return result;
    }

    //TODO: Figure out a way to implement this logic. These methods are used for validating a PackedObjVar.
    public static boolean consumePackedObjVarIntField(final int startingIndex, final String input) {
        return false;
    }

    public static boolean consumePackedObjVarStringField(final StringBuilder input) {
        return false;
    }
}
