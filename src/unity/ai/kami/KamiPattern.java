package unity.ai.kami;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import unity.type.RainbowUnitType;

public class KamiPattern {
    public static final Seq<KamiPattern> all = new Seq();
    public int id;
    public float time;
    public float waitTime;
    public float followRange;
    public boolean lootAtTarget;
    public boolean followTarget;
    public Cons<KamiAI> cons;
    public Prov<PatternData> data;
    public PatternType type;

    public KamiPattern(float time) {
        this(time, 180.0F);
    }

    public KamiPattern(float time, float waitTime) {
        this.followRange = 350.0F;
        this.lootAtTarget = true;
        this.type = KamiPattern.PatternType.basic;
        this.time = time;
        this.waitTime = waitTime;
        this.id = all.size;
        all.add(this);
    }

    public void update(KamiAI ai) {
    }

    public void init(KamiAI ai) {
    }

    public void end(KamiAI ai) {
    }

    public void draw(KamiAI ai) {
        if (this.type == KamiPattern.PatternType.bossBasic) {
            float z = Draw.z();
            RainbowUnitType rt = (RainbowUnitType)ai.unit.type;
            TextureRegion r = rt.trailRegion;
            float fin = Mathf.clamp(ai.pTime() / 80.0F);
            Draw.z(z - 0.01F);

            for(int i = 0; i < 3; ++i) {
                float ang = (float)i * 360.0F / 3.0F + ai.patternTime * 2.0F;
                Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time + ang));
                Vec2 v = Tmp.v1.trns(ang, fin * 45.0F).add(ai.unit.x, ai.unit.y);
                Draw.rect(r, v.x, v.y, ai.unit.rotation - 90.0F);
            }

            Draw.z(z);
        }

    }

    public String toString() {
        return "KamiPattern: " + this.id + "priority: " + this.type.priority;
    }

    public static enum PatternType {
        permanent((ai) -> true),
        basic((ai) -> {
            int s = ai.stages;
            int ms = 10;
            float chance = 1.0F - (float)(s - ms) / 5.0F;
            return s < ms || chance > 0.0F && ai.rand.chance((double)chance);
        }),
        bossBasic((ai) -> ai.stages > 5 && ai.stages % 3 == 2, 1, 2),
        advance((ai) -> ai.stages > 10, 5, 1);

        public final Boolf<KamiAI> able;
        public final int limit;
        public final int priority;

        private PatternType(Boolf<KamiAI> able) {
            this(able, 10, 0);
        }

        private PatternType(Boolf<KamiAI> able, int limit, int priority) {
            this.able = able;
            this.limit = limit;
            this.priority = priority;
        }
    }

    public static class PatternData {
    }

    public static class StagePattern extends KamiPattern {
        Stage[] stages;

        public StagePattern(float time, Stage... stages) {
            this(time, KamiPattern.PatternType.basic, stages);
        }

        public StagePattern(float time, PatternType type, Stage... stages) {
            super(time);
            this.stages = stages;
            this.type = type;
            this.data = StageData::new;
            if (time < 0.0F) {
                float t = 0.0F;

                for(Stage s : stages) {
                    t += s.time * (float)(s.loop + 1);
                }

                this.time = t * -time;
            }

        }

        public void init(KamiAI ai) {
            StageData d = (StageData)ai.patternData;
            this.initAlt(ai, d);
        }

        void initAlt(KamiAI ai, StageData d) {
            Stage s = this.stages[d.index];
            if (s.init != null) {
                s.init.get(ai, d);
            }

        }

        public void update(KamiAI ai) {
            StageData d = (StageData)ai.patternData;
            Stage s = this.stages[d.index];
            s.cons.get(ai, d);
            d.time += Time.delta;
            if (d.time > s.time) {
                d.time = 0.0F;
                ++d.loops;
                this.initAlt(ai, d);
            }

            if (d.loops > s.loop) {
                d.loops = 0;
                d.index = (short)((d.index + 1) % this.stages.length);
                this.initAlt(ai, d);
            }

        }

        public static class Stage {
            float time;
            short loop;
            Cons2<KamiAI, StageData> cons;
            Cons2<KamiAI, StageData> init;

            public Stage(float time, Cons2<KamiAI, StageData> cons) {
                this(time, 0, cons, (Cons2)null);
            }

            public Stage(float time, Cons2<KamiAI, StageData> cons, Cons2<KamiAI, StageData> init) {
                this(time, 0, cons, init);
            }

            public Stage(float time, int loop, Cons2<KamiAI, StageData> cons, Cons2<KamiAI, StageData> init) {
                this.time = time;
                this.loop = (short)loop;
                this.cons = cons;
                this.init = init;
            }
        }

        static class StageData extends PatternData {
            public short loops;
            public short index;
            public float time;
        }
    }
}
