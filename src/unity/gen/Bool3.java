package unity.gen;

public final class Bool3 {
    public static boolean x(byte bool3) {
        return ((long)bool3 & 1L) != 0L;
    }

    public static byte x(byte bool3, boolean value) {
        return !value ? (byte)((int)((long)bool3 & -2L)) : (byte)((int)((long)bool3 & -2L | 1L));
    }

    public static boolean y(byte bool3) {
        return ((long)bool3 & 2L) != 0L;
    }

    public static byte y(byte bool3, boolean value) {
        return !value ? (byte)((int)((long)bool3 & -3L)) : (byte)((int)((long)bool3 & -3L | 2L));
    }

    public static boolean z(byte bool3) {
        return ((long)bool3 & 4L) != 0L;
    }

    public static byte z(byte bool3, boolean value) {
        return !value ? (byte)((int)((long)bool3 & -5L)) : (byte)((int)((long)bool3 & -5L | 4L));
    }

    public static byte construct(boolean x, boolean y, boolean z) {
        return (byte)((int)((x ? 1L : 0L) | (y ? 2L : 0L) | (z ? 4L : 0L)));
    }
}
