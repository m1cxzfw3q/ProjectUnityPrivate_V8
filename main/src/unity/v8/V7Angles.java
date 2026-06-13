package unity.v8;

import arc.func.Floatc;

public class V7Angles {
    public static void shotgun(int points, float spacing, float offset, Floatc cons) {
        for(int i = 0; i < points; ++i) {
            cons.get(i * spacing - (points - 1) * spacing / 2.0F + offset);
        }
    }
}
