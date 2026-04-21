package unity.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import mindustry.Vars;

public final class ReflectUtils {
    public static Class<?> box(Class<?> type) {
        if (type == Boolean.TYPE) {
            return Boolean.class;
        } else if (type == Byte.TYPE) {
            return Byte.class;
        } else if (type == Character.TYPE) {
            return Character.class;
        } else if (type == Short.TYPE) {
            return Short.class;
        } else if (type == Integer.TYPE) {
            return Integer.class;
        } else if (type == Float.TYPE) {
            return Float.class;
        } else if (type == Long.TYPE) {
            return Long.class;
        } else {
            return type == Double.TYPE ? Double.class : type;
        }
    }

    public static Class<?> unbox(Class<?> type) {
        if (type == Boolean.class) {
            return Boolean.TYPE;
        } else if (type == Byte.class) {
            return Byte.TYPE;
        } else if (type == Character.class) {
            return Character.TYPE;
        } else if (type == Short.class) {
            return Short.TYPE;
        } else if (type == Integer.class) {
            return Integer.TYPE;
        } else if (type == Float.class) {
            return Float.TYPE;
        } else if (type == Long.class) {
            return Long.TYPE;
        } else {
            return type == Double.class ? Double.TYPE : type;
        }
    }

    public static String def(Class<?> type) {
        String var10000;
        switch (unbox(type).getSimpleName()) {
            case "boolean":
                var10000 = "false";
                break;
            case "byte":
            case "char":
            case "short":
            case "int":
            case "long":
                var10000 = "0";
                break;
            case "float":
            case "double":
                var10000 = "0.0";
                break;
            default:
                var10000 = "null";
        }

        return var10000;
    }

    public static Class<?> findClassf(Class<?> type, String field) {
        for(type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
            try {
                type.getDeclaredField(field);
                break;
            }
        }

        return type;
    }

    public static Class<?> findClassm(Class<?> type, String method, Class<?>... args) {
        for(type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
            try {
                type.getDeclaredMethod(method, args);
                break;
            }
        }

        return type;
    }

    public static Class<?> findClassc(Class<?> type, Class<?>... args) {
        for(type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
            try {
                type.getDeclaredConstructor(args);
                break;
            }
        }

        return type;
    }

    public static Class<?> findClass(String name) {
        try {
            return Class.forName(name, true, Vars.mods.mainLoader());
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field findField(Class<?> type, String field, boolean access) {
        try {
            Field f = findClassf(type, field).getDeclaredField(field);
            if (access) {
                f.setAccessible(true);
            }

            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object object, Field field, Object value) {
        try {
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getField(Object object, Field field) {
        try {
            return (T)field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethod(Class<?> type, String methodName, boolean access, Class<?>... args) {
        try {
            Method m = findClassm(type, methodName, args).getDeclaredMethod(methodName, args);
            if (access) {
                m.setAccessible(true);
            }

            return m;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invokeMethod(Object object, Method method, Object... args) {
        try {
            return (T)method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> findConstructor(Class<T> type, boolean access, Class<?>... args) {
        try {
            Constructor<T> c = findClassc(type, args).getDeclaredConstructor(args);
            if (access) {
                c.setAccessible(true);
            }

            return c;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        try {
            return (T)constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> classCaller() {
        Thread thread = Thread.currentThread();
        StackTraceElement[] trace = thread.getStackTrace();

        try {
            return Class.forName(trace[3].getClassName(), false, Vars.mods.mainLoader());
        } catch (ClassNotFoundException var3) {
            return null;
        }
    }
}
