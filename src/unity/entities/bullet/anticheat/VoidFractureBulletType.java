package unity.entities.bullet.anticheat;

import arc.audio.Sound;
import arc.func.Floatc2;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import unity.content.effects.HitFx;
import unity.content.effects.SpecialFx;
import unity.gen.UnitySounds;
import unity.util.Utils;

public class VoidFractureBulletType extends AntiCheatBulletTypeBase {
    public float length = 28.0F;
    public float width = 12.0F;
    public float widthTo = 3.0F;
    public float delay = 30.0F;
    public float targetingRange = 320.0F;
    public float trueSpeed;
    public float nextLifetime = 10.0F;
    public int maxTargets = 15;
    public float spikesRange = 100.0F;
    public float spikesDamage = 200.0F;
    public float spikesRand = 8.0F;
    public Sound activeSound;
    public Sound spikesSound;
    public Effect spikeEffect;
    private static float s;
    private static int in;

    public VoidFractureBulletType(float speed, float damage) {
        super(4.3F, damage);
        this.activeSound = UnitySounds.fractureShoot;
        this.spikesSound = UnitySounds.spaceFracture;
        this.spikeEffect = HitFx.voidHit;
        this.drag = 0.11F;
        this.trueSpeed = speed;
        this.collides = false;
        this.collidesTiles = false;
        this.keepVelocity = false;
        this.layer = 110.03F;
        this.pierce = true;
        this.despawnEffect = Fx.none;
        this.smokeEffect = Fx.none;
        this.hitEffect = HitFx.voidHit;
    }

    public float range() {
        return this.trueSpeed * this.nextLifetime;
    }

    public void init() {
        super.init();
        this.drawSize = this.range() * 2.0F + 30.0F;
    }

    public void update(Bullet b) {
        super.update(b);
        Object var3 = b.data;
        if (var3 instanceof FractureData) {
            FractureData data = (FractureData)var3;
            if (b.fdata <= 0.0F) {
                if (data.target != null && !data.target.isValid()) {
                    data.target = null;
                }

                if (data.target == null && b.timer(1, 5.0F)) {
                    s = Float.MAX_VALUE;
                    Floatf<Healthc> score = (h) -> h.dst2(b) + Mathf.pow(Angles.angleDist(b.rotation(), b.angleTo(h)), 4.0F);
                    Units.nearbyEnemies(b.team, b.x, b.y, this.targetingRange, (u) -> {
                        float c = score.get(u);
                        if (u.isValid() && Angles.within(b.rotation(), b.angleTo(u), 45.0F) && c < s) {
                            s = c;
                            data.target = u;
                        }

                    });
                    Vars.indexer.allBuildings(b.x, b.y, this.targetingRange, (build) -> {
                        if (build.team != b.team && Angles.within(b.rotation(), b.angleTo(build), 45.0F)) {
                            float c = score.get(build);
                            if (c < s) {
                                s = c;
                                data.target = build;
                            }
                        }

                    });
                }

                if (data.target != null) {
                    b.rotation(Mathf.slerpDelta(b.rotation(), b.angleTo(data.target), 0.1F));
                }

                if (b.time >= this.delay) {
                    b.time = 0.0F;
                    b.lifetime = this.nextLifetime;
                    b.fdata = 1.0F;
                    b.drag = 0.0F;
                    b.vel.trns(b.rotation(), this.trueSpeed);
                    data.x = b.x;
                    data.y = b.y;
                    this.activeSound.at(b.x, b.y, Mathf.random(0.9F, 1.1F));
                }
            } else {
                Utils.collideLineRawEnemy(b.team, b.lastX, b.lastY, b.x, b.y, 3.0F, (build, direct) -> {
                    if (direct) {
                        if (data.collided.add(build.id)) {
                            this.hitBuildingAntiCheat(b, build);
                        }

                        if (build.block.absorbLasers) {
                            Tmp.v1.set(b.x, b.y).sub(b.lastX, b.lastY).setLength2(build.dst2(b.lastX, b.lastY)).add(b.lastX, b.lastY);
                            b.x = Tmp.v1.x;
                            b.y = Tmp.v1.y;
                            b.vel.setZero();
                        }
                    }

                    return build.block.absorbLasers;
                }, (unit) -> {
                    if (data.collided.add(unit.id)) {
                        this.hitUnitAntiCheat(b, unit);
                    }

                    return false;
                }, (ex, ey) -> this.hit(b, ex, ey), true);
            }
        }

    }

    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        if (entity instanceof Healthc) {
            Healthc h = (Healthc)entity;
            h.damage(Math.max(b.damage, h.maxHealth() * this.ratioDamage));
        }

