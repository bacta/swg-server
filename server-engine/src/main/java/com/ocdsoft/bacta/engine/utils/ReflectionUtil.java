package com.ocdsoft.bacta.engine.utils;

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
