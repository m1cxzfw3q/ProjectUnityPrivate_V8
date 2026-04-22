package unity.content.effects;

import arc.Core;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
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
import unity.entities.bullet.anticheat.OppressionLaserBulletType;
import unity.entities.effects.CustomStateEffect;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.MathU;
import unity.util.Utils;

public class ShootFx {
    private static final Color tmpCol = new Color();
    public static Effect laserChargeShoot = new Effect(21.0F, (e) -> {
        Draw.color(e.color, Color.white, e.fout());

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(e.x, e.y, 4.0F * e.fout(), 29.0F, e.rotation + 90.0F * (float)i + e.finpow() * 112.0F);
        }

    });
    public static Effect laserChargeShootShort = new Effect(15.0F, (e) -> {
        Draw.color(e.color, Color.white, e.fout());
        Lines.stroke(2.0F * e.fout());
        Lines.square(e.x, e.y, 0.1F + 20.0F * e.finpow(), 45.0F);
    });
    public static Effect laserFractalShoot = new Effect(40.0F, (e) -> {
        Draw.color(Tmp.c1.set(e.color).lerp(Color.white, e.fout()));

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(e.x, e.y, 4.0F * e.fout(), 29.0F, e.rotation + 90.0F * (float)i + e.finpow() * 112.0F);
        }

        for(int h = 1; h <= 5; ++h) {
            float mul = (float)(h % 2);
            float rm = 1.0F + mul * 0.5F;
            float rot = 90.0F + (1.0F - e.finpow()) * Mathf.randomSeed((long)e.id + (long)(mul * 2.0F), 210.0F * rm, 360.0F * rm);

            for(int i = 0; i < 2; ++i) {
                float m = i == 0 ? 1.0F : 0.5F;
                float w = 8.0F * e.fout() * m;
                float length = 24.0F / (2.0F - mul);
                Vec2 fxPos = Tmp.v1.trns(rot, length - 4.0F);
                length *= Utils.pow25Out.apply(e.fout());
                Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length * m, rot + 180.0F);
                Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length / 3.0F * m, rot);
                Draw.alpha(0.5F);
                Drawf.tri(e.x, e.y, w, length * m, rot + 360.0F);
                Drawf.tri(e.x, e.y, w, length / 3.0F * m, rot);
                Fill.square(fxPos.x + e.x, fxPos.y + e.y, 3.0F * e.fout(), rot + 45.0F);
            }
        }

    });
    public static Effect laserBreakthroughShoot = new Effect(40.0F, (e) -> {
        Draw.color(e.color);
        Lines.stroke(e.fout() * 2.5F);
        Lines.circle(e.x, e.y, e.finpow() * 100.0F);
        Lines.stroke(e.fout() * 5.0F);
        Lines.circle(e.x, e.y, e.fin() * 100.0F);
        Draw.color(e.color, Color.white, e.fout());
        Angles.randLenVectors((long)e.id, 20, 80.0F * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5.0F));

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(e.x, e.y, 9.0F * e.fout(), 170.0F, e.rotation + Mathf.randomSeed((long)e.id, 360.0F) + 90.0F * (float)i + e.finpow() * (0.5F - Mathf.randomSeed((long)e.id)) * 150.0F);
        }

    });
    public static Effect shootSmallBlaze = new Effect(22.0F, (e) -> {
        Draw.color(Pal.lightFlame, Pal.darkFlame, Pal.gray, e.fin());
        Angles.randLenVectors((long)e.id, 16, e.finpow() * 60.0F, e.rotation, 18.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85F + e.fout() * 3.5F));
    });
    public static Effect shootPyraBlaze = new Effect(32.0F, (e) -> {
        Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Pal.gray, e.fin());
        Angles.randLenVectors((long)e.id, 16, e.finpow() * 60.0F, e.rotation, 18.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85F + e.fout() * 3.5F));
    });
    public static Effect orbShoot = new Effect(21.0F, (e) -> {
        Draw.color(Pal.surge);

        for(int i = 0; i < 2; ++i) {
            int l = Mathf.signs[i];
            Drawf.tri(e.x, e.y, 4.0F * e.fout(), 29.0F, e.rotation + (float)(67 * l));
        }

    });
    public static Effect shrapnelShoot = new Effect(13.0F, (e) -> {
        Draw.color(Color.white, Pal.bulletYellow, Pal.lightOrange, e.fin());
        Lines.stroke(e.fout() * 1.2F + 0.5F);
        Angles.randLenVectors((long)e.id, 10, 30.0F * e.finpow(), e.rotation, 50.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5.0F + 2.0F));
    });
    public static Effect plagueShootSmokeLarge = new Effect(35.0F, (e) -> {
        Draw.color(UnityPal.plagueDark, Color.gray, Color.darkGray, e.fin());

        for(int i = 0; i < 12; ++i) {
            float r = Utils.randomTriangularSeed((long)e.id * 191L + (long)i) * 90.0F + e.rotation;
            Vec2 v = Tmp.v1.trns(r, e.finpow() * 20.0F * Mathf.randomSeed((long)e.id * 81L + (long)i)).add(e.x, e.y);
            Fill.circle(v.x, v.y, 5.0F * Mathf.curve(e.fout(), 0.0F, 0.7F) * Mathf.randomSeed((long)e.id * 9L + (long)i, 0.8F, 1.1F));
        }

        e.scaled(20.0F, (s) -> {
            Lines.stroke(1.5F);
            Draw.color(UnityPal.plague, Color.white, s.fin());
            Angles.randLenVectors((long)e.id, 5, 25.0F * s.finpow() + 0.1F, e.rotation, 20.0F, (x, y) -> {
                float r = Mathf.angle(x, y);
                Lines.lineAngle(e.x + x, e.y + y, r, 5.0F * s.fout());
            });
        });
    });
    public static Effect scarRailShoot = new Effect(24.0F, (e) -> {
        e.scaled(10.0F, (b) -> {
            Draw.color(Color.white, Color.lightGray, b.fin());
            Lines.stroke(b.fout() * 3.0F + 0.2F);
            Lines.circle(b.x, b.y, b.fin() * 50.0F);
        });

        for(int i = 0; i < 2; ++i) {
            int sign = Mathf.signs[i];
            Draw.color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 13.0F * e.fout(), 85.0F, e.rotation + 90.0F * (float)sign);
            Draw.color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(13.0F * e.fout() - 4.0F, 0.0F), 81.0F, e.rotation + 90.0F * (float)sign);
        }

    });
    public static Effect endGameShoot = (new Effect(45.0F, 1640.0F, (e) -> {
        float curve = Mathf.curve(e.fin(), 0.0F, 0.2F) * 820.0F;
        float curveB = Mathf.curve(e.fin(), 0.0F, 0.7F);
        Draw.color(Color.red, Color.valueOf("ff000000"), curveB);
        Draw.blend(Blending.additive);
        Fill.poly(e.x, e.y, Lines.circleVertices(curve), curve);
        Draw.blend();
    })).layer(110.99F);
    public static Effect oppressionShoot = (new Effect(170.0F, 5060.0F, (e) -> {
        Rand r = Utils.seedr;
        Rand r2 = Utils.seedr2;
        float[] shape = OppressionLaserBulletType.shape;
        float[] q = OppressionLaserBulletType.quad;
        float fin1 = e.time / 25.0F;
        Draw.color(UnityPal.endColor);
        UnityDrawf.diamond(e.x, e.y, 17.0F * e.fout(), 160.0F + Mathf.absin(8.0F, 6.0F) + 90.0F * e.finpow(), e.rotation + 90.0F);
        if (e.time < 25.0F) {
            float width = 280.0F * Interp.pow3Out.apply(fin1);
            Lines.stroke(5.0F * (1.0F - fin1));

            for(int i = 0; i < shape.length; i += 4) {
                if (i >= shape.length - 4) {
                    for(int s : Mathf.signs) {
                        Vec2 v = Tmp.v1.trns(e.rotation, 380.0F, width * (float)s).add(e.x, e.y);
                        UnityDrawf.tri(v.x, v.y, Lines.getStroke(), 1000.0F, e.rotation);
                    }
                } else {
                    for(int j = 0; j < q.length; j += 2) {
                        Vec2 v = Tmp.v1.trns(e.rotation, shape[i + j + 1] * 380.0F, shape[i + j] * width).add(e.x, e.y);
                        q[j] = v.x;
                        q[j + 1] = v.y;
                    }

                    Lines.line(q[0], q[1], q[6], q[7], false);
                    Lines.line(q[2], q[3], q[4], q[5], false);
                }
            }
        }

        Lines.stroke(11.0F * e.fout());

        for(int i = 0; i < 2; ++i) {
            float fin2 = Mathf.clamp(e.time / 15.0F) + e.fin() * 0.25F;
            float trns = i == 0 ? 60.0F : 130.0F;
            float w = Interp.circleOut.apply(trns / 380.0F) * 140.0F + 250.0F * fin2;
            Vec2 v = Tmp.v1.trns(e.rotation, trns).add(e.x, e.y);
            Lines.lineAngleCenter(v.x, v.y, e.rotation + 90.0F, w * 2.0F, false);
        }

        r.setSeed((long)e.id * 9999L);

        for(int i = 0; i < 75; ++i) {
            float maxOff = 0.2F + r.random(0.1F);
            float off = r.nextFloat() * maxOff;
            float fin = Mathf.curve(e.fin(), off, 1.0F - maxOff + off);
            float rot = r.random(360.0F);
            float trns1 = r.random(195.0F) * Interp.pow3Out.apply(fin) + r.random(20.0F);
            float trns2 = r.random(190.0F, 610.0F) * Interp.pow2In.apply(fin);
            float scl = Interp.pow3Out.apply(MathU.slope(fin, 0.09F)) * r.random(9.0F, 14.0F);
            Vec2 v = Tmp.v1.trns(rot, trns1);
            Vec2 v2 = Tmp.v2.trns(e.rotation, -trns2).add(e.x, e.y);
            Draw.color(UnityPal.scarColor, Color.darkGray, Color.gray, fin);
            Fill.circle(v2.x + v.x, v2.y + v.y, scl);
            Fill.circle(v2.x + v.x / 2.0F, v2.y + v.y / 2.0F, scl / 2.0F);
        }

    })).followParent(true).rotWithParent(true);
    public static Effect monumentShoot = new Effect(48.0F, (e) -> {
        Draw.color(UnityPal.monolithLight);
        Drawf.tri(e.x, e.y, 10.0F * e.fout(), 175.0F - 20.0F * e.fin(), e.rotation);

        for(int i = 0; i < 2; ++i) {
            Drawf.tri(e.x, e.y, 10.0F * e.fout(), 50.0F, e.rotation + (45.0F + e.fin(Interp.pow3Out) * 30.0F) * (float)Mathf.signs[i]);
        }

        Angles.randLenVectors((long)e.id, 15, e.fin(Interp.pow2Out) * 80.0F, e.rotation, 20.0F, (x, y) -> Fill.square(e.x + x, e.y + y, 3.0F * e.fout()));
        Fill.square(e.x, e.y, 5.0F * e.fout(Interp.pow3Out), e.rotation + 45.0F);
        Draw.color();
        Fill.square(e.x, e.y, 2.0F * e.fout(Interp.pow3Out), e.rotation + 45.0F);
        e.scaled(15.0F, (s) -> {
            Draw.z(111.0F);
            Draw.blend(Blending.additive);
            Tmp.c1.set(UnityPal.monolithLight).a(s.fout(Interp.pow5In));
            Fill.light(s.x, s.y, 4, 40.0F * s.fin(Interp.pow5Out), Color.clear, Tmp.c1);
            Draw.blend();
        });
    });
    public static Effect soulConcentrateShoot = new Effect(60.0F, (e) -> {
        int id = e.id;

        for(int sign : Mathf.signs) {
            float r = e.foutpow() * 2.0F;
            Draw.color(UnityPal.monolithGreen, UnityPal.monolithGreenDark, e.finpowdown());

            for(int rsign : Mathf.signs) {
                Angles.randLenVectors((long)(id++), 2, e.finpow() * 20.0F, e.rotation + (float)sign * 90.0F, 30.0F, (x, y) -> Fill.rect(e.x + x, e.y + y, r, r, e.foutpow() * 135.0F * (float)rsign));
            }
        }

        float r = e.fout(Interp.pow5Out) * 2.4F;
        Draw.color(UnityPal.monolithGreenLight, UnityPal.monolithGreen, e.fin(Interp.pow5In));

        for(int rsign : Mathf.signs) {
            Angles.randLenVectors((long)(id++), 3, e.fin(Interp.pow5Out) * 32.0F, e.rotation, 45.0F, (x, y) -> Fill.rect(e.x + x, e.y + y, r, r, e.foutpow() * 180.0F * (float)rsign));
        }

    });
    public static Effect tendenceShoot = (new Effect(32.0F, (e) -> {
        TextureRegion reg = Core.atlas.find("unity-monolith-chain");
        Utils.q1.set(Vec3.Z, e.rotation + 90.0F).mul(Utils.q2.set(Vec3.X, 75.0F));
        float t = e.finpow();
        float w = (float)reg.width * Draw.scl * 0.4F * t;
        float h = (float)reg.height * Draw.scl * 0.4F * t;
        float rad = 9.0F + t * 8.0F;
        Draw.color(UnityPal.monolithLight);
        Draw.alpha(e.foutpowdown());
        UnityDrawf.panningCircle(reg, e.x, e.y, w, h, rad, 360.0F, e.fin(Interp.pow2Out) * 90.0F * (float)Mathf.sign(e.id % 2 == 0) + (float)e.id * 30.0F, Utils.q1, 89.99F, 115.0F);
        Draw.color(Color.black, UnityPal.monolithDark, 0.67F);
        Draw.alpha(e.foutpowdown());
        Draw.blend(Blending.additive);
        UnityDrawf.panningCircle(Core.atlas.find("unity-line-shade"), e.x, e.y, w + 6.0F, h + 6.0F, rad, 360.0F, 0.0F, Utils.q1, true, 89.99F, 115.0F);
        Draw.blend();
    })).layer(115.0F);
    public static Effect pedestalShootAdd = (new CustomStateEffect(() -> (EffectState)Pools.obtain(State.class, () -> {
        class State extends EffectState {
            public void remove() {
                Object var2 = this.data;
                if (var2 instanceof Trail[]) {
                    Trail[] data = (Trail[])var2;

                    for(Trail trail : data) {
                        Fx.trailFade.at(this.x, this.y, 1.0F, UnityPal.monolithLight, trail.copy());
                    }
                }

                super.remove();
            }
        }

        return new State();
    }), 25.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Trail[]) {
            Trail[] data = (Trail[])data$temp;
            float initAngle = Mathf.randomSeed((long)e.id, 360.0F);

            for(int i = 0; i < data.length; ++i) {
                Trail trail = data[i];
                if (!Vars.state.isPaused()) {
                    Tmp.v1.trns(initAngle + 360.0F / (float)data.length * (float)i + Time.time * 6.0F, 4.0F + e.foutpowdown() * 16.0F).add(e.x, e.y);
                    trail.update(Tmp.v1.x, Tmp.v1.y, e.fin() * 1.4F);
                }

                trail.drawCap(UnityPal.monolithLight, 1.0F);
                trail.draw(UnityPal.monolithLight, 1.0F);
            }

            Draw.color(UnityPal.monolithDark, UnityPal.monolith, e.fin());
            Angles.randLenVectors((long)(e.id + 1), 3, e.foutpow() * 8.0F, 360.0F, 0.0F, 4.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.4F + e.fin() * 1.6F));
        }
    }) {
        protected EffectState inst(float x, float y, float rotation, Color color, Object data) {
            Trail[] trails = new Trail[5];

            for(int i = 0; i < trails.length; ++i) {
                trails[i] = Trails.soul(24);
            }

            EffectState state = super.inst(x, y, rotation, color, data);
            state.data = trails;
            return state;
        }
    }).followParent(true).rotWithParent(true);
    public static Effect phantasmalLaserShoot = new Effect(36.0F, (e) -> {
        Object var3 = e.data;
        float var10000;
        if (var3 instanceof Float) {
            Float data = (Float)var3;
            var10000 = data;
        } else {
            var10000 = 9.0F;
        }

        float radius = var10000;
        float fin = e.fin();
        float f1 = Mathf.curve(fin, 0.0F, 0.76F);
        float f2 = Mathf.curve(fin, 0.12F, 0.88F);
        float f3 = Mathf.curve(fin, 0.24F, 1.0F);
        TextureRegion reg = Core.atlas.white();
        Utils.q1.set(Vec3.Z, e.rotation + 90.0F).mul(Utils.q2.set(Vec3.X, 75.0F));
        Lines.stroke(2.0F);
        Draw.color(UnityPal.monolithLight, Interp.pow3Out.apply(f1) * Interp.pow10Out.apply(1.0F - f1));
        Tmp.v1.trns(e.rotation, -8.0F + Interp.bounceOut.apply(f1) * 8.0F - Interp.pow3In.apply(Mathf.curve(f1, 0.67F, 1.0F)) * 4.0F).add(e.x, e.y);
        UnityDrawf.panningCircle(reg, Tmp.v1.x, Tmp.v1.y, 1.0F, 1.0F, radius, 360.0F, 0.0F, Utils.q1, true, 99.999F, 100.001F);
        Draw.color(UnityPal.monolith, Interp.pow3Out.apply(f2) * Interp.pow10Out.apply(1.0F - f2));
        Tmp.v1.trns(e.rotation, -2.0F + Interp.bounceOut.apply(f2) * 8.0F - Interp.pow3In.apply(Mathf.curve(f2, 0.67F, 1.0F)) * 4.0F).add(e.x, e.y);
        UnityDrawf.panningCircle(reg, Tmp.v1.x, Tmp.v1.y, 1.0F, 1.0F, radius * 0.75F, 360.0F, 0.0F, Utils.q1, true, 99.999F, 100.001F);
        Draw.color(UnityPal.monolithDark, Interp.pow3Out.apply(f3) * Interp.pow10Out.apply(1.0F - f3));
        Tmp.v1.trns(e.rotation, 4.0F + Interp.bounceOut.apply(f3) * 8.0F - Interp.pow3In.apply(Mathf.curve(f3, 0.67F, 1.0F)) * 4.0F).add(e.x, e.y);
        UnityDrawf.panningCircle(reg, Tmp.v1.x, Tmp.v1.y, 1.0F, 1.0F, radius * 0.5F, 360.0F, 0.0F, Utils.q1, true, 99.999F, 100.001F);
    });
    public static Effect coloredPlasmaShoot = new Effect(25.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        Angles.randLenVectors((long)e.id, 13, e.finpow() * 20.0F, e.rotation, 23.0F, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5.0F);
            Fill.circle(e.x + x / 1.2F, e.y + y / 1.2F, e.fout() * 3.0F);
        });
    });
    public static Effect sapPlasmaShoot = new Effect(25.0F, (e) -> {
        Draw.color(Color.white, Pal.sapBullet, e.fin());
        Angles.randLenVectors((long)e.id, 13, e.finpow() * 20.0F, e.rotation, 23.0F, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5.0F);
            Fill.circle(e.x + x / 1.2F, e.y + y / 1.2F, e.fout() * 3.0F);
        });
    });
    public static Effect blueTriangleShoot = new Effect(23.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Fill.poly(e.x, e.y, 3, e.fout() * 24.0F, e.rotation);
        Fill.circle(e.x, e.y, e.fout() * 11.0F);
        Draw.color(Color.white);
        Fill.circle(e.x, e.y, e.fout() * 9.0F);
    });
    public static Effect voidShoot = new Effect(20.0F, (e) -> {
        Draw.color(Color.black);
        Angles.randLenVectors((long)e.id, 14, e.finpow() * 20.0F, e.rotation, 20.0F * Mathf.curve(e.fin(), 0.0F, 0.2F), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5.0F);
            Fill.circle(e.x + x / 2.0F, e.y + y / 2.0F, e.fout() * 3.0F);
        });
    });
}
