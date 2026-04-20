package unity.ai.kami;

import arc.func.Cons;
import arc.func.FloatFloatf;
import arc.func.Floatc;
import arc.func.Floatc2;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;

public class KamiBulletPresets {
    private static final Vec2 vec = new Vec2();

    public static void shootLine(float from, float to, int density, Floatc c) {
        for(int i = 0; i < density; ++i) {
            float fin = Mathf.lerp(from, to, (float)i / ((float)density - 1.0F));
            if (Float.isNaN(fin)) {
                fin = to;
            }

            c.get(fin);
        }

    }

    public static void shootLine(float from, float to, int density, Floatc2 c) {
        for(int i = 0; i < density; ++i) {
            float fin = Mathf.lerp(from, to, (float)i / ((float)density - 1.0F));
            if (Float.isNaN(fin)) {
                fin = to;
            }

            c.get(fin, (float)i);
        }

    }

    public static void petal(KamiAI ai, float angleCone, float time, int amount, FloatFloatf spacingF, Floatc2 cons) {
        for(int i = 0; i < amount; ++i) {
            float fin = (float)i / ((float)amount - 1.0F);
            int[] sign = i <= 0 ? KamiPatterns.zero : Mathf.signs;
            float delay = fin * time;
            float angle = spacingF.get(fin) * angleCone;
            ai.run(delay, () -> {
                for(int s : sign) {
                    cons.get(angle * (float)s, delay);
                }

            });
        }

    }

    public static void square(BulletType type, Entityc owner, Team team, float x, float y, float rotation, float speed, int density, Cons<Bullet> cons) {
        for(int i = 0; i < 4; ++i) {
            for(int s = 0; s < density; ++s) {
                float fin = ((float)s / (float)density - 0.5F) * 2.0F;
                vec.trns((float)i * 90.0F + rotation, speed, speed * fin);
                Bullet b = type.create(owner, team, x, y, rotation);
                b.vel.set(vec);
                if (cons != null) {
                    cons.get(b);
                }
            }
        }

    }
}
