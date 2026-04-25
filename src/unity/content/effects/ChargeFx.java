package unity.content.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import unity.content.Trails;
import unity.entities.effects.CustomStateEffect;
import unity.entities.effects.ParentEffect;
import unity.graphics.MultiTrail;
import unity.graphics.TexturedTrail;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.MathU;
import unity.util.Utils;

public class ChargeFx {
    private static final Color tmpCol = new Color();

    public static Effect greenLaserChargeSmallParent = new ParentEffect(40.0F, 100.0F, (e) -> {
        Draw.color(Pal.heal);
        Lines.stroke(e.fin() * 2.0F);
        Lines.circle(e.x, e.y, e.fout() * 50.0F);
    });

    public static Effect greenLaserChargeParent = new ParentEffect(80.0F, 100.0F, (e) -> {
        Draw.color(Pal.heal);
        Lines.stroke(e.fin() * 2.0F);
        Lines.circle(e.x, e.y, 4.0F + e.fout() * 100.0F);
        Fill.circle(e.x, e.y, e.fin() * 20.0F);
        Angles.randLenVectors((long)e.id, 20, 40.0F * e.fout(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fin() * 5.0F);
            Drawf.light(e.x + x, e.y + y, e.fin() * 15.0F, Pal.heal, 0.7F);
        });
        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 10.0F);
        Drawf.light(e.x, e.y, e.fin() * 20.0F, Pal.heal, 0.7F);
    });

    public static Effect sagittariusCharge = (new Effect(120.0F, (e) -> {
        float size = e.fin() * 15.0F;
        Draw.color(Pal.heal);
        Fill.circle(e.x, e.y, size);
        MathU.randLenVectors((long)e.id * 9999L, 15, e.fout(), 0.5F, 0.6F, 0.2F, (fx) -> fx * fx * fx * 90.0F, (ex, ey, fin) -> {
            float fout = 1.0F - fin;
            if ((double)fin < 0.9999) {
                Fill.circle(ex + e.x, ey + e.y, fout * 11.0F);
            }

        });
        float f = Mathf.curve(e.fin(), 0.4F);
        if (f > 1.0E-4F) {
            for(int s : Mathf.signs) {
                Drawf.tri(e.x, e.y, Interp.pow2Out.apply(f) * 15.0F * 1.22F, f * f * 80.0F, e.rotation + 90.0F * (float)s);
            }
        }

        Draw.color(Color.white);
        Fill.circle(e.x, e.y, size * 0.5F);
    })).followParent(true).rotWithParent(true);

    public static Effect tenmeikiriChargeEffect = new ParentEffect(40.0F, (e) -> Angles.randLenVectors((long)e.id, 2, 10.0F, 90.0F, (x, y) -> {
        float angle = Mathf.angle(x, y);
        Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Lines.stroke(1.5F);
        Lines.lineAngleCenter(e.x + x * e.fout(), e.y + y * e.fout(), angle, e.fslope() * 13.0F);
    }));

    public static Effect tenmeikiriChargeBegin = new ParentEffect(158.0F, (e) -> {
        Color[] colors = new Color[]{UnityPal.scarColor, UnityPal.endColor, Color.white};

        for(int ii = 0; ii < 3; ++ii) {
            float s = (float)(3 - ii) / 3.0F;
            float width = Mathf.clamp(e.time / 80.0F) * (20.0F + Mathf.absin(Time.time + (float)ii * 1.4F, 1.1F, 7.0F)) * s;
            float length = e.fin() * (100.0F + Mathf.absin(Time.time + (float)ii * 1.4F, 1.1F, 11.0F)) * s;
            Draw.color(colors[ii]);

            for(int i : Mathf.signs) {
                float rotation = e.rotation + (float)i * 90.0F;
                Drawf.tri(e.x, e.y, width, length * 0.5F, rotation);
            }

            Drawf.tri(e.x, e.y, width, length * 1.25F, e.rotation);
        }
    });

    public static Effect devourerChargeEffect = new ParentEffect(41.0F, (e) -> {
        Color[] colors = new Color[]{UnityPal.scarColor, UnityPal.endColor, Color.white};

        for(int i = 0; i < colors.length; ++i) {
            Draw.color(colors[i]);
            float scl = ((float)colors.length - (float)i / 1.25F) * (17.0F / (float)colors.length);
            float width = 35.0F / (1.0F + (float)i / (float)Math.PI) * e.fin();
            float spikeIn = e.fslope() * scl * 1.5F;
            UnityDrawf.shiningCircle(e.id * 241, Time.time + (float)i * 3.0F, e.x, e.y, scl * e.fin(), 9, 12.0F, width, spikeIn);
        }
    });

    public static Effect oppressionCharge = (new Effect(300.0F, 5060.0F, (e) -> {
        Rand r = Utils.seedr;
        Rand r2 = Utils.seedr2;
        Rand r3 = Utils.seedr3;
        r.setSeed((long)e.id * 9999L);
        float off = 140.0F / e.lifetime;
        float off2 = 70.0F / e.lifetime;
        float fin1 = e.time >= 150.0F ? 1.0F : e.time / 150.0F;
        float fin2 = e.time >= 60.0F ? 1.0F : e.time / 60.0F;
        float time = Time.time;
        Draw.color(UnityPal.scarColor);

        for(int i = 0; i < 11; ++i) {
            float f = (float)i / 10.0F * off2;
            float cf = Mathf.curve(e.fin(), f, 1.0F - off2 + f);
            float cfo = 1.0F - cf;
            float rot = e.rotation + (r.nextFloat() - r.nextFloat()) * 6.0F;
            float len = r.random(75.0F, 210.0F) * Interp.pow2Out.apply(MathU.slope(cf, 0.75F));
            float wid = len / 15.0F * cf * 2.0F * r.random(0.8F, 1.2F);
            float trns = r.random(2530.0F - len * 2.0F) + len;
            if (!(cf <= 0.0F) && !(cf >= 1.0F)) {
                Vec2 v = Tmp.v1.trns(rot, trns * Interp.pow3In.apply(cfo)).add(e.x, e.y);
                UnityDrawf.diamond(v.x + Mathf.range(4.0F) * cf, v.y + Mathf.range(4.0F) * cf, wid, len, rot);
            }
        }

        if (e.time > 145.0F) {
            float fin3 = e.time - 145.0F >= 140.0F ? 1.0F : (e.time - 145.0F) / 140.0F;
            r3.setSeed((long)e.id * 9999L + 781L);
            float spikef = Mathf.clamp((e.time - 145.0F) / 20.0F, 0.0F, 13.0F);
            int spikei = Mathf.ceil(spikef);

            for(int i = 0; i < spikei; ++i) {
                float spikem = !(spikef >= 13.0F) && i >= spikei - 1 ? spikef % 1.0F : 1.0F;
                float d = r3.random(25.0F, 45.0F);
                float timeOffset = r3.random(d);
                float f = (time + timeOffset) % d / d;
                float fo = 1.0F - f;
                int timeSeed = Mathf.floor((time + timeOffset) / d) + r3.nextInt();
                float offs = 0.33F;
                float lt = f < offs ? Interp.pow2In.apply(f / offs) : 1.0F - (f - offs) / (1.0F - offs);
                r2.setSeed((long)timeSeed);
                float rot = r2.random(360.0F) + r2.range(5.0F) * f;
                float trns = r2.random(8.0F, 13.0F) + r2.random(5.0F, 10.0F) * e.fin();
                float w = r2.random(17.0F, 30.0F) + r2.random(8.0F) * fin3 * Mathf.curve(fo, 0.0F, 0.5F);
                float l = r2.random(75.0F, 180.0F) * lt * spikem;
                Tmp.v1.trns(rot, trns).add(e.x, e.y);
                UnityDrawf.diamond(Tmp.v1.x, Tmp.v1.y, w, l, 0.4F, rot);
            }

            float fin4 = (e.time - 145.0F) / (e.lifetime - 145.0F);
            UnityDrawf.diamond(e.x, e.y, 17.0F * Interp.pow2Out.apply(Mathf.curve(fin4, 0.0F, 0.2F)), (160.0F + Mathf.absin(8.0F, 6.0F)) * Interp.pow2.apply(fin4), e.rotation + 90.0F);
        }

        for(int i = 0; i < 35; ++i) {
            float d = r.random(10.0F, 30.0F);
            float timeOffset = r.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + r.nextInt();
            float f = (time + timeOffset) % d / d;
            float fo = 1.0F - f;
            float trv = 1.0F - (f < 0.75F ? Interp.pow3Out.apply(f / 0.75F) * 0.75F : Interp.pow2In.apply((f - 0.75F) / 0.25F) * 0.25F + 0.75F);
            r2.setSeed((long)timeSeed);
            float rot = r2.random(360.0F);
            float trns = (r2.random(15.0F, 65.0F) + r2.random(15.0F, 75.0F) * e.fin()) * trv;
            float trns2 = r2.random(200.0F, 900.0F) * fo * (1.0F - fin1);
            float rad = (r2.random(10.0F, 22.0F) + 11.0F * e.fin()) * fin2 * Interp.pow2Out.apply(MathU.slope(f, 0.75F));
            if (trns2 > 0.0F) {
                Tmp.v1.trns(e.rotation + r2.range(4.0F), trns2).add(e.x, e.y);
            } else {
                Tmp.v1.set(e.x, e.y);
            }

            Draw.color(UnityPal.scarColor, Color.black, Mathf.curve(f, 0.35F, 0.75F));
            Vec2 v = Tmp.v2.trns(rot, trns).add(Tmp.v1);
            Fill.square(v.x, v.y, rad, 45.0F);
        }

        Draw.color(UnityPal.scarColor);

        for(int i = 0; i < 22; ++i) {
            float f = (float)i / 21.0F * off;
            float cf = Mathf.curve(e.fin(), f, 1.0F - off + f);
            float cfo = 1.0F - cf;
            float rot = e.rotation + (r.nextFloat() - r.nextFloat()) * 20.0F;
            float len = r.random(300.0F, 800.0F);
            float trns = r.random(2530.0F - len) * cfo * cfo;
            if (!(cf <= 0.0F) && !(cf >= 1.0F)) {
                Vec2 v = Tmp.v1.trns(rot, trns).add(e.x, e.y);
                Lines.stroke(3.0F);
                Lines.lineAngle(v.x, v.y, rot, len * Mathf.slope(cfo * cfo), false);
            }
        }

        float t = e.time < 225.0F ? 0.0F : Mathf.clamp((e.time - 225.0F) / 30.0F);
        float length = Interp.pow3.apply(Mathf.clamp(e.time / 20.0F)) * 2530.0F;
        Draw.color(UnityPal.scarColor, Color.black, t);
        Lines.stroke(5.0F);
        Lines.lineAngle(e.x, e.y, e.rotation, length);
        if (t > 0.0F) {
            r3.setSeed((long)e.id * 9999L + 613L);
            float dr = 225.0F;
            float partf = Mathf.clamp((e.time - dr) / (e.lifetime - dr)) * 9.0F;
            int parti = Mathf.ceil(partf);

            for(int j = 0; j < parti; ++j) {
                float partm = !(partf >= 9.0F) && j >= parti - 1 ? partf % 1.0F : 1.0F;

                for(int i = 0; i < 9; ++i) {
                    float d = r3.random(7.0F, 11.0F);
                    float timeOffset = r3.random(d);
                    int timeSeed = Mathf.floor((time + timeOffset) / d) + r3.nextInt();
                    float f = (time + timeOffset) % d / d;
                    r2.setSeed((long)timeSeed);
                    float l = r2.random(100.0F, 200.0F) * Interp.pow2Out.apply(Mathf.curve(f, 0.0F, 0.5F)) * partm;
                    float w = r2.random(9.0F, 19.0F) * MathU.slope(f, 0.8F) * partm * t;
                    float trns = r2.random(2530.0F - l * 2.0F) + l + r2.range(3.0F) * f;
                    float of = (r2.nextFloat() - r2.nextFloat()) * 35.0F * Interp.pow3Out.apply(1.0F - f) * (0.5F + t * 0.5F);
                    Tmp.v1.trns(e.rotation, trns, of).add(e.x, e.y);
                    Draw.color(UnityPal.scarColor, Color.black, Mathf.curve(f, 0.2F, 0.75F));
                    UnityDrawf.diamond(Tmp.v1.x, Tmp.v1.y, w, l, e.rotation);
                }
            }
        }

        if (e.time < 225.0F) {
            float t2 = Mathf.clamp((225.0F - e.time) / 30.0F);
            r3.setSeed((long)e.id * 9999L + 613L);
            Draw.color(UnityPal.scarColor);

            for(int i = 0; i < 30; ++i) {
                float d = r3.random(18.0F, 24.0F);
                float timeOffset = r3.random(d);
                int timeSeed = Mathf.floor((time + timeOffset) / d) + r3.nextInt();
                float f = (time + timeOffset) % d / d;
                r2.setSeed((long)timeSeed);
                float trns = r2.random(length) + r2.range(2.0F) * f;
                float of = (r2.nextFloat() - r2.nextFloat()) * 65.0F * Interp.pow3In.apply(f) * (0.5F + t2 * 0.5F);
                float scl = r2.random(3.0F, 8.0F) * t2 * MathU.slope(f, 0.25F);
                Tmp.v1.trns(e.rotation, trns, of).add(e.x, e.y);
                Fill.square(Tmp.v1.x, Tmp.v1.y, scl, 45.0F);
            }
        }

    })).followParent(true).rotWithParent(true);

    public static Effect wBosonChargeBeginEffect = new Effect(38.0F, (e) -> {
        Draw.color(UnityPal.lightEffect, Pal.lancerLaser, e.fin());
        Fill.circle(e.x, e.y, 3.0F + e.fin() * 6.0F);
        Draw.color(Color.white);
        Fill.circle(e.x, e.y, 1.75F + e.fin() * 5.75F);
    });

    public static Effect wBosonChargeEffect = new Effect(24.0F, (e) -> {
        Draw.color(UnityPal.lightEffect, Pal.lancerLaser, e.fin());
        Lines.stroke(1.5F);
        Angles.randLenVectors((long)e.id, 2, (1.0F - e.finpow()) * 50.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, a, Mathf.sin(e.finpow() * 3.0F, 1.0F, 8.0F) + 1.5F);
            Fill.circle(e.x + x, e.y + y, 2.0F + e.fin() * 1.75F);
        });
    });

    public static Effect ephmeronCharge = new Effect(80.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        UnityDrawf.shiningCircle(e.id, Time.time, e.x, e.y, e.fin() * 9.5F, 6, 25.0F, 20.0F, 3.0F * e.fin());
        Draw.color(Color.white);
        UnityDrawf.shiningCircle(e.id, Time.time, e.x, e.y, e.fin() * 7.5F, 6, 25.0F, 20.0F, 2.5F * e.fin());
    });

    public static Effect tendenceCharge = (new CustomStateEffect(() -> Pools.obtain(State.class, State::new), 40.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof MultiTrail.TrailHold[]) {
            MultiTrail.TrailHold[] data = (MultiTrail.TrailHold[])data$temp;
            Draw.color(UnityPal.monolith, UnityPal.monolithLight, e.fin());
            Angles.randLenVectors((long)e.id, 8, 8.0F + e.foutpow() * 32.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.5F + e.fin() * 2.5F));
            Draw.color();

            for(MultiTrail.TrailHold hold : data) {
                Tmp.v1.set(hold.x, hold.y);
                Tmp.v2.trns(Tmp.v1.angle() - 90.0F, Mathf.sin(hold.width * 2.6F, hold.width * 8.0F * Interp.pow2Out.apply(e.fslope())));
                Tmp.v1.scl(e.foutpowdown()).add(Tmp.v2).add(e.x, e.y);
                float w = hold.width * e.fin();
                if (!Vars.state.isPaused()) {
                    hold.trail.update(Tmp.v1.x, Tmp.v1.y, w);
                }

                tmpCol.set(UnityPal.monolith).lerp(UnityPal.monolithLight, e.finpowdown());
                hold.trail.drawCap(tmpCol, w);
                hold.trail.draw(tmpCol, w);
            }

            Lines.stroke(Mathf.curve(e.fin(), 0.5F) * 1.4F, UnityPal.monolithLight);
            Lines.circle(e.x, e.y, e.fout() * 64.0F);
        }
    }) {
        protected EffectState inst(float x, float y, float rotation, Color color, Object data) {
            MultiTrail.TrailHold[] trails = new MultiTrail.TrailHold[12];

            for(int i = 0; i < trails.length; ++i) {
                Tmp.v1.trns(Mathf.random(360.0F), Mathf.random(24.0F, 64.0F));
                trails[i] = new MultiTrail.TrailHold((Trail)Utils.with(Trails.soul(26), (t) -> {
                    Trail tr$temp = t.trails[t.trails.length - 1].trail;
                    if (tr$temp instanceof TexturedTrail) {
                        TexturedTrail tr = (TexturedTrail)tr$temp;
                        tr.trailChance = 0.1F;
                    }

                }), Tmp.v1.x, Tmp.v1.y, Mathf.random(1.0F, 2.0F));
            }

            EffectState state = super.inst(x, y, rotation, color, data);
            state.data = trails;
            return state;
        }
    }).followParent(true);

    public static class State extends EffectState {
        public void remove() {
            Object var2 = this.data;
            if (var2 instanceof MultiTrail.TrailHold[]) {
                MultiTrail.TrailHold[] data = (MultiTrail.TrailHold[])var2;

                for(MultiTrail.TrailHold trail : data) {
                    Fx.trailFade.at(this.x, this.y, trail.width, UnityPal.monolithLight, trail.trail.copy());
                }
            }

            super.remove();
        }
    }
}
