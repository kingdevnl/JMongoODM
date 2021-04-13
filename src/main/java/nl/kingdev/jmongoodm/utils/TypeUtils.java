package nl.kingdev.jmongoodm.utils;

public class TypeUtils {

    public static boolean isPrimitive(Class<?> type) {
        return type == String.class || type == boolean.class || type == byte.class || type == char.class || type == short.class
                || type == int.class || type == long.class || type == float.class || type == double.class;
    }
}
