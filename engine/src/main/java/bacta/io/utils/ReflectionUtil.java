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

package bacta.io.utils;

import java.lang.reflect.Field;

/**
 * Created by crush on 5/4/2016.
 * <p>
 * Some helper utilities for dealing with reflection.
 */
public class ReflectionUtil {
    public static <T> Field getFieldOrNull(final Class<T> classType, final String fieldName) {
        return getFieldOrNull(classType, fieldName, true);
    }
    /**
     * Gets the field, or returns null. Basically just hides the exception handling requirements.
     *
     * @param classType The class that is being reflected.
     * @param fieldName The name of the field to get.
     * @param accessible Should the field be explicitly set as accessible.
     * @param <T>       The type of the class.
     * @return A reference to the Field, or null if it doesn't exist.
     */
    public static <T> Field getFieldOrNull(final Class<T> classType, final String fieldName, final boolean accessible) {
        try {
            final Field field = classType.getDeclaredField(fieldName);

            if (accessible)
                field.setAccessible(true);

            return field;
        } catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public static void setFieldValue(final Field field, final Object obj, final Object value) {
        try {
            field.set(obj, value);
        } catch (final NullPointerException ex) {
            throw new IllegalArgumentException("field is null");
        } catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(final Field field, final Object obj) {
        try {
            return (T) field.get(obj);
        } catch (final NullPointerException ex) {
            throw new IllegalArgumentException("field is null");
        } catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
