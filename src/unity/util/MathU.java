package unity.util;

import arc.func.FloatFloatf;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;

public class MathU {
    private static final Vec2 vec = new Vec2();
    private static final Rand seedr = new Rand();

    public static void randLenVectors(long seed, int amount, float in, float inRandMin, float inRandMax, float lengthRand, FloatFloatf length, UParticleConsumer cons) {
        seedr.setSeed(seed);

        for(int i = 0; i < amount; ++i) {
            float r = seedr.random(inRandMin, inRandMax);
            float offset = r > 0.0F ? seedr.nextFloat() * r : 0.0F;
            float fin = Mathf.curve(in, offset, 1.0F - r + offset);
            float f = length.get(fin) * (lengthRand <= 0.0F ? 1.0F : seedr.random(1.0F - lengthRand, 1.0F));
            vec.trns(seedr.random(360.0F), f);
            cons.get(vec.x, vec.y, fin);
        }

    }

    public static float slope(float fin, float bias) {
        return fin < bias ? fin / bias : 1.0F - (fin - bias) / (1.0F - bias);
    }

    public static Vec2 addLength(Vec2 vec, float add) {
        float len = vec.len();
        vec.x += add * (vec.x / len);
        vec.y += add * (vec.y / len);
        return vec;
    }

    public interface UParticleConsumer {
        void get(float var1, float var2, float var3);
    }
}
