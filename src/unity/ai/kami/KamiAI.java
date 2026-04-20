package unity.ai.kami;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import java.util.Arrays;
import mindustry.entities.Units;
import mindustry.entities.units.UnitController;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import unity.ai.kami.KamiPattern.PatternType;
import unity.util.Utils;

public class KamiAI implements UnitController {
    public static final float minRange = 350.0F;
    public static final float barrierRange = 800.0F;
    protected static boolean allPatterns = true;
    private static final Vec2 vec = new Vec2();
    private static KamiPattern testPattern;
    private static final int[] limit = new int[PatternType.values().length];
    public Unit unit;
    public Unit target;
    public KamiPattern pattern;
    public KamiPattern.PatternData patternData;
    public float[] reloads = new float[16];
    public int difficulty = 0;
    public int stages = 0;
    public float x;
    public float y;
    public float patternTime;
    public float waitTime = 120.0F;
    public Rand rand = new Rand();
    protected Seq<KamiDelay> delays = new Seq();
    protected Seq<KamiPattern> patterns = new Seq();

    public void draw() {
        float z = Draw.z();
        Draw.z(115.0F);
        Lines.stroke(3.0F + Mathf.absin(12.0F, 1.0F));
        Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time));
        Draw.blend(Blending.additive);
        Lines.circle(this.x, this.y, 800.0F);
        if (this.pattern != null && this.waitTime <= 0.0F) {
            this.pattern.draw(this);
        }

        Draw.blend();
        Draw.reset();
        Draw.z(z);
    }

    public void updateFollowing() {
        float range = this.pattern != null && this.pattern.followTarget ? this.pattern.followRange : 350.0F;
        vec.trns(this.target.angleTo(this.unit), range).add(this.target).sub(this.unit).scl(0.05F * Time.delta);
        this.unit.move(vec);
        this.unit.lookAt(this.target);
    }

    public void updateUnit() {
        if (this.target != null && Units.invalidateTarget(this.target, this.unit.team, this.unit.x, this.unit.y)) {
            this.target = null;
        }

        if (this.target == null) {
            Player player = (Player)Utils.bestEntity(Groups.player, (p) -> p.unit() != null && p.unit().isValid(), (p) -> -p.dst(this.unit));
            this.target = player.unit();
        }

        if ((this.waitTime > 0.0F || this.pattern != null && this.pattern.followTarget) && this.target != null) {
            float speed = this.patternTime <= 0.0F ? Mathf.clamp(this.waitTime / 40.0F) : 1.0F;
            float range = this.pattern != null && this.pattern.followTarget ? this.pattern.followRange : 350.0F;
            vec.trns(this.target.angleTo(this.unit), range).add(this.target).sub(this.unit).scl(0.05F * speed * Time.delta);
            this.unit.move(vec);
            this.unit.lookAt(this.target);
            if (this.patternTime <= 0.0F) {
                vec.set(this.x, this.y).lerpDelta(this.target.x, this.target.y, 0.1F * speed);
                this.x = vec.x;
                this.y = vec.y;
            }
        }

        if (this.target != null && this.waitTime <= 0.0F) {
            if (this.pattern == null) {
                this.reset();
            }

            if (this.pattern != null) {
                if (this.pattern.lootAtTarget) {
                    this.unit.lookAt(this.target);
                }

                this.pattern.update(this);
                this.delays.removeAll((k) -> {
                    k.delay -= Time.delta;
                    boolean b = k.delay <= 0.0F;
                    if (b) {
                        k.run.run();
                        Pools.free(k);
                    }

                    return b;
                });
                this.patternTime -= Time.delta;
                if (this.patternTime <= 0.0F) {
                    this.waitTime = this.pattern.waitTime;
                    this.pattern.end(this);
                    this.pattern = null;
                    this.patternData = null;
                }
            }
        }

        this.waitTime = Math.max(0.0F, this.waitTime - Time.delta);
        this.updateBarrier();
    }

    public float pTime() {
        return this.pattern == null ? 0.0F : this.pattern.time - this.patternTime;
    }

    void reset() {
        Arrays.fill(this.reloads, 0.0F);

        for(KamiDelay delay : this.delays) {
            Pools.free(delay);
        }

        this.delays.clear();
        if (testPattern == null) {
            if (this.patterns.isEmpty()) {
                Arrays.fill(limit, 0);

                for(KamiPattern p : KamiPattern.all) {
                    if (allPatterns || p.type.able.get(this)) {
                        this.patterns.add(p);
                    }
                }

                this.patterns.shuffle();
                if (!allPatterns) {
                    this.patterns.removeAll((px) -> limit[px.type.ordinal()]++ >= px.type.limit);
                }

                this.patterns.sort((px) -> (float)px.type.priority);
            }

            this.pattern = (KamiPattern)this.patterns.first();
            this.patterns.remove(0);
        } else {
            this.pattern = testPattern;
        }

        if (this.pattern.data != null) {
            this.patternData = (KamiPattern.PatternData)this.pattern.data.get();
        }

        this.pattern.init(this);
        this.patternTime = this.pattern.time;
        ++this.stages;
    }

    void updateBarrier() {
        for(Player p : Groups.player) {
            Unit u = p.unit();
            if (p.unit().isValid() && !Mathf.within(this.x, this.y, u.x, u.y, 800.0F)) {
                vec.set(u).sub(this.x, this.y).limit(800.0F).add(this.x, this.y);
                u.set(vec);
            }
        }

    }

    public boolean burst(int i, float time, int bursts, float burstSpacing, Runnable begin) {
        boolean s = this.shoot(i, burstSpacing);
        if (s) {
            if (this.reloads[i + 1] <= 0.0F) {
                begin.run();
            }

            int var10002 = this.reloads[i + 1]++;
            if (this.reloads[i + 1] >= (float)bursts) {
                float[] var10000 = this.reloads;
                var10000[i] += time;
                this.reloads[i + 1] = 0.0F;
            }
        }

        return s;
    }

    public boolean burst(int i, float time, int bursts, float burstSpacing) {
        boolean s = this.shoot(i, burstSpacing);
        if (s) {
            int var10002 = this.reloads[i + 1]++;
            if (this.reloads[i + 1] >= (float)bursts) {
                float[] var10000 = this.reloads;
                var10000[i] += time;
            }
        }

        return s;
    }

    public boolean shoot(int i, float time) {
        boolean s = this.reloads[i] <= 0.0F;
        if (s) {
            float[] var10000 = this.reloads;
            var10000[i] += time;
        }

        float[] var4 = this.reloads;
        var4[i] -= Time.delta;
        return s;
    }

    public float targetAngle() {
        return this.unit.angleTo(this.target);
    }

    public void run(float delay, Runnable run) {
        if (delay <= 0.0F) {
            run.run();
        } else {
            KamiDelay k = (KamiDelay)Pools.obtain(KamiDelay.class, KamiDelay::new);
            k.delay = delay;
            k.run = run;
            this.delays.add(k);
        }
    }

    public void unit(Unit unit) {
        this.unit = unit;
        this.x = unit.x;
        this.y = unit.y;
        this.rand.setSeed((long)unit.id * 9999L);
    }

    public Unit unit() {
        return this.unit;
    }

    static {
        KamiPatterns.load();
    }

    static class KamiDelay implements Pool.Poolable {
        Runnable run;
        float delay;

        public void reset() {
            this.run = null;
            this.delay = 0.0F;
        }
    }
}
