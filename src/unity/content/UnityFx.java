package unity.content;

import arc.Core;
import arc.func.Cons;
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
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.bullet.PointBulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.turrets.Turret;
import unity.entities.UnitVecData;
import unity.entities.abilities.BaseAbility;
import unity.entities.bullet.energy.EphemeronBulletType;
import unity.entities.bullet.energy.EphemeronPairBulletType;
import unity.entities.bullet.energy.SingularityBulletType;
import unity.entities.bullet.laser.PointBlastLaserBulletType;
import unity.gen.SVec2;
import unity.graphics.FixedTrail;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.type.RainbowUnitType;
import unity.util.GraphicUtils;
import unity.util.Utils;

public class UnityFx {
    private static int integer;
    private static final Rand rand = new Rand();
    public static final Effect fixedTrailFade = new Effect(400.0F, (e) -> {
        Object trail$temp = e.data;
        if (trail$temp instanceof FixedTrail) {
            FixedTrail trail = (FixedTrail)trail$temp;
            e.lifetime = (float)trail.length * 1.4F;
            trail.shorten();
            trail.drawCap(e.color, e.rotation);
            trail.draw(e.color, e.rotation);
        }
    });
    public static final Effect sparkle = new Effect(55.0F, (e) -> {
        Draw.color(e.color);
        integer = 0;
        Angles.randLenVectors((long)e.id, e.id % 3 + 1, 8.0F, (x, y) -> {
            ++integer;
            UnityDrawf.spark(e.x + x, e.y + y, e.fout() * 2.5F, 0.5F + e.fout(), (float)(e.id * integer));
        });
    });
    public static final Effect expGain = new Effect(75.0F, 400.0F, (e) -> {
        Object pos$temp = e.data;
        if (pos$temp instanceof Position) {
            Position pos = (Position)pos$temp;
            float fin = Mathf.curve(e.fin(), 0.0F, Mathf.randomSeed((long)e.id, 0.25F, 1.0F));
            if (!(fin >= 1.0F)) {
                float a = Angles.angle(e.x, e.y, pos.getX(), pos.getY()) - 90.0F;
                float d = Mathf.dst(e.x, e.y, pos.getX(), pos.getY());
                float fslope = fin * (1.0F - fin) * 4.0F;
                float sfin = Interp.pow2In.apply(fin);
                float spread = d / 4.0F;
                Tmp.v1.trns(a, Mathf.randomSeed((long)e.id * 2L, -spread, spread) * fslope, d * sfin);
                Tmp.v1.add(e.x, e.y);
                Draw.color(UnityPal.exp, Color.white, 0.1F + 0.1F * Mathf.sin(Time.time * 0.03F + (float)e.id * 3.0F));
                Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.5F);
                Lines.stroke(0.5F);

                for(int i = 0; i < 4; ++i) {
                    Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4.0F, 4.0F + 1.5F * Mathf.sin(Time.time * 0.12F + (float)e.id * 4.0F), (float)i * 90.0F + Mathf.sin(Time.time * 0.04F + (float)e.id * 5.0F) * 28.0F);
                }

            }
        }
    });
    public static final Effect expDump = new Effect(75.0F, 400.0F, (e) -> {
        Object pos$temp = e.data;
        if (pos$temp instanceof Position) {
            Position pos = (Position)pos$temp;
            float fin = Mathf.curve(e.fin(), 0.0F, Mathf.randomSeed((long)e.id, 0.25F, 1.0F));
            if (!(fin >= 1.0F)) {
                float a = Angles.angle(e.x, e.y, pos.getX(), pos.getY()) - 90.0F;
                float d = Mathf.dst(e.x, e.y, pos.getX(), pos.getY());
                float fslope = fin * (1.0F - fin) * 4.0F;
                float sfin = Interp.pow2In.apply(fin);
                float spread = d / 4.0F;
                Tmp.v1.trns(a, Mathf.randomSeed((long)e.id * 2L, -spread, spread) * fslope, d * sfin);
                Tmp.v1.add(e.x, e.y);
                Draw.color(UnityPal.exp, Color.white, 0.1F + 0.1F * Mathf.sin(Time.time * 0.03F + (float)e.id * 3.0F));
                Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.5F);
                Lines.stroke(0.5F);

                for(int i = 0; i < 4; ++i) {
                    Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4.0F, 4.0F + 1.5F * Mathf.cos(Time.time * 0.12F + (float)e.id * 4.0F), (float)i * 90.0F + Mathf.sin(Time.time * 0.04F + (float)e.id * 5.0F) * 28.0F);
                }

            }
        }
    });
    public static final Effect expPoof = new Effect(60.0F, (e) -> {
        Draw.color(Pal.accent, UnityPal.exp, e.fin());
        integer = 0;
        Angles.randLenVectors((long)e.id, 9, 1.0F + 30.0F * e.finpow(), (x, y) -> {
            ++integer;
            Fill.circle(e.x + x, e.y + y, 1.7F * e.fout());
            UnityDrawf.spark(e.x + x, e.y + y, 5.0F, (5.0F + 1.5F * Mathf.sin(Time.time * 0.12F + (float)integer * 4.0F)) * e.fout(), e.finpow() * 90.0F + (float)integer * 69.0F);
        });
    });
    public static final Effect expShineRegion = new Effect(25.0F, (e) -> {
        Draw.color();
        Tmp.c1.set(Pal.accent).lerp(UnityPal.exp, e.fin());
        Draw.mixcol(Tmp.c1, 1.0F);
        Draw.alpha(1.0F - e.fin() * e.fin());
        Object region$temp = e.data;
        if (region$temp instanceof TextureRegion) {
            TextureRegion region = (TextureRegion)region$temp;
            Draw.rect(region, e.x, e.y, e.rotation);
        }

    });
    public static final Effect orbDespawn = new Effect(15.0F, (e) -> {
        Draw.color(UnityPal.exp);
        Lines.stroke(e.fout() * 1.2F + 0.01F);
        Lines.circle(e.x, e.y, 4.0F * e.finpow());
    });
    public static final Effect expLaser = new Effect(15.0F, (e) -> {
        Object b$temp = e.data;
        if (b$temp instanceof Building) {
            Building b = (Building)b$temp;
            if (!b.dead) {
                Tmp.v2.set(b);
                Tmp.v1.set(Tmp.v2).sub(e.x, e.y).nor().scl(4.0F);
                Tmp.v2.sub(Tmp.v1);
                Tmp.v1.add(e.x, e.y);
                Drawf.laser((Team)null, Core.atlas.find("unity-exp-laser"), Core.atlas.find("unity-exp-laser-end"), Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, 0.4F * e.fout());
            }
        }

    });
    public static final Effect placeShine = new Effect(30.0F, (e) -> {
        Draw.color(e.color);
        Lines.stroke(e.fout());
        Lines.square(e.x, e.y, e.rotation / 2.0F + e.fin() * 3.0F);
        UnityDrawf.spark(e.x, e.y, 25.0F, 15.0F * e.fout(), e.finpow() * 90.0F);
    });
    public static final Effect laserCharge = new Effect(38.0F, (e) -> {
        Draw.color(e.color);
        Angles.randLenVectors((long)e.id, e.id % 3 + 1, 1.0F + 20.0F * e.fout(), e.rotation, 120.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3.0F + 1.0F));
    });
    public static final Effect laserChargeShort = new Effect(18.0F, (e) -> {
        Draw.color(e.color);
        Angles.randLenVectors((long)e.id, 1, 1.0F + 20.0F * e.fout(), e.rotation, 120.0F, (x, y) -> Fill.square(e.x + x, e.y + y, e.fslope() * 1.5F + 0.1F, 45.0F));
    });
    public static final Effect laserFractalCharge = new Effect(120.0F, (e) -> {
        float radius = 80.0F;
        float[] p = new float[]{0.0F, 0.0F};
        Angles.randLenVectors((long)e.id, 3, radius / 2.0F + Interp.pow3Out.apply(1.0F - e.fout(0.5F)) * radius * 1.25F, (x, y) -> e.scaled(60.0F, (ee) -> {
            ee.scaled(30.0F, (e1) -> {
                p[0] = Mathf.lerp(x, 0.0F, e1.fin(Interp.pow2));
                p[1] = Mathf.lerp(y, 0.0F, e1.fin(Interp.pow2));
            });
            Lines.stroke(ee.fout(0.5F), Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5F).a(ee.fout(0.5F)));
            Lines.line(e.x + x, e.y + y, e.x + p[0], e.y + p[1]);
        }));
    });
    public static final Effect laserFractalChargeBegin = new Effect(90.0F, (e) -> {
        int[] r = new int[]{9, 10, 11, 12};
        e.scaled(60.0F, (ee) -> r[0] = (int)((float)r[0] * ee.fin()));
        e.scaled(40.0F, (ee) -> r[1] = (int)((float)r[1] * ee.fin()));
        e.scaled(40.0F, (ee) -> r[2] = (int)((float)r[2] * ee.fin()));
        e.scaled(60.0F, (ee) -> r[3] = (int)((float)r[3] * ee.fin()));
        Draw.color(UnityPal.lancerSap3.cpy().a(0.1F + 0.55F * e.fslope()));
        Lines.arc(e.x, e.y, (float)r[0], 0.6F, Time.time * 8.0F - 60.0F);
        Lines.arc(e.x, e.y, (float)r[1], 0.6F, Time.time * 5.0F);
        Draw.color(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5F + 0.5F * Mathf.sin(16.0F * e.fin())).a(0.25F + 0.8F * e.fslope()));
        Lines.arc(e.x, e.y, (float)r[2], 0.4F, Time.time * -6.0F + 121.0F);
        Lines.arc(e.x, e.y, (float)r[3], 0.4F, Time.time * -4.0F + 91.0F);
    });
    public static final Effect laserChargeBegin = new Effect(60.0F, (e) -> {
        Draw.color(e.color);
        Fill.square(e.x, e.y, e.fin() * 3.0F, 45.0F);
        Draw.color();
        Fill.square(e.x, e.y, e.fin() * 2.0F, 45.0F);
    });
    public static final Effect freezeEffect = new Effect(30.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        Lines.stroke(e.fout() * 2.0F);
        Lines.poly(e.x, e.y, 6, 4.0F + e.rotation * 1.5F * e.finpow(), Mathf.randomSeed((long)e.id) * 360.0F);
        Draw.color();
        integer = 0;
        Angles.randLenVectors((long)e.id, 5, e.rotation * 1.6F * e.fin() + 16.0F, e.fin() * 33.0F, 360.0F, (x, y) -> {
            UnityDrawf.snowFlake(e.x + x, e.y + y, e.finpow() * 60.0F, Mathf.randomSeed((long)e.id + (long)integer) * 2.0F + 2.0F);
            ++integer;
        });
        Angles.randLenVectors((long)(e.id + 1), 3, e.rotation * 2.1F * e.fin() + 7.0F, e.fin() * -19.0F, 360.0F, (x, y) -> {
            UnityDrawf.snowFlake(e.x + x, e.y + y, e.finpow() * 60.0F, Mathf.randomSeed((long)e.id + (long)integer) * 2.0F + 2.0F);
            ++integer;
        });
    });
    public static final Effect giantSplash = new Effect(30.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        Lines.stroke(2.0F * e.fout());
        Lines.circle(e.x, e.y, e.finpow() * 20.0F);
        integer = 0;
        Angles.randLenVectors((long)e.id, 11, 4.0F + 40.0F * e.finpow(), (x, y) -> {
            ++integer;
            Fill.circle(e.x + x, e.y + y, e.fslope() * Mathf.randomSeed((long)(e.id + integer), 5.0F, 9.0F) + 0.1F);
        });
    });
    public static final Effect hotSteam = (new Effect(150.0F, (e) -> {
        Draw.color(e.color, e.fout() * 0.9F);
        integer = 0;
        Angles.randLenVectors((long)e.id, 2, 10.0F + 20.0F * e.fin(), (x, y) -> {
            ++integer;
            Fill.circle(e.x + x, e.y + y, e.fin() * Mathf.randomSeed((long)(e.id + integer), 15.0F, 19.0F) + 0.1F);
        });
    })).layer(116.0F);
    public static final Effect iceSheet = (new Effect(540.0F, (e) -> {
        Draw.color(Color.white, e.color, 0.3F);
        integer = 0;
        float fin2 = Mathf.clamp(e.fin() * 5.0F);
        Angles.randLenVectors((long)e.id, 1, 16.0F + 2.0F * fin2, (x, y) -> {
            ++integer;
            Fill.poly(e.x + x, e.y + y, 6, fin2 * Mathf.randomSeed((long)(e.id + integer), 6.0F, 13.0F) * Mathf.clamp(9.0F * e.fout()) + 0.1F);
        });
    })).layer(18.9F);
    public static final Effect shootFlake = new Effect(21.0F, (e) -> {
        Draw.color(e.color, Color.white, e.fout());

        for(int i = 0; i < 6; ++i) {
            Drawf.tri(e.x, e.y, 3.0F * e.fout(), 12.0F, e.rotation + Mathf.randomSeed((long)e.id, 360.0F) + 60.0F * (float)i);
        }

    });
    public static final Effect plasmaedEffect = new Effect(50.0F, (e) -> {
        Draw.color(Liquids.cryofluid.color, Color.white.cpy().mul(0.25F, 0.25F, 1.0F, e.fout()), e.fout() / 6.0F + Mathf.randomSeedRange((long)e.id, 0.1F));
        Fill.square(e.x, e.y, e.fslope() * 2.0F, 45.0F);
    });
    public static final Effect laserBreakthroughChargeBegin = new Effect(100.0F, 100.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Lines.stroke(e.fin() * 3.0F);
        Lines.circle(e.x, e.y, 4.0F + e.fout() * 120.0F);
        Fill.circle(e.x, e.y, e.fin() * 23.5F);
        Angles.randLenVectors((long)e.id, 20, 50.0F * e.fout(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fin() * 6.0F));
        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 13.0F);
    });
    public static final Effect laserBreakthroughChargeBegin2 = new Effect(100.0F, 100.0F, (e) -> {
        Draw.color(UnityPal.exp);
        Lines.stroke(e.fin() * 3.0F);
        Lines.circle(e.x, e.y, 4.0F + e.fout() * 120.0F);
        Fill.circle(e.x, e.y, e.fin() * 23.5F);
        Angles.randLenVectors((long)e.id, 20, 50.0F * e.fout(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fin() * 6.0F));
        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 13.0F);
    });
    public static final Effect craft = new Effect(10.0F, (e) -> {
        Draw.color(Pal.accent, Color.gray, e.fin());
        Lines.stroke(1.0F);
        Lines.spikes(e.x, e.y, e.fin() * 4.0F, 1.5F, 6);
    });
    public static final Effect denseCraft = new Effect(10.0F, (e) -> {
        Draw.color(UnityPal.dense, Color.gray, e.fin());
        Lines.stroke(1.0F);
        Lines.spikes(e.x, e.y, e.finpow() * 4.5F, 1.0F, 6);
    });
    public static final Effect diriumCraft = new Effect(10.0F, (e) -> {
        Draw.color(Color.white, UnityPal.dirium, e.fin());
        Lines.stroke(1.0F);
        Lines.spikes(e.x, e.y, e.fin() * 4.0F, 1.5F, 6);
    });
    public static final Effect longSmoke = (new Effect(80.0F, (e) -> {
        Draw.color(Color.gray, Color.clear, e.fin());
        Angles.randLenVectors((long)e.id, 2, 4.0F + e.fin() * 4.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2F + e.fin() * 4.0F));
    })).layer(114.0F);
    public static final Effect blockMelt = new Effect(400.0F, (e) -> {
        Draw.color(Color.coral, Color.orange, Mathf.absin(9.0F, 1.0F));
        integer = 0;
        float f = Mathf.clamp(e.finpow() * 5.0F);
        Angles.randLenVectors((long)e.id, 15, 2.0F + f * f * 16.0F, (x, y) -> {
            ++integer;
            Fill.circle(e.x + x, e.y + y, 0.01F + e.fout() * Mathf.randomSeed((long)(e.id + integer), 2.0F, 6.0F));
        });
    });
    public static final Effect absorb = new Effect(12.0F, (e) -> {
        Draw.color(e.color);
        Lines.stroke(2.0F * e.fout());
        Lines.circle(e.x, e.y, 5.0F * e.fout());
    });
    public static final Effect deflect = new Effect(12.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        Lines.stroke(2.0F * e.fout());
        Angles.randLenVectors((long)e.id, 4, 0.1F + 8.0F * e.fout(), e.rotation, 60.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3.0F + 1.0F));
    });
    public static final Effect forceShrink = (new Effect(20.0F, (e) -> {
        Draw.color(e.color, e.fout());
        if (Vars.renderer.animateShields) {
            Fill.poly(e.x, e.y, Lines.circleVertices(e.rotation * e.fout()), e.rotation * e.fout());
        } else {
            Lines.stroke(1.5F);
            Draw.alpha(0.09F);
            Fill.circle(e.x, e.y, e.rotation * e.fout());
            Draw.alpha(1.0F);
            Lines.circle(e.x, e.y, e.rotation * e.fout());
        }

    })).layer(125.0F);
    public static final Effect shieldBreak = (new Effect(40.0F, (e) -> {
        Draw.color(e.color);
        Lines.stroke(3.0F * e.fout());
        Lines.circle(e.x, e.y, e.rotation + e.fin());
    })).followParent(true);
    public static final Effect craftingEffect = new Effect(67.0F, 35.0F, (e) -> {
        float value = Mathf.randomSeed((long)e.id);
        Tmp.v1.trns(value * 360.0F + (value + 4.0F) * e.fin() * 80.0F, (Mathf.randomSeed((long)e.id * 126L) + 1.0F) * 34.0F * (1.0F - e.finpow()));
        Draw.color(UnityPal.laserOrange);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3.0F, 45.0F);
        Draw.color();
    });
    public static final Effect whirl = new Effect(65.0F, (e) -> {
        for(int i = 0; i < 2; ++i) {
            int h = i * 2;
            float r1 = Interp.exp5In.apply((Mathf.randomSeedRange((long)(e.id + h), 1.0F) + 1.0F) / 2.0F);
            float r2 = (Mathf.randomSeedRange((long)e.id * 2L + (long)h, 360.0F) + 360.0F) / 2.0F;
            float r3 = (Mathf.randomSeedRange((long)e.id * 4L + (long)h, 5.0F) + 5.0F) / 2.0F;
            float a = r2 + (180.0F + r3) * e.fin();
            Tmp.v1.trns(a, r1 * 70.0F * e.fout());
            Draw.color(Pal.lancerLaser);
            Lines.stroke(e.fout() + 0.25F);
            Lines.lineAngle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, a + 270.0F + 15.0F, e.fout() * 8.0F);
        }

    });
    public static final Effect lightHexagonTrail = new Effect(55.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Fill.poly(e.x, e.y, 6, e.rotation * e.fout(), e.rotation);
        Draw.color();
    });
    public static final Effect wBosonEffect = new Effect(24.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Lines.stroke(1.25F);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout() * 4.0F);
    });
    public static final Effect wBosonEffectLong = new Effect(47.0F, (e) -> {
        Draw.color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        Lines.stroke(1.25F);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout() * 7.0F);
    });
    public static final Effect ephemeronLaser = new Effect(19.0F, 100.0F, (e) -> {
        Object d$temp = e.data;
        if (d$temp instanceof EphemeronBulletType.EphemeronEffectData) {
            EphemeronBulletType.EphemeronEffectData d = (EphemeronBulletType.EphemeronEffectData)d$temp;
            if (d.b != null && d.b.type instanceof EphemeronPairBulletType && d.b.isAdded()) {
                Lines.stroke(3.6F * e.fout(), e.color);
                Lines.line(d.b.x, d.b.y, d.x, d.y, false);
            }
        }

    });
    public static final Effect singularityDespawn = new Effect(12.0F, (e) -> {
        float[] scales = new float[]{8.6F, 7.0F, 5.5F, 4.3F, 4.1F, 3.9F};
        Color[] colors = new Color[]{Color.valueOf("4787ff80"), Pal.lancerLaser, Color.white, Pal.lancerLaser, UnityPal.lightEffect, Color.black};

        for(int i = 0; i < colors.length; ++i) {
            Draw.color(colors[i]);
            Fill.circle(e.x + (float)Mathf.range(1), e.y + (float)Mathf.range(1), e.fout() * 5.0F * scales[i]);
        }

    });
    public static final Effect singularityAttraction = (new Effect(23.0F, 180.0F, (e) -> {
        Object d$temp = e.data;
        if (d$temp instanceof SingularityBulletType.SingularityAbsorbEffectData) {
            SingularityBulletType.SingularityAbsorbEffectData d = (SingularityBulletType.SingularityAbsorbEffectData)d$temp;
            float interp = e.fin(Interp.pow3In);
            float size = 1.0F - e.fin(Interp.pow5In);
            float rot = e.rotation * 90.0F;
            float lerpx = Mathf.lerp(d.x, e.x, interp);
            float lerpy = Mathf.lerp(d.y, e.y, interp);
            Draw.rect(d.region, lerpx, lerpy, (float)d.region.width * Draw.scl * size, (float)d.region.height * Draw.scl * size, e.fin() * Mathf.randomSeedRange((long)e.id, 32.0F) + rot);
        }

    })).layer(110.02F);
    public static final Effect orbTrail = (new Effect(43.0F, (e) -> {
        Tmp.v1.trns(Mathf.randomSeed((long)e.id) * 360.0F, Mathf.randomSeed((long)e.id * 341L) * 12.0F * e.fin());
        Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7F * e.fout() + 3.0F, Pal.surge, 0.6F);
        Draw.color(Pal.surge);
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7F);
    })).layer(99.99F);
    public static final Effect orbCharge = new Effect(38.0F, (e) -> {
        Draw.color(Pal.surge);
        Angles.randLenVectors((long)e.id, 2, 1.0F + 20.0F * e.fout(), e.rotation, 120.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3.0F + 1.0F));
    });
    public static final Effect orbChargeBegin = new Effect(71.0F, (e) -> {
        Draw.color(Pal.surge);
        Fill.circle(e.x, e.y, e.fin() * 3.0F);
        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 2.0F);
    });
    public static final Effect currentCharge = new Effect(32.0F, (e) -> {
        Draw.color(Pal.surge, Color.white, e.fin());
        Angles.randLenVectors((long)e.id, 8, 420.0F + Mathf.random(24.0F, 28.0F) * e.fout(), e.rotation, 4.0F, (x, y) -> {
            Lines.stroke(0.3F + e.fout() * 2.0F);
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 14.0F + 0.5F);
        });
        Lines.stroke(e.fin() * 1.5F);
        Lines.circle(e.x, e.y, e.fout() * 60.0F);
    });
    public static final Effect currentChargeBegin = new Effect(260.0F, (e) -> {
        Draw.color(Pal.surge);
        Fill.circle(e.x, e.y, e.fin() * 7.0F);
        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 3.0F);
    });
    public static final Effect plasmaFragAppear = (new Effect(12.0F, (e) -> {
        Draw.color(Color.white);
        Drawf.tri(e.x, e.y, e.fin() * 12.0F, e.fin() * 13.0F, e.rotation);
    })).layer(99.99F);
    public static final Effect plasmaFragDisappear = (new Effect(12.0F, (e) -> {
        Draw.color(Pal.surge, Color.white, e.fin());
        Drawf.tri(e.x, e.y, e.fout() * 10.0F, e.fout() * 11.0F, e.rotation);
    })).layer(99.99F);
    public static final Effect surgeSplash = new Effect(40.0F, 100.0F, (e) -> {
        Draw.color(Pal.surge);
        Lines.stroke(e.fout() * 2.0F);
        Lines.circle(e.x, e.y, 4.0F + e.finpow() * 65.0F);
        Draw.color(Pal.surge);

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(e.x, e.y, 6.0F, 100.0F * e.fout(), (float)(i * 90));
        }

        Draw.color();

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(e.x, e.y, 3.0F, 35.0F * e.fout(), (float)(i * 90));
        }

    });
    public static final Effect oracleCharge = new Effect(30.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Tmp.v1.trns(Mathf.randomSeed((long)e.id, 360.0F) + Time.time, (1.0F - e.finpow()) * 20.0F);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin() * 4.5F, 45.0F);
    });
    public static final Effect oracleChargeBegin = new Effect(40.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Fill.circle(e.x, e.y, e.fin() * 6.0F);
    });
    public static final Effect monolithRingEffect = new Effect(60.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Float) {
            Float data = (Float)data$temp;
            Draw.color(Pal.lancerLaser);
            Lines.stroke(e.fout() * 3.0F * data);
            Lines.circle(e.x, e.y, e.finpow() * 24.0F * data);
        }

    });
    public static final Effect scarRailTrail = new Effect(16.0F, (e) -> {
        for(int i = 0; i < 2; ++i) {
            int sign = Mathf.signs[i];
            Draw.color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 10.0F * e.fout(), 24.0F, e.rotation + 90.0F + 90.0F * (float)sign);
            Draw.color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(10.0F * e.fout() - 4.0F, 0.0F), 20.0F, e.rotation + 90.0F + 90.0F * (float)sign);
        }

    });
    public static final Effect falseLightning = new Effect(10.0F, 500.0F, (e) -> {
        Object length$temp = e.data;
        if (length$temp instanceof Float) {
            Float length = (Float)length$temp;
            int lenInt = Mathf.round(length / 8.0F);
            Lines.stroke(3.0F * e.fout());
            Draw.color(e.color, Color.white, e.fin());

            for(int i = 0; i < lenInt; ++i) {
                float offsetXA = i == 0 ? 0.0F : Mathf.randomSeed((long)e.id + (long)i * 6413L, -4.5F, 4.5F);
                float offsetYA = length / (float)lenInt * (float)i;
                int j = i + 1;
                float offsetXB = j == lenInt ? 0.0F : Mathf.randomSeed((long)e.id + (long)j * 6413L, -4.5F, 4.5F);
                float offsetYB = length / (float)lenInt * (float)j;
                Tmp.v1.trns(e.rotation, offsetYA, offsetXA);
                Tmp.v1.add(e.x, e.y);
                Tmp.v2.trns(e.rotation, offsetYB, offsetXB);
                Tmp.v2.add(e.x, e.y);
                Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
                Fill.circle(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() / 2.0F);
            }

        }
    });
    public static final Effect forgeAbsorbPulseEffect = new Effect(124.0F, (e) -> {
        float rad = 110.0F * e.fout(Interp.pow5In);
        int sides = Lines.circleVertices(rad);
        Draw.z(111.0F);
        Draw.blend(Blending.additive);
        Tmp.c1.set(UnityPal.endColor);
        Tmp.c1.a = e.fin(Interp.pow5Out);
        Fill.light(e.x, e.y, sides, rad, Color.clear, Tmp.c1);
        Tmp.c1.a = e.fin(Interp.pow10Out) * e.fout(Interp.pow10Out);
        Fill.light(e.x, e.y, 27, 40.0F, Tmp.c1, Color.clear);
        Draw.blend();
    });
    public static final Effect forgeAbsorbEffect = new Effect(124.0F, (e) -> {
        float angle = e.rotation;
        float slope = (0.5F - Math.abs(e.finpow() - 0.5F)) * 2.0F;
        Tmp.v1.trns(angle, (1.0F - e.finpow()) * 110.0F);
        Lines.stroke(1.5F, UnityPal.endColor);
        Lines.lineAngleCenter(e.x + Tmp.v1.x, e.y + Tmp.v1.y, angle, slope * 8.0F);
    });
    public static final Effect forgeFlameEffect = (new Effect(84.0F, (e) -> {
        float fin = e.fin(Interp.pow5Out);
        float alpha = 1.0F - Mathf.curve(fin, 0.5F, 1.0F);

        for(int i = 0; i < 4; ++i) {
            float a = 90.0F * (float)i;

            for(int j = 0; j < 2; ++j) {
                float side = (float)Mathf.signs[j];
                float fa = a - 45.0F * side;
                Tmp.v1.trns(a, 7.75F * side, 19.0F);
                float s = (float)Math.sqrt((double)72.0F) / 2.0F;
                Draw.color(UnityPal.endColor);
                Draw.alpha(alpha);
                Tmp.v2.trns(fa, s, 0.0F);
                Tmp.v3.trns(fa, -s, 0.0F);
                Tmp.v4.trns(fa, 0.0F, fin * 20.0F);
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, s);
                Fill.tri(e.x + Tmp.v1.x + Tmp.v2.x, e.y + Tmp.v1.y + Tmp.v2.y, e.x + Tmp.v1.x + Tmp.v3.x, e.y + Tmp.v1.y + Tmp.v3.y, e.x + Tmp.v1.x + Tmp.v4.x, e.y + Tmp.v1.y + Tmp.v4.y);
                s = (float)Math.sqrt((double)18.0F) / 2.0F;
                Draw.color(Color.white);
                Draw.alpha(alpha);
                Tmp.v2.trns(fa, s, 0.0F);
                Tmp.v3.trns(fa, -s, 0.0F);
                Tmp.v4.trns(fa, 0.0F, fin * 16.0F);
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, s);
                Fill.tri(e.x + Tmp.v1.x + Tmp.v2.x, e.y + Tmp.v1.y + Tmp.v2.y, e.x + Tmp.v1.x + Tmp.v3.x, e.y + Tmp.v1.y + Tmp.v3.y, e.x + Tmp.v1.x + Tmp.v4.x, e.y + Tmp.v1.y + Tmp.v4.y);
            }
        }

    })).layer(35.0F);
    public static final Effect imberSparkCraftingEffect = new Effect(70.0F, (e) -> {
        Draw.color(UnityPal.imberColor, Color.valueOf("ffc266"), e.finpow());
        Draw.alpha(e.finpow());
        Angles.randLenVectors((long)e.id, 3, (1.0F - e.finpow()) * 24.0F, e.rotation, 360.0F, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, e.fout() * 8.0F, e.fout() * 10.0F, e.rotation);
            Drawf.tri(e.x + x, e.y + y, e.fout() * 4.0F, e.fout() * 6.0F, e.rotation);
        });
        Draw.color();
    });
    public static final Effect healLaser = new Effect(60.0F, (e) -> {
        Object temp$temp = e.data;
        if (temp$temp instanceof Position[]) {
            Position[] temp = (Position[])temp$temp;
            float[] reduction = new float[]{0.0F, 1.5F};
            Position a = temp[0];
            Position b = temp[1];

            for(int i = 0; i < 2; ++i) {
                Draw.color(i == 0 ? Pal.heal : Color.white);
                Lines.stroke((3.0F - reduction[i]) * e.fout());
                Lines.line(a.getX(), a.getY(), b.getX(), b.getY());
                Fill.circle(a.getX(), a.getY(), (2.5F - reduction[i]) * e.fout());
                Fill.circle(b.getX(), b.getY(), (2.5F - reduction[i]) * e.fout());
            }

        }
    });
    public static final Effect pylonLaserCharge = new Effect(200.0F, 180.0F, (e) -> {
        e.scaled(100.0F, (c) -> {
            float slope = Interp.pow3Out.apply(Mathf.mod(c.fout() * 3.0F, 1.0F));
            float rot = (float)Mathf.round(c.fout() * 4.0F);
            Draw.color(UnityPal.monolithLight);
            Fill.circle(c.x, c.y, 15.0F * c.fin());
            Draw.z(111.0F);
            Draw.blend(Blending.additive);
            Tmp.c1.set(UnityPal.monolithLight).a(c.fin(Interp.pow3Out));
            Fill.light(c.x, c.y, 27, 40.0F * c.fout(Interp.pow10Out), Tmp.c1, Color.clear);
            Tmp.c1.a((1.0F - slope) * 0.5F);
            Fill.light(c.x, c.y, 4, 80.0F * slope, Color.clear, Tmp.c1);
            Draw.blend();
        });
        if (!(e.fin() < 0.5F)) {
            float fin = Mathf.curve(e.fin(), 0.5F, 1.0F);
            float finscaled = Mathf.curve(fin, 0.0F, 0.8F);
            float fin5 = Interp.pow5Out.apply(fin);
            float fin3 = Interp.pow3Out.apply(fin);
            float fin2 = Interp.pow2Out.apply(fin);
            float fout = 1.0F - fin;
            float rot = 370.0F * fin5;
            float rad = 160.0F * Interp.pow5Out.apply(finscaled);
            Lines.stroke(3.0F * fout);

            for(int i = 0; i < 2; ++i) {
                Draw.color(UnityPal.monolithLight, UnityPal.monolith, fin);
                Lines.square(e.x, e.y, 200.0F * fin3, rot * (float)Mathf.signs[i]);
                Draw.color(UnityPal.monolith);
                Lines.square(e.x, e.y, 100.0F * fin5, rot * (float)Mathf.signs[i] + 45.0F);
            }

            Draw.color(UnityPal.monolithLight, UnityPal.monolithDark, fin);
            Angles.randLenVectors((long)e.id, 48, fin3 * 180.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 5.0F * fout));
            Draw.z(111.0F);
            Draw.blend(Blending.additive);
            Tmp.c1.set(UnityPal.monolithLight).a(1.0F - fin3);
            Fill.light(e.x, e.y, 27, 40.0F, Tmp.c1, Color.clear);
            Tmp.c1.set(UnityPal.monolithDark).a((1.0F - fin2) * 0.8F);
            Fill.light(e.x, e.y, 4, rad, Color.clear, Tmp.c1);
            Draw.blend();
        }

    });
    public static final Effect evaporateDeath = new Effect(64.0F, 800.0F, (e) -> {
        Object temp$temp = e.data;
        if (temp$temp instanceof UnitVecData) {
            UnitVecData temp = (UnitVecData)temp$temp;
            Unit unit = temp.unit;
            float curve = Interp.exp5In.apply(e.fin());
            Tmp.c1.set(Color.black);
            Tmp.c1.a = e.fout();
            Draw.color(Tmp.c1);
            Draw.rect(unit.type.region, unit.x + temp.vec.x * curve, unit.y + temp.vec.y * curve, unit.rotation - 90.0F);
        }
    });
    public static final Effect vaporation = (new Effect(23.0F, (e) -> {
        Object temp$temp = e.data;
        if (temp$temp instanceof Position[]) {
            Position[] temp = (Position[])temp$temp;
            Tmp.v1.set(temp[0]);
            Tmp.v1.lerp(temp[1], e.fin());
            Draw.color(Pal.darkFlame, Pal.darkerGray, e.fin());
            Fill.circle(Tmp.v1.x + temp[2].getX(), Tmp.v1.y + temp[2].getY(), e.fout() * 5.0F);
        }
    })).layer(115.012F);
    public static final Effect sparkleFx = new Effect(15.0F, (e) -> {
        Draw.color(Color.white, e.color, e.fin());
        integer = 1;
        Angles.randLenVectors((long)e.id, e.id % 3 + 1, e.rotation * 4.0F + 4.0F, (x, y) -> {
            UnityDrawf.spark(e.x + x, e.y + y, e.fout() * 4.0F, 0.5F + e.fout() * 2.2F, (float)(e.id * integer));
            ++integer;
        });
    });
    public static final Effect upgradeBlockFx = new Effect(90.0F, (e) -> {
        Draw.color(Color.white, Color.green, e.fin());
        Lines.stroke(e.fout() * 6.0F * e.rotation);
        Lines.square(e.x, e.y, (e.fin() * 4.0F + 2.0F) * e.rotation, 0.0F);
        integer = 1;
        Angles.randLenVectors((long)e.id, e.id % 3 + 7, e.rotation * 4.0F + 4.0F + 8.0F * e.finpow(), (x, y) -> {
            UnityDrawf.spark(e.x + x, e.y + y, e.fout() * 5.0F, e.fout() * 3.5F, (float)(e.id * integer));
            ++integer;
        });
    });
    public static final Effect imberCircleSparkCraftingEffect = new Effect(30.0F, (e) -> {
        Draw.color(Pal.surge);
        Lines.stroke(e.fslope());
        Lines.circle(e.x, e.y, e.fin() * 20.0F);
    });
    public static final Effect waitFx = (new Effect(30.0F, (e) -> {
        Object[] data = e.data;
        float whenReady = (Float)data[0];
        Unit u = (Unit)data[1];
        if (u != null && u.isValid() && !u.dead) {
            Draw.color(e.color);
            Lines.stroke(e.fout() * 1.5F);
            Lines.polySeg(60, 0, (int)(60.0F * (1.0F - (e.rotation - Time.time) / whenReady)), u.x, u.y, 8.0F, 0.0F);
        }
    })).layer(109.99999F);
    public static final Effect waitEffect = (new Effect(30.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof BaseAbility.WaitEffectData) {
            BaseAbility.WaitEffectData data = (BaseAbility.WaitEffectData)data$temp;
            if (data.unit() == null || !data.unit().isValid() || data.unit().dead) {
                return;
            }

            Draw.color(e.color);
            Lines.stroke(e.fout() * 1.5F);
            Lines.polySeg(60, 0, (int)(60.0F * data.progress()), data.unit().x, data.unit().y, 8.0F, 0.0F);
        }

    })).layer(109.99999F);
    public static final Effect waitEffect2 = (new Effect(30.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof BaseAbility.WaitEffectData) {
            BaseAbility.WaitEffectData data = (BaseAbility.WaitEffectData)data$temp;
            if (data.unit() == null || !data.unit().isValid() || data.unit().dead) {
                return;
            }

            Draw.color(e.color);
            Lines.stroke(e.fout() * 1.5F);
            Lines.polySeg(90, 0, (int)(90.0F * data.progress()), data.unit().x, data.unit().y, 12.0F, 0.0F);
        }

    })).layer(109.99999F);
    public static final Effect ringFx = new Effect(25.0F, (e) -> {
        Object u$temp = e.data;
        if (u$temp instanceof Unit) {
            Unit u = (Unit)u$temp;
            if (u.isValid() && !u.dead) {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 1.5F);
                Lines.circle(u.x, u.y, 8.0F);
            }
        }
    });
    public static final Effect ringEffect2 = new Effect(25.0F, (e) -> {
        Object unit$temp = e.data;
        if (unit$temp instanceof Unit) {
            Unit unit = (Unit)unit$temp;
            if (!unit.isValid() || unit.dead) {
                return;
            }

            Draw.color(Color.white, e.color, e.fin());
            Lines.stroke(e.fout() * 1.5F);
            Lines.circle(unit.x, unit.y, 12.0F);
        }

    });
    public static final Effect smallRingFx = new Effect(20.0F, (e) -> {
        Object u$temp = e.data;
        if (u$temp instanceof Unit) {
            Unit u = (Unit)u$temp;
            if (u.isValid() && !u.dead) {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fin());
                Lines.circle(u.x, u.y, e.fin() * 5.0F);
            }
        }
    });
    public static final Effect smallRingEffect2 = new Effect(20.0F, (e) -> {
        Object unit$temp = e.data;
        if (unit$temp instanceof Unit) {
            Unit unit = (Unit)unit$temp;
            if (!unit.isValid() || unit.dead) {
                return;
            }

            Draw.color(Color.white, e.color, e.fin());
            Lines.stroke(e.fin());
            Lines.circle(unit.x, unit.y, e.fin() * 7.5F);
        }

    });
    public static final Effect squareFx = new Effect(25.0F, (e) -> {
        Object u$temp = e.data;
        if (u$temp instanceof Unit) {
            Unit u = (Unit)u$temp;
            if (u.isValid() && !u.dead) {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 2.5F);
                Lines.square(u.x, u.y, e.fin() * 18.0F, 45.0F);
            }
        }
    });
    public static final Effect expAbsorb = new Effect(15.0F, (e) -> {
        Lines.stroke(e.fout() * 1.5F);
        Draw.color(UnityPal.exp);
        Lines.circle(e.x, e.y, e.fin() * 2.5F + 1.0F);
    });
    public static final Effect expDespawn = new Effect(15.0F, (e) -> {
        Draw.color(UnityPal.exp);
        Angles.randLenVectors((long)e.id, 7, 2.0F + 5.0F * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout()));
    });
    public static final Effect maxDamageFx = new Effect(16.0F, (e) -> {
        Draw.color(Color.orange);
        Lines.stroke(2.5F * e.fin());
        Lines.square(e.x, e.y, e.rotation * 4.0F);
    });
    public static final Effect withstandFx = new Effect(16.0F, (e) -> {
        Draw.color(Color.orange);
        Lines.stroke(1.2F * e.rotation * e.fout());
        Lines.square(e.x, e.y, e.rotation * 4.0F);
    });
    public static final Effect ahhimaLiquidNow = new Effect(45.0F, (e) -> {
        Draw.color(Color.gray, Color.clear, e.fin());
        Angles.randLenVectors((long)e.id, 3, 2.5F + e.fin() * 6.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2F + e.fin() * 3.0F));
        Draw.color(UnityPal.lava, UnityPal.lava2, e.fout());
        Angles.randLenVectors((long)(e.id + 1), 4, 1.0F + e.fin() * 4.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2F + e.fout() * 1.3F));
    });
    public static final Effect blinkFx = new Effect(30.0F, (e) -> {
        Draw.color(Color.white, UnityPal.dirium, e.fin());
        Lines.stroke(3.0F * e.rotation * e.fout());
        Lines.square(e.x, e.y, e.rotation * 4.0F * e.finpow());
    });
    public static final Effect tpOut = new Effect(30.0F, (e) -> {
        Draw.color(UnityPal.dirium);
        Lines.stroke(3.0F * e.fout());
        Lines.square(e.x, e.y, e.finpow() * e.rotation, 45.0F);
        Lines.stroke(5.0F * e.fout());
        Lines.square(e.x, e.y, e.fin() * e.rotation, 45.0F);
        Angles.randLenVectors((long)e.id, 10, e.fin() * (e.rotation + 10.0F), (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 4.0F, 100.0F * Mathf.randomSeed((long)(e.id + 1)) * e.fin()));
    });
    public static final Effect tpIn = new Effect(50.0F, (e) -> {
        Object type$temp = e.data;
        if (type$temp instanceof UnitType) {
            UnitType type = (UnitType)type$temp;
            TextureRegion region = type.fullIcon;
            Draw.color();
            Draw.mixcol(UnityPal.dirium, 1.0F);
            Draw.rect(region, e.x, e.y, (float)region.width * Draw.scl * e.fout(), (float)region.height * Draw.scl * e.fout(), e.rotation);
            Draw.mixcol();
        }
    });
    public static final Effect tpFlash = (new Effect(30.0F, (e) -> {
        Object unit$temp = e.data;
        if (unit$temp instanceof Unit) {
            Unit unit = (Unit)unit$temp;
            if (unit.isValid()) {
                TextureRegion region = unit.type.fullIcon;
                Draw.mixcol(UnityPal.diriumLight, 1.0F);
                Draw.alpha(e.fout());
                Draw.rect(region, unit.x, unit.y, unit.rotation - 90.0F);
                Draw.mixcol();
                Draw.color();
                return;
            }
        }

    })).layer(116.0F);
    public static final Effect empShockwave = new Effect(30.0F, 800.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Lines.stroke(e.fout() + 0.5F);
        Lines.circle(e.x, e.y, e.rotation * Mathf.curve(e.fin(), 0.0F, 0.23F));
    });
    public static final Effect empCharge = new Effect(70.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        UnityDrawf.shiningCircle(e.id * 63, Time.time, e.x, e.y, 4.0F * e.fin(), 7, 15.0F, 24.0F * e.fin(), 2.0F * e.fin());
        Draw.color(Color.white);
        UnityDrawf.shiningCircle(e.id * 63, Time.time, e.x, e.y, 2.0F * e.fin(), 7, 15.0F, 38.0F * e.fin(), e.fin());
        Draw.color();
    });
    public static final Effect blueTriangleTrail = new Effect(50.0F, (e) -> {
        Draw.color(Color.white, Pal.lancerLaser, e.fin());
        Fill.poly(e.x, e.y, 3, 4.0F * e.fout(), e.rotation + 180.0F);
    });
    public static final Effect advanceFlameTrail = new Effect(27.0F, (e) -> {
        Draw.color(UnityPal.advance, UnityPal.advanceDark, e.fin());
        float rot = (float)Mathf.randomSeed((long)e.id, -1, 1) * 270.0F;
        Fill.poly(e.x, e.y, 6, e.fout() * 4.1F, e.rotation + e.fin() * rot);
    });
    public static final Effect advanceFlameSmoke = new Effect(13.0F, (e) -> {
        Draw.color(Color.valueOf("4d668f77"), Color.valueOf("35455f00"), e.fin());
        float rot = (float)Mathf.randomSeed((long)e.id, -1, 1) * 270.0F;
        Angles.randLenVectors((long)e.id, 2, e.finpow() * 13.0F, e.rotation, 60.0F, (x, y) -> Fill.poly(e.x + x, e.y + y, 6, e.fout() * 4.1F, e.rotation + e.fin() * rot));
    });
    public static final Effect arcCharge = new Effect(27.0F, (e) -> {
        Draw.color(Color.valueOf("606571"), Color.valueOf("6c8fc7"), e.fin());
        Angles.randLenVectors((long)e.id, 2, e.fout() * 40.0F, e.rotation, 135.0F, (x, y) -> Fill.poly(e.x + x, e.y + y, 6, 1.0F + Mathf.sin(e.fin() * 3.0F, 1.0F, 2.0F) * 5.0F, e.rotation));
    });
    public static final Effect arcSmoke = new Effect(27.0F, (e) -> {
        Draw.color(Color.valueOf("6c8fc7"), Color.valueOf("606571"), e.fin());
        Angles.randLenVectors((long)e.id, 3, e.finpow() * 20.0F, e.rotation, 180.0F, (x, y) -> Fill.poly(e.x + x, e.y + y, 6, e.fout() * 9.0F, e.rotation));
    });
    public static final Effect arcSmoke2 = new Effect(27.0F, (e) -> {
        Draw.color(Color.valueOf("6c8fc7"), Color.valueOf("606571"), e.fin());
        Tmp.v1.trns(e.rotation, e.fin() * 4.6F * 15.0F);
        Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 6, e.fout() * 16.0F, e.rotation);
    });
    public static final Effect teamConvertedEffect = new Effect(18.0F, (e) -> {
        Draw.color(UnityPal.advance, Color.white, e.fin());
        Fill.square(e.x, e.y, 0.1F + e.fout() * 2.8F, 45.0F);
    });
    public static final Effect blueBurnEffect = new Effect(35.0F, (e) -> {
        Draw.color(UnityPal.advance, UnityPal.advanceDark, e.fin());
        Angles.randLenVectors((long)e.id, 3, 2.0F + e.fin() * 7.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.1F + e.fout() * 1.4F));
    });
    public static final Effect tenmeikiriCut = new Effect(20.0F, 150.0F, (e) -> {
        if (e.data instanceof Float) {
            Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
            Drawf.tri(e.x, e.y, 12.0F * e.fout(), (Float)e.data, e.rotation);
            Drawf.tri(e.x, e.y, 12.0F * e.fout(), (Float)e.data, e.rotation + 180.0F);
        }
    });
    public static final Effect vapourizeTile = (new Effect(126.0F, 128.0F, (e) -> {
        Draw.color(Color.red);
        Draw.blend(Blending.additive);
        Fill.square(e.x, e.y, e.fout() * e.rotation * 4.0F);
        Object turret$temp = e.data;
        if (turret$temp instanceof Turret.TurretBuild) {
            Turret.TurretBuild turret = (Turret.TurretBuild)turret$temp;
            Draw.mixcol(Color.red, 1.0F);
            Draw.alpha(e.fout());
            Draw.rect(turret.block.region, e.x, e.y, turret.rotation - 90.0F);
        }

        Draw.blend();
        Draw.mixcol();
        Draw.color();
    })).layer(111.0F);
    public static final Effect vapourizeUnit = (new Effect(126.0F, 512.0F, (e) -> {
        Draw.mixcol(Color.red, 1.0F);
        Draw.color(1.0F, 1.0F, 1.0F, e.fout());
        Draw.blend(Blending.additive);
        GraphicUtils.simpleUnitDrawer((Unit)e.data, false);
        Draw.blend();
        Draw.color();
        Draw.mixcol();
    })).layer(111.0F);
    public static final Effect endgameLaser = new Effect(76.0F, 1640.0F, (e) -> {
        if (e.data != null) {
            Color[] colors = new Color[]{Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white};
            float[] strokes = new float[]{2.0F, 1.3F, 0.6F};
            float oz = Draw.z();
            Object[] data = e.data;
            Position a = (Position)data[0];
            Position b = (Position)data[1];
            float width = (Float)data[2];
            Tmp.v1.set(a).lerp(b, Mathf.curve(e.fin(), 0.0F, 0.09F));

            for(int i = 0; i < 3; ++i) {
                Draw.z(oz + (float)i / 1000.0F);
                if (i >= 2) {
                    Draw.color(Color.white);
                } else {
                    Draw.color(Tmp.c1.set(colors[i]).mul(1.0F, 1.0F + Utils.offsetSinB(0.0F, 5.0F), 1.0F + Utils.offsetSinB(90.0F, 5.0F), 1.0F));
                }

                Fill.circle(a.getX(), a.getY(), strokes[i] * 4.0F * width * e.fout());
                Fill.circle(Tmp.v1.x, Tmp.v1.y, strokes[i] * 4.0F * width * e.fout());
                Lines.stroke(strokes[i] * 4.0F * width * e.fout());
                Lines.line(a.getX(), a.getY(), Tmp.v1.x, Tmp.v1.y);
            }

            Draw.z(oz);
        }
    });
    public static final Effect rainbowTextureTrail = new Effect(80.0F, (e) -> {
        Object t$temp = e.data;
        if (t$temp instanceof RainbowUnitType) {
            RainbowUnitType t = (RainbowUnitType)t$temp;
            Draw.blend(Blending.additive);
            Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 4.0F).a(Mathf.clamp(e.fout() * 1.5F)));
            Draw.rect(t.trailRegion, e.x, e.y, e.rotation - 90.0F);
            Draw.blend();
        }
    });
    public static final Effect kamiBulletDespawn = new Effect(60.0F, (e) -> {
        float size = Mathf.clamp(e.rotation, 0.0F, 15.0F);
        Draw.blend(Blending.additive);
        Draw.color(Tmp.c1.set(Color.red).shiftHue((e.time + Time.time) / 2.0F * 3.0F));
        Lines.stroke(2.0F * e.fout());
        Lines.circle(e.x, e.y, e.finpow() * size + size / 2.0F);
        Lines.stroke(e.fout());
        Lines.circle(e.x, e.y, e.finpow() * (size / 2.0F) + size);
        Draw.blend();
    });
    public static final Effect kamiEoLCharge = new Effect(60.0F, (e) -> {
        Object u$temp = e.data;
        if (u$temp instanceof Unit) {
            Unit u = (Unit)u$temp;
            Draw.blend(Blending.additive);

            for(int i = 0; i < 2; ++i) {
                float angle = (float)i * 360.0F / 2.0F;
                Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 5.0F + angle).a(Mathf.clamp(e.fout() * 1.5F)));
                Tmp.v1.trns(angle + e.fin() * 180.0F, 150.0F * e.fslope()).add(u);
                Draw.rect(u.type.region, Tmp.v1.x, Tmp.v1.y, u.rotation - 90.0F);
            }

            for(int i = 0; i < 4; ++i) {
                float angle = (float)i * 360.0F / 4.0F;
                Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 5.0F + angle).a(Mathf.clamp(e.fout() * 1.5F)));
                Tmp.v1.trns(angle + e.fin() * -270.0F, 100.0F * e.fslope()).add(u);
                Draw.rect(u.type.region, Tmp.v1.x, Tmp.v1.y, u.rotation - 90.0F);
            }

            Draw.blend();
        }
    });
    public static final Effect kamiCharge = new Effect(60.0F, (e) -> {
        Draw.blend(Blending.additive);
        Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 3.0F));
        e.scaled(20.0F, (s) -> {
            Lines.stroke(3.0F * Mathf.clamp(s.fin() * 2.0F));
            Lines.circle(e.x, e.y, s.fout() * 300.0F);
        });

        for(int i = 0; i < 15; ++i) {
            float fout = 1.0F - Mathf.clamp(Mathf.randomSeed((long)(e.id + i * 121), 1.0F, 2.0F) * e.fin());
            float angle = Mathf.randomSeed((long)(e.id + i * 3542), 360.0F);
            float rad = Mathf.randomSeed((long)(e.id + i * 2451), 150.0F, 300.0F);
            if (fout > 1.0E-4F) {
                Tmp.v1.trns(angle, rad * fout).add(e.x, e.y);
                float slope = (0.5F - Math.abs(fout - 0.5F)) * 2.0F;
                Draw.color(Tmp.c1.set(Color.red).shiftHue((e.time + Mathf.randomSeed((long)(e.id + i * 231), 360.0F)) * 3.0F));
                Fill.square(Tmp.v1.x, Tmp.v1.y, 13.0F * slope, 45.0F);
            }
        }

        Draw.blend();
    });
    public static final Effect kamiWarningLine = new Effect(120.0F, 1340.0F, (e) -> {
        if (e.data != null) {
            Position[] data = (Position[])e.data;
            Position a = data[0];
            Position b = data[1];
            Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 3.0F));
            Lines.stroke(Mathf.clamp(e.fslope() * 2.0F) * 1.2F);
            Lines.line(a.getX(), a.getY(), b.getX(), b.getY());
        }
    });
    public static final Effect pointBlastLaserEffect = new Effect(23.0F, 600.0F, (e) -> {
        Object btype$temp = e.data;
        if (btype$temp instanceof PointBlastLaserBulletType) {
            PointBlastLaserBulletType btype = (PointBlastLaserBulletType)btype$temp;

            for(int i = 0; i < btype.laserColors.length; ++i) {
                Draw.color(btype.laserColors[i]);
                Fill.circle(e.x, e.y, (e.rotation - btype.auraWidthReduction * (float)i) * e.fout());
            }

            Drawf.light(e.x, e.y, e.rotation * e.fout() * 3.0F, btype.laserColors[0], 0.66F);
        }
    });
    public static final Effect rockFx = new Effect(10.0F, (e) -> {
        Draw.color(Color.orange, Color.gray, e.fin());
        Lines.stroke(1.0F);
        Lines.spikes(e.x, e.y, e.fin() * 4.0F, 1.5F, 6);
    });
    public static final Effect craftFx = new Effect(10.0F, (e) -> {
        Draw.color(Pal.accent, Color.gray, e.fin());
        Lines.stroke(1.0F);
        Lines.spikes(e.x, e.y, e.fin() * 4.0F, 1.5F, 6);
    });
    public static final Effect monumentDespawn = new Effect(32.0F, (e) -> {
        e.scaled(15.0F, (i) -> {
            Draw.color(Pal.lancerLaser);
            Lines.stroke(i.fout() * 5.0F);
            Lines.circle(e.x, e.y, 4.0F + i.finpow() * 26.0F);
        });
        Angles.randLenVectors((long)e.id, 25, 5.0F + e.fin() * 80.0F, e.rotation, 60.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3.0F));
    });
    public static final Effect monumentTrail = new Effect(32.0F, (e) -> {
        float len = ((PointBulletType)UnityBullets.monumentRailBullet).trailSpacing - 12.0F;
        float rot = e.rotation;
        Tmp.v1.trns(rot, len);

        for(int i = 0; i < 2; ++i) {
            Draw.color(i < 1 ? Color.white : Pal.lancerLaser);
            float scl = i < 1 ? 1.0F : 0.5F;
            Lines.stroke(e.fout() * 10.0F * scl);
            Lines.lineAngle(e.x, e.y, rot, len, false);
            Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Lines.getStroke() * 1.22F, 12.0F * scl, rot);
            Drawf.tri(e.x, e.y, Lines.getStroke() * 1.22F, 12.0F * scl, rot + 180.0F);
        }

    });
    public static final Effect supernovaChargeBegin = new Effect(27.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Float) {
            Float data = (Float)data$temp;
            float r = data;
            Angles.randLenVectors((long)e.id, (int)(2.0F * r), 1.0F + 27.0F * e.fout(), (x, y) -> {
                Draw.color(Pal.lancerLaser);
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), (1.0F + e.fslope() * 6.0F) * r);
            });
        }

    });
    public static final Effect supernovaStarHeatwave = new Effect(40.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Lines.stroke(e.fout());
        Lines.circle(e.x, e.y, 110.0F * e.fin());
        Lines.circle(e.x, e.y, 120.0F * e.finpow() * 0.6F);
    });
    public static final Effect supernovaChargeStar = new Effect(30.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Float) {
            Float data = (Float)data$temp;
            float r = data;
            Draw.color(Pal.lancerLaser);
            Draw.alpha(e.fin() * 2.0F * r);
            Lines.circle(e.x, e.y, 150.0F * Interp.pow2Out.apply(e.fout()) * Mathf.lerp(0.1F, 1.0F, r));
        }

    });
    public static final Effect supernovaStarDecay = new Effect(56.0F, (e) -> Angles.randLenVectors((long)e.id, 1, 36.0F * e.finpow(), (x, y) -> {
        Draw.color(Pal.lancerLaser);
        Fill.rect(e.x + x, e.y + y, 2.2F * e.fout(), 2.2F * e.fout(), 45.0F);
    }));
    public static final Effect supernovaChargeStar2 = new Effect(27.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Float) {
            Float data = (Float)data$temp;
            float r = data;
            Angles.randLenVectors((long)e.id, (int)(3.0F * r), e.fout() * (90.0F + r * 150.0F) * (0.3F + Mathf.randomSeed((long)e.id, 0.7F)), (x, y) -> {
                Draw.color(Pal.lancerLaser);
                Fill.circle(e.x + x, e.y + y, 2.0F * e.fin());
            });
        }

    });
    public static final Effect supernovaPullEffect = new Effect(30.0F, 500.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Long) {
            Long data = (Long)data$temp;
            float size = e.rotation;
            float x = e.x + Mathf.randomSeedRange((long)e.id, 4.0F);
            float y = e.y + Mathf.randomSeedRange((long)(e.id + 1), 4.0F);
            long pos = SVec2.scl(SVec2.sub(data, x, y), e.fin());
            Draw.color(Pal.lancerLaser);
            Fill.circle(x + SVec2.x(pos), y + SVec2.y(pos), size * (0.5F + e.fslope() * 0.5F));
        }

    });
    public static final Effect reflectResumeDynamic = new Effect(22.0F, (e) -> {
        Draw.color(Color.valueOf("FFF3D6"));
        Lines.stroke(e.fout() * 2.0F);
        Lines.circle(e.x, e.y, Interp.pow3In.apply(e.fout()) * e.rotation);
    });
    public static final Effect reflectPulseDynamic = new Effect(22.0F, (e) -> {
        Draw.color(Color.valueOf("FFF3D6"));
        Lines.stroke(e.fout() * 2.0F);
        Lines.circle(e.x, e.y, e.finpow() * e.rotation);
    });
    public static final Effect slashEffect = new Effect(90.0F, (e) -> {
        Draw.color(Pal.lancerLaser);
        Drawf.tri(e.x, e.y, 4.0F * e.fout(), 45.0F, ((float)e.id * 57.0F + 90.0F) % 360.0F);
        Drawf.tri(e.x, e.y, 4.0F * e.fout(), 45.0F, ((float)e.id * 57.0F - 90.0F) % 360.0F);
    });
    public static final Effect teleportPos = new Effect(60.0F, (e) -> {
        Object unit$temp = e.data;
        if (unit$temp instanceof UnitType) {
            UnitType unit = (UnitType)unit$temp;
            Draw.blend(Blending.additive);
            Draw.alpha(e.fout());
            TextureRegion region = unit.fullIcon;
            float w = (float)region.width * Draw.scl * e.fout();
            float h = (float)region.height * Draw.scl * e.fout();
            Draw.rect(region, e.x, e.y, w, h, e.rotation - 90.0F);
            Draw.blend();
        }

    });
    public static final Effect distortFx = new Effect(18.0F, (e) -> {
        if (e.data instanceof Float) {
            Draw.color(Pal.lancerLaser, Pal.place, e.fin());
            Fill.square(e.x, e.y, 0.1F + e.fout() * 2.5F, (Float)e.data);
        }
    });
    public static final Effect distSplashFx = new Effect(80.0F, (e) -> {
        if (e.data instanceof Float[]) {
            Draw.color(Pal.lancerLaser, Pal.place, e.fin());
            Lines.stroke(2.0F * e.fout());
            Lines.circle(e.x, e.y, ((Float[])e.data)[0] * e.fin());
        }
    }) {
        public void at(float x, float y, float rotation, Object data) {
            if (data instanceof Float[]) {
                super.lifetime = ((Float[])data)[1];
            }

            create(this, x, y, rotation, Color.white, data);
        }
    };
    public static final Effect distStart = new Effect(45.0F, (e) -> {
        if (e.data instanceof Float) {
            float centerf = Color.clear.toFloatBits();
            float edgef = Pal.lancerLaser.cpy().a(e.fout()).toFloatBits();
            float sides = (float)(Mathf.ceil((float)Lines.circleVertices((Float)e.data) / 2.0F) * 2);
            float space = 360.0F / sides;

            for(int i = 0; (float)i < sides; i += 2) {
                float px = Angles.trnsx(space * (float)i, (Float)e.data);
                float py = Angles.trnsy(space * (float)i, (Float)e.data);
                float px2 = Angles.trnsx(space * (float)(i + 1), (Float)e.data);
                float py2 = Angles.trnsy(space * (float)(i + 1), (Float)e.data);
                float px3 = Angles.trnsx(space * (float)(i + 2), (Float)e.data);
                float py3 = Angles.trnsy(space * (float)(i + 2), (Float)e.data);
                Fill.quad(e.x, e.y, centerf, e.x + px, e.y + py, edgef, e.x + px2, e.y + py2, edgef, e.x + px3, e.y + py3, edgef);
            }

        }
    });
    public static final Effect smallChainLightning = new Effect(40.0F, 300.0F, (e) -> {
        Object p$temp = e.data;
        if (p$temp instanceof Position) {
            Position p = (Position)p$temp;
            float tx = p.getX();
            float ty = p.getY();
            float dst = Mathf.dst(e.x, e.y, tx, ty);
            Tmp.v1.set(p).sub(e.x, e.y).nor();
            float normx = Tmp.v1.x;
            float normy = Tmp.v1.y;
            float range = 6.0F;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / (float)links;
            Lines.stroke(2.5F * e.fout());
            Draw.color(Color.white, e.color, e.fin());
            Lines.beginLine();
            Lines.linePoint(e.x, e.y);
            rand.setSeed((long)e.id);

            for(int i = 0; i < links; ++i) {
                float nx;
                float ny;
                if (i == links - 1) {
                    nx = tx;
                    ny = ty;
                } else {
                    float len = (float)(i + 1) * spacing;
                    Tmp.v1.setToRandomDirection(rand).scl(range / 2.0F);
                    nx = e.x + normx * len + Tmp.v1.x;
                    ny = e.y + normy * len + Tmp.v1.y;
                }

                Lines.linePoint(nx, ny);
            }

            Lines.endLine();
        }
    });
    public static final Effect chainLightning = new Effect(30.0F, 300.0F, (e) -> {
        Object p$temp = e.data;
        if (p$temp instanceof Position) {
            Position p = (Position)p$temp;
            float tx = p.getX();
            float ty = p.getY();
            float dst = Mathf.dst(e.x, e.y, tx, ty);
            Tmp.v1.set(p).sub(e.x, e.y).nor();
            float normx = Tmp.v1.x;
            float normy = Tmp.v1.y;
            float range = 6.0F;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / (float)links;
            Lines.stroke(4.0F * e.fout());
            Draw.color(Color.white, e.color, e.fin());
            Lines.beginLine();
            Lines.linePoint(e.x, e.y);
            rand.setSeed((long)e.id);

            for(int i = 0; i < links; ++i) {
                float nx;
                float ny;
                if (i == links - 1) {
                    nx = tx;
                    ny = ty;
                } else {
                    float len = (float)(i + 1) * spacing;
                    Tmp.v1.setToRandomDirection(rand).scl(range / 2.0F);
                    nx = e.x + normx * len + Tmp.v1.x;
                    ny = e.y + normy * len + Tmp.v1.y;
                }

                Lines.linePoint(nx, ny);
            }

            Lines.endLine();
        }
    });
    public static final Effect ricochetTrailSmall = new Effect(12.0F, (e) -> Angles.randLenVectors((long)e.id, 4, e.fout() * 3.5F, (x, y) -> {
        float w = 0.3F + e.fout();
        Draw.color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Fill.rect(e.x + x, e.y + y, w, w, 45.0F);
    }));
    public static final Effect ricochetTrailMedium = new Effect(16.0F, (e) -> Angles.randLenVectors((long)e.id, 5, e.fout() * 5.0F, (x, y) -> {
        float w = 0.3F + e.fout() * 1.3F;
        Draw.color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Fill.rect(e.x + x, e.y + y, w, w, 45.0F);
    }));
    public static final Effect ricochetTrailBig = new Effect(20.0F, (e) -> Angles.randLenVectors((long)e.id, 6, e.fout() * 6.5F, (x, y) -> {
        float w = 0.3F + e.fout() * 1.7F;
        Draw.color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Fill.rect(e.x + x, e.y + y, w, w, 45.0F);
    }));
    public static final Effect plated = new Effect(30.0F, (e) -> {
        Draw.color(e.color);
        Fill.circle(e.x, e.y, e.fout() * (Float)e.data);
    });
    public static final Effect sparkBoi = new Effect(15.0F, (e) -> {
        Draw.color(e.color);

        for(int j = 0; j < 4; ++j) {
            Drawf.tri(e.x, e.y, (Float)e.data - e.fin(), (Float)e.data + 1.0F - e.fin() * ((Float)e.data + 1.0F), (float)(90 * j) + e.rotation);
        }

        Draw.color();
    });
    public static final Effect orbShot = new Effect(20.0F, (e) -> {
        Draw.color(e.color);
        Draw.alpha(e.fout());
        Lines.circle(e.x, e.y, 4.0F + e.fin() * 2.0F);
    });
}
