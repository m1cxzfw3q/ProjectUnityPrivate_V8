package unity.content.effects;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.gen.Float2;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class HitFx {
    public static Effect electronHit = new Effect(12.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Lines.stroke(e.fout() * 3.0F);
        Lines.circle(e.x, e.y, e.fin() * 90.0F);
        Angles.randLenVectors((long)e.id, 7, e.finpow() * 45.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 4.0F, e.fin() * 120.0F + e.rotation + a);
        });
    });
    public static Effect protonHit = new Effect(20.0F, (e) -> {
        Draw.color(Pal.lancerLaser, Color.valueOf("4787ff00"), e.fin());
        Lines.stroke(e.fout() * 4.0F);
        Lines.circle(e.x, e.y, e.fin() * 150.0F);
        Angles.randLenVectors((long)e.id, 12, e.finpow() * 64.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 6.0F, e.fin() * 135.0F + e.rotation + a);
        });
    });
    public static Effect neutronHit = new Effect(28.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Angles.randLenVectors((long)e.id, 7, e.finpow() * 50.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 5.0F, e.fin() * 120.0F + e.rotation + a);
        });
    });
    public static Effect wBosonDecayHitEffect = new Effect(13.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Lines.stroke(0.5F + e.fout());
        Angles.randLenVectors((long)e.id, 17, e.finpow() * 20.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, a, e.fout() * 8.0F);
        });
    });
    public static Effect philinopsisEmpZap = new Effect(50.0F, 100.0F, (e) -> {
        float rad = 68.0F;
        e.scaled(7.0F, (b) -> {
            Draw.color(Pal.heal, b.fout());
            UnityDrawf.arcFill(e.x, e.y, rad, 36.0F, e.rotation);
        });
        Draw.color(Pal.heal);
        Lines.stroke(e.fout() * 3.0F);
        UnityDrawf.arcLine(e.x, e.y, rad, 36.0F, e.rotation);
        Drawf.tri(e.x + Angles.trnsx(e.rotation, rad), e.y + Angles.trnsy(e.rotation, rad), 6.0F, 50.0F * e.fout(), e.rotation);
    });
    public static Effect philinopsisEmpHit = new Effect(50.0F, 100.0F, (e) -> {
        float rad = 124.0F;
        e.scaled(7.0F, (b) -> {
            Draw.color(Pal.heal, b.fout());
            Fill.circle(e.x, e.y, rad);
        });
        Draw.color(Pal.heal);
        Lines.stroke(e.fout() * 3.0F);
        Lines.circle(e.x, e.y, rad);
        int points = 10;
        float offset = Mathf.randomSeed((long)e.id, 360.0F);

        for(int i = 0; i < points; ++i) {
            float angle = (float)i * 360.0F / (float)points + offset;
            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6.0F, 50.0F * e.fout(), angle);
        }

        Fill.circle(e.x, e.y, 12.0F * e.fout());
        Draw.color();
        Fill.circle(e.x, e.y, 6.0F * e.fout());
        Drawf.light(e.x, e.y, rad * 1.6F, Pal.heal, e.fout());
    });
    public static Effect lightHitLarge = new Effect(15.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Lines.stroke(0.5F + e.fout());
        Angles.randLenVectors((long)e.id, 17, e.finpow() * 50.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, a, e.fout() * 8.0F);
        });
        Lines.stroke(0.5F + e.fout() * 1.2F);
        Lines.circle(e.x, e.y, e.finpow() * 30.0F);
    });
    public static Effect orbHit = new Effect(12.0F, (e) -> {
        Draw.color(Pal.surge);
        Lines.stroke(e.fout() * 1.5F);
        Angles.randLenVectors((long)e.id, 8, e.finpow() * 17.0F, e.rotation, 360.0F, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4.0F + 1.0F);
        });
    });
    public static Effect plasmaTriangleHit = new Effect(30.0F, (e) -> {
        Draw.color(Pal.surge);
        Lines.stroke(e.fout() * 2.8F);
        Lines.circle(e.x, e.y, e.fin() * 60.0F);
    });
    public static Effect scarHitSmall = new Effect(14.0F, (e) -> {
        Draw.color(Color.white, UnityPal.scarColor, e.fin());
        e.scaled(7.0F, (s) -> {
            Lines.stroke(0.5F + s.fout());
            Lines.circle(e.x, e.y, s.fin() * 5.0F);
        });
        Lines.stroke(0.5F + e.fout());
        Angles.randLenVectors((long)e.id, 5, e.fin() * 15.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.0F + 1.0F));
    });
    public static Effect scarRailHit = new Effect(18.0F, (e) -> {
        for(int i = 0; i < 2; ++i) {
            int sign = Mathf.signs[i];
            Draw.color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 10.0F * e.fout(), 60.0F, e.rotation + 90.0F + 90.0F * (float)sign);
            Draw.color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(10.0F * e.fout() - 4.0F, 0.0F), 56.0F, e.rotation + 90.0F + 90.0F * (float)sign);
        }

    });
    public static Effect coloredHitSmall = new Effect(14.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        e.scaled(7.0F, (s) -> {
            Lines.stroke(0.5F + s.fout());
            Lines.circle(e.x, e.y, s.fin() * 5.0F);
        });
        Lines.stroke(0.5F + e.fout());
        Angles.randLenVectors((long)e.id, 5, e.fin() * 15.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.0F + 1.0F));
    });
    public static Effect coloredHitLarge = new Effect(21.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        e.scaled(8.0F, (s) -> {
            Lines.stroke(0.5F + s.fout());
            Lines.circle(e.x, e.y, s.fin() * 11.0F);
        });
        Lines.stroke(0.5F + e.fout());
        Angles.randLenVectors((long)e.id, 6, e.fin() * 35.0F, e.rotation + 180.0F, 45.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 7.0F + 1.0F));
    });
    public static Effect empHit = new Effect(50.0F, 100.0F, (e) -> {
        float rad = 70.0F;
        e.scaled(7.0F, (b) -> {
            Draw.color(Pal.heal, b.fout());
            Fill.circle(e.x, e.y, rad);
        });
        Draw.color(Pal.heal);
        Lines.stroke(e.fout() * 3.0F);
        Lines.circle(e.x, e.y, rad);
        int points = 10;
        float offset = Mathf.randomSeed((long)e.id, 360.0F);

        for(int i = 0; i < points; ++i) {
            float angle = (float)i * 360.0F / (float)points + offset;
            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6.0F, 50.0F * e.fout(), angle);
        }

        Fill.circle(e.x, e.y, 12.0F * e.fout());
        Draw.color();
        Fill.circle(e.x, e.y, 6.0F * e.fout());
        Drawf.light(e.x, e.y, rad * 1.6F, Pal.heal, e.fout());
    });
    public static Effect plagueLargeHit = new Effect(80.0F, (e) -> {
        float fOffset = 0.1F;
        float fOffsetA = 0.05F;
        Rand r = Utils.seedr;
        r.setSeed((long)e.id * 99999L);

        for(int i = 0; i < 9; ++i) {
            float f = r.nextFloat() * fOffset;
            float fin = Mathf.curve(e.fin(), f, f + 1.0F - fOffset);
            float ex = Interp.pow3Out.apply(fin) * 35.0F * r.nextFloat();
            Vec2 v = Tmp.v1.trns(r.random(360.0F), ex);
            Draw.color(Color.gray, Color.darkGray, fin);
            Fill.circle(e.x + v.x, e.y + v.y, 9.0F * Mathf.curve(1.0F - fin, 0.0F, 0.7F));
            Fill.circle(e.x + v.x * 0.5F, e.y + v.y * 0.5F, 5.0F * Mathf.curve(1.0F - fin, 0.0F, 0.7F));
        }

        e.scaled(40.0F, (s) -> {
            Draw.color(UnityPal.plague);

            for(int i = 0; i < 6; ++i) {
                float f = r.nextFloat() * fOffsetA;
                float fin = Mathf.curve(s.fin(), f, f + 1.0F - fOffsetA);
                float ifin = Interp.pow3Out.apply(fin);
                float scl = r.nextFloat();
                float ex = ifin * 50.0F * scl;
                float slope = Interp.pow3Out.apply(Mathf.slope(ifin));
                Vec2 v = Tmp.v1.trns(r.random(360.0F), ex, Mathf.sin(ifin * (float)Math.PI, 1.0F / r.random(1.0F, 5.0F), slope * scl * r.random(6.0F, 11.0F))).add(e.x, e.y);
                Fill.circle(v.x, v.y, 6.0F * s.fout());
            }

        });
        e.scaled(15.0F, (s) -> {
            Draw.color(UnityPal.plague);
            Lines.stroke(2.0F * s.fout());
            Lines.circle(e.x, e.y, 50.0F * s.finpow());
        });
    });
    public static Effect eclipseHit = new Effect(15.0F, (e) -> {
        Draw.color(Color.valueOf("c2ebff"), Color.valueOf("68c0ff"), e.fin());
        Angles.randLenVectors((long)e.id, 4, e.finpow() * 28.0F, (x, y) -> Fill.poly(e.x + x, e.y + y, 4, 3.0F + e.fout() * 9.0F, 0.0F));
        Draw.color(Color.white, Pal.lancerLaser, e.fin());
        Lines.stroke(1.5F * e.fout());
        Angles.randLenVectors((long)e.id * 2L, 7, e.finpow() * 42.0F, (x, y) -> {
            float a = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, a, e.fout() * 8.0F + 1.5F);
        });
    });
    public static Effect tenmeikiriTipHit = new Effect(27.0F, (e) -> Angles.randLenVectors((long)e.id, 8, 90.0F * e.fin(), e.rotation, 80.0F, (x, y) -> {
        float angle = Mathf.angle(x, y);
        Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Lines.stroke(1.5F);
        Lines.lineAngleCenter(e.x + x, e.y + y, angle, e.fslope() * 13.0F);
    }));
    public static Effect voidHit = new Effect(20.0F, (e) -> {
        Draw.color(Color.black);
        Angles.randLenVectors((long)e.id, 7, e.finpow() * 15.0F, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5.0F);
            Fill.circle(e.x + x / 2.0F, e.y + y / 2.0F, e.fout() * 3.0F);
        });
    });
    public static Effect voidHitBig = new Effect(30.0F, (e) -> {
        Draw.color(Color.black);
        e.scaled(e.lifetime / 2.0F, (s) -> {
            for(int i = 0; i < 3; ++i) {
                float f = Mathf.lerp(10.0F, 5.0F, (float)i / 2.0F);
                Draw.alpha(Mathf.lerp(0.45F, 1.0F, (float)i / 2.0F * ((float)i / 2.0F)));
                Drawf.tri(e.x, e.y, f * 1.22F * s.fout(Interp.pow5Out), 1.0F + 7.0F * f * s.fin(Interp.pow5Out), e.rotation);
                Drawf.tri(e.x, e.y, f * 1.22F * s.fout(Interp.pow5Out), 3.0F * f * s.fout(Interp.pow5Out), e.rotation - 180.0F);
            }

        });
        if (e.fin() > 0.45F) {
            float l2 = Mathf.curve(e.fin(), 0.45F, 1.0F);
            Angles.randLenVectors((long)e.id, 20, 35.0F * Interp.pow2Out.apply(l2), e.rotation, 45.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 4.0F * Interp.pow2Out.apply(1.0F - l2)));
        }

    });
    public static Effect endHitRedSmall = new Effect(15.0F, (e) -> {
        e.scaled(e.lifetime / 2.0F, (s) -> {
            Draw.color(UnityPal.scarColor, UnityPal.endColor, s.fin());
            Lines.stroke(2.0F * s.fout());
            Lines.circle(e.x, e.y, 10.0F * s.fin());
        });
        Draw.color(UnityPal.endColor, UnityPal.scarColor, e.fin());
        Angles.randLenVectors((long)e.id, 7, e.fin(Interp.pow3Out) * 20.0F, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.stroke(e.fout());
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout(Interp.pow5In) * 12.0F);
        });
    });
    public static Effect endHitRedSmoke = new Effect(25.0F, (e) -> {
        Draw.color(UnityPal.scarColor, Color.darkGray, Color.gray, e.fin());
        Angles.randLenVectors((long)e.id * 451L, 7, e.fin(Interp.pow3Out) * 35.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 4.0F));
        e.scaled(15.0F, (s) -> {
            Draw.color(UnityPal.endColor, UnityPal.scarColor, e.fin());
            Angles.randLenVectors((long)e.id, 7, e.fin(Interp.pow3Out) * 20.0F, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.stroke(s.fout());
                Lines.lineAngle(e.x + x, e.y + y, ang, s.fout(Interp.pow5In) * 12.0F);
            });
        });
    });
    public static Effect endHitRedBig = new Effect(15.0F, (e) -> {
        Draw.color(UnityPal.endColor, UnityPal.scarColor, e.fin());
        Angles.randLenVectors((long)e.id, 7, e.fin(Interp.pow3Out) * 45.0F, e.rotation, 45.0F, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.stroke(e.fout() * 2.0F);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout(Interp.pow3In) * 24.0F);
        });
    });
    public static Effect endDeathLaserHit = new Effect(35.0F, (e) -> {
        if (e.data instanceof Float) {
            Rand r = Utils.seedr;
            float rad = Math.max(20.0F, (Float)e.data);
            float maxOffset = 0.14285715F;
            float maxOffset2 = 0.33333334F;
            int smokeAmount = 9 + (int)(rad / 8.0F);
            int sparkAmount = 2 + (int)(rad / 25.0F);
            r.setSeed((long)e.id * 9999L);

            for(int i = 0; i < smokeAmount; ++i) {
                float cf = (float)i / ((float)smokeAmount - 1.0F) * maxOffset;
                float nf = Mathf.curve(e.fin(), cf, 1.0F + cf - maxOffset);
                Draw.color(UnityPal.scarColor, Color.darkGray, Color.gray, nf);
                float rot = e.rotation + r.range(4.0F);
                float f = Interp.pow3In.apply(nf);
                float w = r.range(rad) * Interp.circleOut.apply(f);
                float l = rad * r.random(1.5F, 3.25F) * f;
                Vec2 v = Tmp.v1.trns(rot, l, w).add(e.x, e.y);
                Fill.circle(v.x, v.y, (9.0F + rad / 7.0F) * (1.0F - nf));
            }

            e.scaled(15.0F, (s) -> {
                Draw.color(UnityPal.scarColor, Color.white, s.fin());
                Lines.stroke(2.0F);

                for(int i = 0; i < sparkAmount; ++i) {
                    float cf = (float)i / ((float)sparkAmount - 1.0F) * maxOffset2;
                    float nf = Mathf.curve(s.fin(), cf, 1.0F + cf - maxOffset2);
                    float f = Interp.pow2Out.apply(nf);
                    float range = (float)Mathf.sign(r.chance((double)0.5F)) * r.random(60.0F, 93.0F);
                    float rot = e.rotation + range;
                    Vec2 v = Tmp.v1.trns(rot, f * (rad / 2.0F) * r.random(0.5F, 1.2F) + 0.001F);
                    Lines.lineAngle(v.x + e.x, v.y + e.y, v.angle(), (7.0F + rad / 12.0F) * (1.0F - f), false);
                }

            });
        }
    });
    public static Effect endHitRail = new Effect(25.0F, (e) -> {
        e.scaled(15.0F, (s) -> {
            Draw.color(UnityPal.endColor, UnityPal.scarColor, e.fin());
            Angles.randLenVectors((long)e.id, 7, s.fin(Interp.pow3Out) * 45.0F, e.rotation, 47.0F, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.stroke(s.fout() * 2.0F);
                Lines.lineAngle(e.x + x, e.y + y, ang, s.fout(Interp.pow3In) * 24.0F);
            });
        });
        float scl = 0.3F;
        int spikes = Mathf.randomSeed((long)e.id * 13L, 3, 5);
        Draw.color(UnityPal.scarColor);

        for(int i = 0; i < spikes; ++i) {
            float fin = Mathf.curve(e.fin(), (float)i / (float)spikes * scl, ((float)i + 1.0F) / (float)spikes * scl + (1.0F - scl));
            float fin2 = Mathf.curve(fin, 0.0F, 0.3F);
            float fout = 1.0F - fin;
            float angle = Mathf.randomSeed((long)e.id * 53L + (long)i * 31L, -25.0F, 25.0F) + e.rotation;
            Drawf.tri(e.x, e.y, fout * 20.0F, fin2 * (80.0F + Mathf.randomSeed((long)(e.id + i) * 73L, 40.0F)), angle);
            Drawf.tri(e.x, e.y, fout * 20.0F, fin2 * 20.0F, angle + 180.0F);
        }

    });
    public static Effect endFlash = (new Effect(7.0F, (e) -> {
        Tmp.c1.set(UnityPal.scarColor).a(e.fout());
        Draw.color(UnityPal.scarColor);
        Draw.blend(Blending.additive);

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(e.x, e.y, 5.0F * e.fout(Interp.pow3In), 40.0F * e.fout(), 90.0F * (float)i);
        }

        Fill.light(e.x, e.y, 15, 16.0F, Tmp.c1, Color.clear);
        Draw.blend();
    })).layer(111.0F);
    public static Effect hitMonolithLaser = new Effect(8.0F, (e) -> {
        Draw.color(UnityPal.monolithLight, UnityPal.monolithDark, e.finpow());
        Lines.stroke(0.2F + e.fout() * 1.3F);
        Lines.circle(e.x, e.y, e.fin() * 5.0F);
    });
    public static Effect tendenceHit = new Effect(52.0F, (e) -> {
        Draw.color(UnityPal.monolithLight, UnityPal.monolith, UnityPal.monolithDark, e.fout());

        for(int sign : Mathf.signs) {
            Angles.randLenVectors((long)(e.id + sign), 3, e.fin(Interp.pow5Out) * 32.0F, e.rotation, 30.0F, 16.0F, (x, y) -> Fill.square(e.x + x, e.y + y, e.foutpowdown() * 2.5F, (float)e.id * 30.0F + e.finpow() * 90.0F * (float)sign));
        }

    });
    public static Effect hitAdvanceFlame = new Effect(15.0F, (e) -> {
        Draw.color(UnityPal.advance, UnityPal.advanceDark, e.fin());
        Angles.randLenVectors((long)e.id, 2, e.finpow() * 17.0F, e.rotation, 60.0F, (x, y) -> Fill.poly(e.x + x, e.y + y, 6, 3.0F + e.fout() * 3.0F, e.rotation));
    });
    public static Effect branchFragHit = new Effect(8.0F, (e) -> {
        Draw.color(Color.white, Pal.lancerLaser, e.fin());
        Lines.stroke(0.5F + e.fout());
        Lines.circle(e.x, e.y, e.fin() * 5.0F);
        Lines.stroke(e.fout());
        Lines.circle(e.x, e.y, e.fin() * 6.0F);
    });
    public static Effect hitExplosionLarge = new Effect(30.0F, 200.0F, (e) -> {
        Draw.color(Pal.missileYellow);
        e.scaled(12.0F, (s) -> {
            Lines.stroke(s.fout() * 2.0F + 0.5F);
            Lines.circle(e.x, e.y, s.fin() * 60.0F);
        });
        Draw.color(Color.gray);
        Angles.randLenVectors((long)e.id, 8, 2.0F + 42.0F * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5.0F + 0.5F));
        Draw.color(Pal.missileYellowBack);
        Lines.stroke(e.fout() * 1.5F);
        Angles.randLenVectors((long)(e.id + 1), 5, 1.0F + 56.0F * e.finpow(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1.0F + e.fout() * 5.0F));
        Drawf.light(e.x, e.y, 60.0F, Pal.missileYellowBack, 0.8F * e.fout());
    });
    public static Effect hitExplosionMassive = new Effect(70.0F, 370.0F, (e) -> {
        e.scaled(17.0F, (s) -> {
            Draw.color(Color.white, Color.lightGray, e.fin());
            Lines.stroke(s.fout() + 0.5F);
            Lines.circle(e.x, e.y, e.fin() * 185.0F);
        });
        Draw.color(Color.gray);
        Angles.randLenVectors((long)e.id, 12, 5.0F + 135.0F * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 22.0F + 0.5F);
            Fill.circle(e.x + x / 2.0F, e.y + y / 2.0F, e.fout() * 9.0F);
        });
        Draw.color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
        Lines.stroke(1.5F * e.fout());
        Angles.randLenVectors((long)(e.id + 1), 14, 1.0F + 160.0F * e.finpow(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1.0F + e.fout() * 3.0F));
    });
    public static Effect monolithHitSmall = new Effect(14.0F, (e) -> {
        Draw.color(UnityPal.monolith);
        e.scaled(7.0F, (s) -> {
            Lines.stroke(s.fout());
            Lines.square(s.x, s.y, 10.0F * s.fin(), 45.0F);
        });
        Draw.color(UnityPal.monolithLight);
        Angles.randLenVectors((long)e.id, 5, e.fin() * 15.0F, (x, y) -> Fill.square(e.x + x, e.y + y, 2.0F * e.fout()));
    });
    public static Effect monolithHitBig = new Effect(13.0F, (e) -> {
        Draw.color(UnityPal.monolithLight);
        Angles.randLenVectors((long)e.id, 10, e.fin() * 20.0F, (x, y) -> Fill.square(e.x + x, e.y + y, 5.0F * e.fout()));
        Tmp.c1.set(UnityPal.monolith).a(e.fout(Interp.pow3In));
        Draw.z(111.0F);
        Draw.blend(Blending.additive);
        Fill.light(e.x, e.y, 4, 25.0F * e.fin(Interp.pow5Out), Color.clear, Tmp.c1);
        Draw.blend();
    });
    public static Effect soulConcentrateHit = new Effect(30.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Long) {
            Long data = (Long)data$temp;
            float initRad = Float2.x(data);
            float scl = Float2.y(data);
            float radius = initRad * scl;
            e.lifetime = 30.0F * scl;
            Draw.color(UnityPal.monolithGreen, e.fout(Interp.pow5In));
            Fill.circle(e.x, e.y, radius + e.fin(Interp.pow10Out) * 6.0F * scl);
            Lines.stroke(e.fout() * 2.0F * scl, UnityPal.monolithGreenLight);
            Lines.circle(e.x, e.y, radius + e.fin(Interp.pow10Out) * 6.0F * scl);
            Angles.randLenVectors((long)e.id, (int)(8.0F * scl), e.finpow() * 15.0F * scl, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout(Interp.pow4Out) * 8.0F * scl));
        }
    });
}
