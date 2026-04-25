package unity.gen;

public final class Float2 {
    public static float x(long float2) {
        return Float.intBitsToFloat((int)(float2 >>> 0 & 4294967295L));
    }

    public static long x(long float2, float value) {
        return float2 & -4294967296L | (long)Float.floatToIntBits(value) << 0;
    }

    public static float y(long float2) {
        return Float.intBitsToFloat((int)(float2 >>> 32 & 4294967295L));
    }

    public static long y(long float2, float value) {
        return float2 & 4294967295L | (long)Float.floatToIntBits(value) << 32;
    }

    public static long construct(float x, float y) {
        return (long)Float.floatToIntBits(x) << 0 | (long)Float.floatToIntBits(y) << 32;
    }
}
