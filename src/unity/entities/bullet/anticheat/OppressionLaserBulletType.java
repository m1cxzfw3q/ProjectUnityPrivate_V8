package unity.entities.bullet.anticheat;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Sized;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Rotc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import unity.content.effects.ChargeFx;
import unity.content.effects.HitFx;
import unity.content.effects.ShootFx;
import unity.entities.bullet.anticheat.modules.AbilityDamageModule;
import unity.entities.bullet.anticheat.modules.AntiCheatBulletModule;
import unity.entities.bullet.anticheat.modules.ArmorDamageModule;
import unity.entities.bullet.anticheat.modules.ForceFieldDamageModule;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class OppressionLaserBulletType extends AntiCheatBulletTypeBase {
    private static final int detail = 24;
    private static final float timeMul = 4.0F;
    public static final float[] shape = new float[96];
    public static final float[] quad = new float[8];
    private static final float[] ltmp = new float[25];
    private static final float[] ltmp2 = new float[25];
    private static final Rand rand = new Rand();
    private static final Rand rand2 = new Rand();
    private static final FloatSeq lines = new FloatSeq();
    private static final Color[] lightningColors;
    protected float length = 2150.0F;
    protected float width = 140.0F;
    protected float cone = 380.0F;
    protected float endLength = 450.0F;
    protected float fadeInTime = 15.0F;
    protected TextureRegion gradientRegion;

    public OppressionLaserBulletType() {
        this.speed = 0.0F;
        this.damage = 9000.0F;
        this.buildingDamageMultiplier = 0.4F;
        this.ratioStart = 100000.0F;
        this.ratioDamage = 0.016666668F;
        this.overDamage = 650000.0F;
        this.overDamagePower = 2.7F;
        this.overDamageScl = 4000.0F;
        this.bleedDuration = 600.0F;
        this.despawnEffect = Fx.none;
        this.hitEffect = HitFx.endHitRedBig;
        this.hittable = this.collides = this.absorbable = this.keepVelocity = false;
        this.impact = true;
        this.pierceShields = this.pierce = true;
        this.lifetime = 480.0F;
        this.knockback = 9.0F;
        this.shootEffect = ChargeFx.oppressionCharge;
        this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.002F, 4.0F, 20.0F, 4.0F), new AbilityDamageModule(40.0F, 350.0F, 6.0F, 0.001F, 4.0F), new ForceFieldDamageModule(8.0F, 20.0F, 220.0F, 6.0F, 0.025F)};
    }

    public void load() {
        this.gradientRegion = Core.atlas.find("unity-gradient");
    }

    public float range() {
        return this.length / 3.0F;
    }

    public void init() {
        super.init();
        this.drawSize = this.length * 2.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        if (b.owner instanceof Rotc) {
            ShootFx.oppressionShoot.at(b.x, b.y, ((Rotc)b.owner).rotation(), b.owner);
        }

    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F)) {
            float fw = b.time < this.fadeInTime ? Interp.pow2Out.apply(b.time / this.fadeInTime) : 1.0F;
            float fow = Mathf.clamp((b.lifetime - b.time) / 120.0F);
            float width = this.width * fw * fow;
            Tmp.v1.trns(b.rotation(), this.length + this.endLength).add(b);
            Utils.collideLineLarge(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width, 24, false, (e, v) -> {
                float dst = b.dst(v);
                float w = this.getWidthCollision(dst, width);
                if (w > 0.0F && e.within(v, w + e.hitSize() / 2.0F)) {
                    Tmp.r1.setCentered(e.getX(), e.getY(), e.hitSize()).grow(width * 2.0F);
                    Vec2 hv = Geometry.raycastRect(b.x, b.y, Tmp.v1.x, Tmp.v1.y, Tmp.r1);
                    if (hv != null) {
                        float hs = e.hitSize() / 2.0F;
                        float scl = Math.max((hs - width) / hs, Math.min(hs / width, 1.0F));
                        hv.sub(e).scl(scl).add(e);
                        v.set(hv);
                        return true;
                    }
                }

                return false;
            }, (x, y, e, d) -> {
                this.hit(b, x, y);
                if (e instanceof Sized) {
                    HitFx.endDeathLaserHit.at(x, y, b.angleTo(e), ((Sized)e).hitSize());
                }

                if (e instanceof Unit) {
                    this.hitUnitAntiCheat(b, (Unit)e);
                } else if (e instanceof Building) {
                    this.hitBuildingAntiCheat(b, (Building)e);
                }

                return false;
            });
        }

    }

    public void drawLight(Bullet b) {
    }

    public void draw(Bullet b) {
        float wid = this.width * 0.125F;
        float fw = b.time < this.fadeInTime ? Interp.pow2Out.apply(b.time / this.fadeInTime) : 1.0F;
        float fow = Mathf.clamp((b.lifetime - b.time) / 120.0F);
        float inout = fw * fow;
        float width = this.width * inout + Mathf.absin(Time.time, 5.0F, 2.0F * inout);
        float sin = Mathf.absin(Time.time, 3.0F, 0.35F);
        Color col = Tmp.c1.set(UnityPal.scarColor).mul(1.0F + sin);
        Draw.color(col);
        Draw.blend();

        for(int i = 0; i < shape.length; i += 4) {
            if (i >= shape.length - 4) {
                Vec2 v = Tmp.v1.trns(b.rotation(), this.length + wid).add(b);
                Vec2 v2 = Tmp.v2.trns(b.rotation(), this.cone).add(b);
                Lines.stroke(width * 2.0F);
                Lines.line(v2.x, v2.y, v.x, v.y, false);
                this.drawEndVoid(b, v.x, v.y, width);
            } else {
                for(int j = 0; j < quad.length; j += 2) {
                    Vec2 v = Tmp.v1.trns(b.rotation(), shape[i + j + 1] * this.cone, shape[i + j] * width).add(b);
                    quad[j] = v.x;
                    quad[j + 1] = v.y;
                }

                Fill.quad(quad[0], quad[1], quad[2], quad[3], quad[4], quad[5], quad[6], quad[7]);
            }
        }

        long seed = (long)b.id * 9999L + 7813L;

        for(int s : Mathf.signs) {
            float stroke = (2.0F + sin / 0.35F) * 1.5F * fow;
            Vec2 v = Tmp.v1.trns(b.rotation(), this.length + wid, width * (float)s).add(b);
            this.drawEndEdge(b, seed, v.x, v.y, width * (float)s, stroke);
            seed += (long)rand.nextInt();
        }

        rand.setSeed((long)b.id * 9999L + 8957324L);
        float spikeLength = this.endLength;
        float time = Time.time;
        Lines.stroke(3.0F);

        for(int i = 0; i < 45; ++i) {
            float d = rand.random(14.0F, 22.0F);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            rand2.setSeed((long)timeSeed);
            float trns = rand2.random(90.0F, 270.0F) * Interp.pow2Out.apply(fin) * inout;
            float rot = (rand2.chance((double)0.5F) ? 1.0F : -1.0F) * rand2.random(50.0F, 85.0F) + b.rotation();
            Tmp.v1.trns(rot, trns).add(b);
            if (rand2.chance((double)0.75F)) {
                float l = rand2.random(10.0F, 35.0F) * (1.0F - fin) * inout;
                Lines.lineAngle(Tmp.v1.x, Tmp.v1.y, rot, l, false);
            } else {
                float scl = rand2.random(6.0F, 12.0F) * (1.0F - fin) * inout;
                Fill.square(Tmp.v1.x, Tmp.v1.y, scl, 45.0F);
            }
        }

        for(int i = 0; i < 18; ++i) {
            boolean alt = i < 8;
            float as = alt ? 1.0F : 3.0F;
            float wid2 = alt ? width : width / 1.5F;
            float d = alt ? rand.random(12.0F, 22.0F) : rand.random(22.0F, 60.0F);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            rand2.setSeed((long)timeSeed);
            Draw.color(Color.white);
            float delay = rand2.random(0.55F, 0.8F);
            float pos1 = Mathf.pow(rand2.nextFloat(), 2.0F);
            float w = rand2.random(2.0F, 7.0F + (1.0F - pos1) * 3.0F) * as * Mathf.lerp(1.0F, rand2.random(0.8F, 1.2F), fin);
            float l = rand2.random(this.length / 2.0F);
            float pos2 = pos1 * Math.max(wid2 - w * 2.0F, 0.0F) * (float)Mathf.sign(rand2.chance((double)0.5F));
            float trns = (this.length + spikeLength * (1.0F - pos1) - l) * rand2.random(1.0F - pos1 * 0.25F, 1.1F);
            float f1 = Mathf.curve(fin, 0.0F, 1.0F - delay);
            float f2 = Mathf.curve(fin, delay, 1.0F);
            Interp p = Interp.pow2In;
            Lines.stroke(w * inout);
            this.drawLine(b, p.apply(f1) * trns + l, p.apply(f2) * trns + l, pos2);
        }

        for(int i = 0; i < 40; ++i) {
            boolean alt = i < 17;
            float as = alt ? 1.0F : 2.25F;
            float wid2 = alt ? width : width / 1.5F;
            float d = alt ? rand.random(16.0F, 27.0F) : rand.random(34.0F, 65.0F);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            rand2.setSeed((long)timeSeed);
            Draw.color(rand2.chance((double)0.75F) ? Color.black : col);
            float w = rand2.random(20.0F, 35.0F) * inout * as * Mathf.slope(fin);
            float l = rand2.random(90.0F, 190.0F) * as * (alt ? 1.0F : 1.5F);
            float p1 = Mathf.pow(rand2.nextFloat(), 2.0F);
            float trns = rand2.random(this.length / 12.0F, this.length / 5.0F);
            float yps = rand2.random(l, Math.max(this.length + spikeLength * (1.0F - p1) - (l + trns), l));
            float xps = p1 * Math.max(wid2 - w, 0.0F) * (float)Mathf.sign(rand2.chance((double)0.5F)) + rand2.range(8.0F) * fin;
            float wScl = this.getWidth(yps, 1.0F);
            Interp p = Interp.pow2In;
            Tmp.v1.trns(b.rotation(), trns * p.apply(fin) + yps, xps * wScl).add(b);
            UnityDrawf.diamond(Tmp.v1.x + Mathf.range(6.0F) * fin, Tmp.v1.y + Mathf.range(6.0F) * fin, w, l, b.rotation());
        }

        for(int i = 0; i < 20; ++i) {
            boolean alt = i < 12;
            float as = alt ? 1.0F : 3.0F;
            float wid2 = alt ? width : width / 2.0F;
            float d = alt ? rand.random(12.0F, 22.0F) : rand.random(22.0F, 60.0F);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            rand2.setSeed((long)timeSeed);
            Draw.color(rand2.chance(alt ? (double)0.5F : (double)0.75F) ? Color.black : col);
            float delay = rand2.random(0.55F, 0.8F);
            float pos1 = Mathf.pow(rand2.nextFloat(), 2.0F);
            float w = rand2.random(2.0F, 7.0F + (1.0F - pos1) * 3.0F) * as * Mathf.lerp(1.0F, rand2.random(0.8F, 1.2F), fin);
            float l = rand2.random(this.length / 2.0F);
            float pos2 = pos1 * Math.max(wid2 - w * 2.0F, 0.0F) * (float)Mathf.sign(rand2.chance((double)0.5F));
            float trns = (this.length + spikeLength * (1.0F - pos1) - l) * rand2.random(1.0F - pos1 * 0.25F, 1.1F);
            float f1 = Mathf.curve(fin, 0.0F, 1.0F - delay);
            float f2 = Mathf.curve(fin, delay, 1.0F);
            Interp p = Interp.pow2In;
            Lines.stroke(w * inout);
            this.drawLine(b, p.apply(f1) * trns + l, p.apply(f2) * trns + l, pos2);
        }

        rand.setSeed((long)b.id * 999L + 7452L);

        for(int i = 0; i < 5; ++i) {
            float d = rand.random(30.0F, 50.0F);
            float timeOffset = rand.random(d);
            int timeSeed = (int)((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            this.drawLightning(b, timeSeed, fin, fow, width);
        }

        Draw.blend();
        Draw.reset();
    }

    void drawLine(Bullet b, float l1, float l2, float width) {
        lines.clear();
        float sw = Lines.getStroke();
        float s = Mathf.clamp((sw - 3.0F) / 2.0F);
        if (l1 < l2) {
            float l = l1;
            l1 = l2;
            l2 = l;
        }

        float h = Math.min(this.cone, l1) - l2;
        if (h > 0.0F) {
            for(float l = l2; l < Math.min(this.cone, l1); l += 4.0F) {
                Tmp.v1.trns(b.rotation(), l, this.getWidth(l, width)).add(b);
                lines.add(Tmp.v1.x, Tmp.v1.y);
            }

            if (l1 > this.cone) {
                Tmp.v1.trns(b.rotation(), this.cone, width).add(b);
                lines.add(Tmp.v1.x, Tmp.v1.y);
            }
        } else {
            Tmp.v1.trns(b.rotation(), l2, width).add(b);
            lines.add(Tmp.v1.x, Tmp.v1.y);
        }

        Tmp.v1.trns(b.rotation(), l1, this.getWidth(l1, width)).add(b);
        lines.add(Tmp.v1.x, Tmp.v1.y);

        for(int i = 0; i < lines.size - 2; i += 2) {
            float x1 = lines.get(i);
            float y1 = lines.get(i + 1);
            float x2 = lines.get(i + 2);
            float y2 = lines.get(i + 3);
            Lines.line(x1, y1, x2, y2, false);
            if (sw > 3.0F) {
                if (i == 0) {
                    Drawf.tri(x1, y1, sw * 1.22F, sw * s * 2.0F, Angles.angle(x2, y2, x1, y1));
                }

                if (i == lines.size - 4) {
                    Drawf.tri(x2, y2, sw * 1.22F, sw * s * 2.0F, Angles.angle(x1, y1, x2, y2));
                }
            }
        }

    }

    float getWidth(float length, float width) {
        return length >= this.cone ? width : Interp.circleOut.apply(length / this.cone) * width;
    }

    float getWidthCollision(float length, float width) {
        return length < this.length ? this.getWidth(length, width) : Mathf.clamp(1.0F - (length - this.length) / this.endLength) * width;
    }

    void drawEndEdge(Bullet b, long seed, float x, float y, float width, float stroke) {
        float spikeLength = this.endLength;
        float time = Time.time * 4.0F;
        rand.setSeed((long)b.id * 9999L + seed);
        Drawf.tri(x, y, stroke * 1.22F, Math.abs(width) / 4.0F, b.rotation());

        for(int i = 0; i < 14; ++i) {
            float d = rand.random(30.0F, 120.0F);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            rand2.setSeed((long)timeSeed);
            float w = rand2.random(stroke * 3.0F, stroke * 5.0F);
            float ofRand = rand2.random(0.5F, 0.8F);
            float of = -width * Interp.pow3In.apply(fin) * ofRand;
            float l = w * 5.0F * rand2.random(1.0F, 2.0F) * Interp.pow3Out.apply(fin);
            float w2 = w * Mathf.slope(fin);
            float trns = spikeLength * ofRand + rand2.random(60.0F, 110.0F);
            Vec2 v = Tmp.v3.trns(b.rotation(), fin * fin * trns, of).add(x, y);
            UnityDrawf.diamond(v.x, v.y, w2, l, b.rotation());
        }

    }

    void drawEndVoid(Bullet b, float x, float y, float width) {
        float spikeLength = this.endLength;
        float tx1 = Angles.trnsx(b.rotation() + 90.0F, width);
        float ty1 = Angles.trnsy(b.rotation() + 90.0F, width);
        float tx2 = Angles.trnsx(b.rotation(), spikeLength) + x;
        float ty2 = Angles.trnsy(b.rotation(), spikeLength) + y;
        Fill.tri(x + tx1, y + ty1, x - tx1, y - ty1, tx2, ty2);
        rand.setSeed((long)b.id * 9999L + 1411L);
        float time = Time.time * 4.0F;

        for(int i = 0; i < 22; ++i) {
            float d = rand.random(40.0F, 180.0F);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = (time + timeOffset) % d / d;
            float fr = 1.0F - fin;
            rand2.setSeed((long)timeSeed);
            float w = rand2.random(width / 5.0F, width / 1.75F);
            float fr2 = Mathf.lerp(fr, 1.0F, rand2.random(0.1F, 0.6F));
            float w2 = w * Mathf.curve(fr, 0.0F, 0.8F) * Mathf.curve(fin, 0.0F, 0.2F);
            float l = w * 3.0F * rand2.random(0.8F, 2.0F) * Interp.pow3Out.apply(fin);
            float pos1 = rand2.range(1.0F);
            float pos = pos1 * Math.max(width - w2 / 2.05F, 0.0F);
            float sclL = 1.0F + Math.abs(pos1) * 0.25F;
            float trns = rand2.random(220.0F, 380.0F);
            float offset = (1.0F - Math.abs(pos1)) * spikeLength + rand2.random(-34.0F, 4.0F) - trns * 0.2F * 0.2F;
            Vec2 v = Tmp.v3.trns(b.rotation(), fin * fin * sclL * trns + offset, pos * fr2).add(x, y);
            UnityDrawf.diamond(v.x + Mathf.range(6.0F) * fin, v.y + Mathf.range(6.0F) * fin, w2, l, b.rotation());
        }

    }

    void drawLightning(Bullet b, int seed, float fin, float fout, float width) {
        rand2.setSeed((long)seed * 2L + 856387231L);
        float time = b.time / 3.0F;
        float f2 = time % 1.0F;
        int timeSeed = (int)time;
        float pos = 0.0F;
        float max = 0.0F;
        float pos2 = 0.0F;
        float max2 = 0.0F;
        float drift = rand2.range(1.0F);
        float length = this.length + this.endLength * (1.0F - Math.abs(drift));

        for(int i = 0; i < ltmp.length; ++i) {
            rand2.setSeed((long)seed * 9999L + (long)(timeSeed + ltmp.length - i));
            float r = rand2.range(1.5F);
            rand2.setSeed((long)seed * 9999L + (long)(timeSeed + ltmp.length - (i + 1)));
            float r2 = rand2.range(1.5F);
            pos += r;
            pos2 += r2;
            ltmp[i] = pos;
            ltmp2[i] = pos2;
        }

        float drft2 = drift > 0.0F ? 1.0F + drift : 1.0F + drift / 2.0F;
        float delta = pos / (float)ltmp.length * drft2;
        float delta2 = pos2 / (float)ltmp.length * drft2;

        for(int i = 0; i < ltmp.length; ++i) {
            float v = ltmp[i] - delta * (float)i;
            float v2 = ltmp2[i] - delta2 * (float)i;
            ltmp[i] = v;
            ltmp2[i] = v2;
            max = Math.max(max, Math.abs(v));
            max2 = Math.max(max2, Math.abs(v2));
        }

        float lx = b.x;
        float ly = b.y;
        Tmp.c1.lerp(lightningColors, Mathf.curve(fin, 0.01F, 0.7F));
        Draw.color(Tmp.c1);
        Lines.stroke(Mathf.clamp(1.0F - fin, 0.0F, 0.4F) * 11.0F * fout);

        for(int i = 1; i < ltmp.length; ++i) {
            float v = ltmp[i] / max * width;
            float v2 = ltmp2[i] / max2 * width;
            float w = Mathf.lerp(v, v2, 1.0F - f2);
            Vec2 nv = Tmp.v1.trns(b.rotation(), (float)i / ((float)ltmp.length - 1.0F) * length, w).add(b);
            Lines.line(lx, ly, nv.x, nv.y, false);
            lx = nv.x;
            ly = nv.y;
        }

    }

    static {
        lightningColors = new Color[]{Color.white, UnityPal.scarColor, Color.black};
        int sign = 1;

        for(int i = 0; i < 24; ++i) {
            int id = i * 4;
            float de = 23.0F;
            float f = Interp.circleIn.apply((float)i / de);
            float w = Interp.circleOut.apply(f);

            for(int s : Mathf.signs) {
                shape[id] = w * (float)s * (float)sign;
                shape[id + 1] = f;
                id += 2;
            }

            sign = (int)((float)sign * -1.0F);
        }

    }
}