        if (entity instanceof Unit) {
            Unit unit = (Unit)entity;
            Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F);
            if (this.impact) {
                Tmp.v3.setAngle(b.rotation() + (this.knockback < 0.0F ? 180.0F : 0.0F));
            }

            unit.impulse(Tmp.v3);
            unit.apply(this.status, this.statusDuration);
        }

    }

    public void removed(Bullet b) {
        super.removed(b);
        if (b.fdata >= 1.0F) {
            Object var3 = b.data;
            if (var3 instanceof FractureData) {
                FractureData data = (FractureData)var3;
                SpecialFx.VoidFractureData d = new SpecialFx.VoidFractureData();
                d.x = data.x;
                d.y = data.y;
                d.x2 = b.x;
                d.y2 = b.y;
                d.b = this;
                in = 0;
                if (!b.hit) {
                    Utils.collideLineRawEnemy(b.team, d.x, d.y, d.x2, d.y2, this.spikesRange, (build, direct) -> {
                        if (direct && in < this.maxTargets) {
                            float s = build.hitSize() / 2.0F;
                            ++in;
                            build.damagePierce(this.spikesDamage);
                            Vec2 v = Intersector.nearestSegmentPoint(d.x, d.y, d.x2, d.y2, build.x + Mathf.range(this.spikesRand), build.y + Mathf.range(this.spikesRand), Tmp.v1);
                            Tmp.v2.set(v).sub(build).limit2(s * s).add(build);
                            this.hitEffect.at(Tmp.v2);
                            this.spikeEffect.at(v, v.angleTo(build));
                            d.spikes.add(v.x, v.y, build.x, build.y);
                        }

                        return false;
                    }, (unit) -> {
                        if (in < this.maxTargets) {
                            float s = unit.hitSize / 2.0F;
                            ++in;
                            unit.damagePierce(this.spikesDamage);
                            unit.apply(this.status, this.statusDuration);
                            Vec2 v = Intersector.nearestSegmentPoint(d.x, d.y, d.x2, d.y2, unit.x + Mathf.range(this.spikesRand), unit.y + Mathf.range(this.spikesRand), Tmp.v1);
                            Tmp.v2.set(v).sub(unit).limit2(s * s).add(unit);
                            this.hitEffect.at(Tmp.v2);
                            this.spikeEffect.at(v, v.angleTo(unit));
                            d.spikes.add(v.x, v.y, unit.x, unit.y);
                        }

                        return false;
                    }, (h) -> Intersector.distanceSegmentPoint(d.x, d.y, d.x2, d.y2, h.getX(), h.getY()), (Floatc2)null, false);
                    if (!d.spikes.isEmpty()) {
                        this.spikesSound.at((d.x + d.x2) / 2.0F, (d.y + d.y2) / 2.0F, Mathf.random(0.9F, 1.1F));
                    }
                }

                SpecialFx.voidFractureEffect.at((d.x + d.x2) / 2.0F, (d.y + d.y2) / 2.0F, 0.0F, d);
            }
        }

    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof FractureData) {
            FractureData data = (FractureData)var3;
            Draw.color(Color.black);
            if (b.fdata <= 0.0F) {
                float in = Mathf.clamp(b.time / this.delay);
                Drawf.tri(b.x, b.y, this.width * in, this.length, b.rotation());
                Drawf.tri(b.x, b.y, this.width * in, this.length, b.rotation() + 180.0F);
            } else {
                Drawf.tri(b.x, b.y, this.width * b.fout(), this.length, b.rotation());
                Drawf.tri(b.x, b.y, this.width * b.fout(), this.length / 2.0F, b.rotation() + 180.0F);
                float ang = b.dst2(data.x, data.y) >= 1.0E-4F ? b.angleTo(data.x, data.y) : b.rotation() + 180.0F;

                for(int i = 0; i < 3; ++i) {
                    float f = Mathf.lerp(this.width, this.widthTo, (float)i / 2.0F);
                    float a = Mathf.lerp(0.25F, 1.0F, (float)i / 2.0F * ((float)i / 2.0F));
                    Draw.alpha(a);
                    Lines.stroke(f);
                    Lines.line(data.x, data.y, b.x, b.y, false);
                    Drawf.tri(b.x, b.y, f * 1.22F, f * 2.0F, ang + 180.0F);
                    Drawf.tri(data.x, data.y, f * 1.22F, f * 2.0F, ang);
                }
            }
        }

    }

    public void drawLight(Bullet b) {
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new FractureData();
    }

    static class FractureData {
        Healthc target;
        float x;
        float y;
        IntSet collided = new IntSet();
    }
}
