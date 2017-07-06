/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.utils;

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
