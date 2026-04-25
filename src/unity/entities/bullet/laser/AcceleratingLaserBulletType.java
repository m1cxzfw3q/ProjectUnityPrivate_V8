package unity.entities.bullet.laser;

import arc.func.Boolf2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import unity.util.Utils;

public class AcceleratingLaserBulletType extends BulletType {
    public float maxLength = 1000.0F;
    public float laserSpeed = 15.0F;
    public float accel = 25.0F;
    public float width = 12.0F;
    public float collisionWidth = 8.0F;
    public float fadeTime = 60.0F;
    public float fadeInTime = 8.0F;
    public float oscOffset = 1.4F;
    public float oscScl = 1.1F;
    public float pierceAmount = 4.0F;
    public boolean fastUpdateLength = true;
    public Color[] colors;
    public Boolf2<Bullet, Building> buildingInsulator;
    public Boolf2<Bullet, Unit> unitInsulator;

    public AcceleratingLaserBulletType(float damage) {
        super(0.0F, damage);
        this.colors = new Color[]{Color.valueOf("ec745855"), Color.valueOf("ec7458aa"), Color.valueOf("ff9c5a"), Color.white};
        this.buildingInsulator = (b, building) -> building.block.absorbLasers || building.health > this.damage * this.buildingDamageMultiplier / 2.0F;
        this.unitInsulator = (b, unit) -> unit.health > this.damage / 2.0F && unit.hitSize > this.width;
        this.despawnEffect = Fx.none;
        this.collides = false;
        this.pierce = true;
        this.impact = true;
        this.keepVelocity = false;
        this.hittable = false;
        this.absorbable = false;
    }

    public float estimateDPS() {
        return this.damage * (this.lifetime / 2.0F) / 5.0F * 3.0F;
    }

    public float continuousDamage() {
        return this.damage / 5.0F * 60.0F;
    }

    public float range() {
        return this.maxRange > 0.0F ? this.maxRange : this.maxLength / 1.5F;
    }

    public void init() {
        super.init();
        this.drawSize = this.maxLength * 2.0F;
        this.despawnHit = false;
    }

    public void draw(Bullet b) {
        float fadeIn = this.fadeInTime <= 0.0F ? 1.0F : Mathf.clamp(b.time / this.fadeInTime);
        float fade = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F) * fadeIn;
        float tipHeight = this.width / 2.0F;
        Lines.lineAngle(b.x, b.y, b.rotation(), b.fdata);

        for(int i = 0; i < this.colors.length; ++i) {
            float f = (float)(this.colors.length - i) / (float)this.colors.length;
            float w = f * (this.width + Mathf.absin(Time.time + (float)i * this.oscOffset, this.oscScl, this.width / 4.0F)) * fade;
            Tmp.v2.trns(b.rotation(), b.fdata - tipHeight).add(b);
            Tmp.v1.trns(b.rotation(), this.width * 2.0F).add(Tmp.v2);
            Draw.color(this.colors[i]);
            Fill.circle(b.x, b.y, w / 2.0F);
            Lines.stroke(w);
            Lines.line(b.x, b.y, Tmp.v2.x, Tmp.v2.y, false);

            for(int s : Mathf.signs) {
                Tmp.v3.trns(b.rotation(), w * -0.7F, w * (float)s);
                Fill.tri(Tmp.v2.x, Tmp.v2.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x + Tmp.v3.x, Tmp.v2.y + Tmp.v3.y);
            }
        }

