package unity.v8;

import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;

public class V7Lines {
    private static final Vec2 vector = new Vec2();

    public static void polySeg(int sides, int from, int to, float x, float y, float radius, float angle) {
        vector.set(0.0F, 0.0F);

        for(int i = from; i < to; ++i) {
            vector.set(radius, 0.0F).setAngle(360.0F / (float)sides * (float)i + angle + 90.0F);
            float x1 = vector.x;
            float y1 = vector.y;
            vector.set(radius, 0.0F).setAngle(360.0F / (float)sides * (float)(i + 1) + angle + 90.0F);
            Lines.line(x1 + x, y1 + y, vector.x + x, vector.y + y);
        }
    }
}
