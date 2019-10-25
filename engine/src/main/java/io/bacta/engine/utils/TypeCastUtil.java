package io.bacta.engine.utils;

public final class TypeCastUtil {
    public static <T> T safeCast(Class<T> type, Object obj) {
        return type.isInstance(obj) ? type.cast(obj) : null;
    }
}