        Tmp.v2.trns(b.rotation(), b.fdata + tipHeight).add(b);
        Drawf.light(b.team, b.x, b.y, Tmp.v2.x, Tmp.v2.y, this.width * 2.0F, this.colors[0], 0.5F);
        Draw.reset();
    }

    public void drawLight(Bullet b) {
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new LaserData();
    }

    public void update(Bullet b) {
        boolean timer = b.timer(0, 5.0F);
        if (b.data instanceof LaserData) {
            LaserData vec = (LaserData)b.data;
            if (vec.restartTime >= 5.0F) {
                if (this.accel > 0.01F) {
                    vec.velocity = Mathf.clamp(vec.velocityTime / this.accel + vec.velocity, 0.0F, this.laserSpeed);
                    b.fdata = Mathf.clamp(b.fdata + vec.velocity * Time.delta, 0.0F, this.maxLength);
                    vec.velocityTime += Time.delta;
                } else if (timer) {
                    b.fdata = this.maxLength;
                }
            } else {
                vec.restartTime += Time.delta;
                if (this.fastUpdateLength && vec.target != null) {
                    vec.pierceOffsetSmooth = Mathf.lerpDelta(vec.pierceOffsetSmooth, vec.pierceOffset, 0.2F);
                    Tmp.v2.trns(b.rotation(), this.maxLength * 1.5F).add(b);
                    float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, vec.target.getX(), vec.target.getY());
                    b.fdata = b.dst(vec.target) - vec.targetSize + dst + this.pierceAmount + vec.pierceOffsetSmooth * vec.targetSize;
                }
            }
        }

        if (timer) {
            boolean p = this.pierceCap > 0;
            Tmp.v1.trns(b.rotation(), b.fdata).add(b);
            if (b.data instanceof LaserData) {
                LaserData data = (LaserData)b.data;
                if (p) {
                    data.pierceScore = 0.0F;
                    data.pierceOffset = 0.0F;
                }

                Utils.collideLineRawEnemyRatio(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.collisionWidth, (building, ratio, direct) -> {
                    boolean h = this.buildingInsulator.get(b, building);
                    if (direct) {
                        if (h) {
                            if (p) {
                                data.pierceScore += (float)building.block.size * (building.block.absorbLasers ? 3.0F : 1.0F) * ratio;
                            }

                            if (!p || data.pierceScore >= (float)this.pierceCap) {
                                Tmp.v2.trns(b.rotation(), this.maxLength * 1.5F).add(b);
                                float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, building.x, building.y);
                                data.velocity = 0.0F;
                                data.restartTime = 0.0F;
                                data.velocityTime = 0.0F;
                                data.pierceOffset = 1.0F - Mathf.clamp(data.pierceScore - (float)this.pierceCap);
                                if (this.fastUpdateLength) {
                                    if (building != data.target) {
                                        data.pierceOffsetSmooth = data.pierceOffset;
                                    }

                                    data.target = building;
                                    data.targetSize = (float)(building.block.size * 8) / 2.0F;
                                }

                                b.fdata = b.dst(building) - (float)(building.block.size * 8) / 2.0F + dst + this.pierceAmount + data.pierceOffsetSmooth * data.targetSize;
                            }
                        }

                        building.damage(this.damage * this.buildingDamageMultiplier * ratio);
                    }

                    return !p ? h : data.pierceScore >= (float)this.pierceCap;
                }, (unit, ratio) -> {
                    boolean h = this.unitInsulator.get(b, unit);
                    if (h) {
                        if (p) {
                            data.pierceScore += (unit.hitSize / 8.0F / 2.0F + unit.health / 4000.0F) * ratio;
                        }

                        if (!p || data.pierceScore >= (float)this.pierceCap) {
                            Tmp.v2.trns(b.rotation(), this.maxLength * 1.5F).add(b);
                            float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, unit.x, unit.y);
                            data.velocity = 0.0F;
                            data.restartTime = 0.0F;
                            data.velocityTime = 0.0F;
                            data.pierceOffset = 1.0F - Mathf.clamp(data.pierceScore - (float)this.pierceCap);
                            if (this.fastUpdateLength) {
                                if (unit != data.target) {
                                    data.pierceOffsetSmooth = data.pierceOffset;
                                }

                                data.target = unit;
                                data.targetSize = unit.hitSize / 2.0F;
                            }

                            b.fdata = b.dst(unit) - unit.hitSize / 2.0F + dst + this.pierceAmount + data.pierceOffsetSmooth * data.targetSize;
                        }
                    }

                    this.hitEntityAlt(b, unit, b.damage * ratio);
                    return !p ? h : data.pierceScore >= (float)this.pierceCap;
                }, (ex, ey) -> this.hit(b, ex, ey));
            }
        }

    }

    void hitEntityAlt(Bullet b, Unit unit, float damage) {
        unit.damage(damage);
        Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F);
        if (this.impact) {
            Tmp.v3.setAngle(b.rotation() + (this.knockback < 0.0F ? 180.0F : 0.0F));
        }

        unit.impulse(Tmp.v3);
        unit.apply(this.status, this.statusDuration);
    }

    public static class LaserData {
        public float lastLength;
        public float lightningTime;
        public float velocity;
        public float velocityTime;
        public float targetSize;
        public float pierceOffset;
        public float pierceOffsetSmooth;
        public float pierceScore;
        public float restartTime = 5.0F;
        public Position target;
    }
}
