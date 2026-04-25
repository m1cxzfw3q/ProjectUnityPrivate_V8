package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import unity.content.effects.HitFx;
import unity.entities.bullet.laser.AcceleratingLaserBulletType;
import unity.entities.effects.UnitCutEffect;
import unity.graphics.UnityPal;
import unity.mod.AntiCheat;
import unity.util.Utils;

public class EndCutterLaserBulletType extends AntiCheatBulletTypeBase {
    public float maxLength = 1000.0F;
    public float laserSpeed = 15.0F;
    public float accel = 25.0F;
    public float width = 12.0F;
    public float antiCheatScl = 1.0F;
    public float fadeTime = 60.0F;
    public float fadeInTime = 8.0F;
    public Color[] colors;
    private boolean hit;

    public EndCutterLaserBulletType(float damage) {
        super(0.005F, damage);
        this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
        this.hit = false;
        this.despawnEffect = Fx.none;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public float estimateDPS() {
        return this.damage * (this.lifetime / 2.0F) / 5.0F * 3.0F;
    }

    public float range() {
        return this.maxLength / 2.0F;
    }

    public void draw(Bullet b) {
        float fade = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F) * Mathf.clamp(b.time / this.fadeInTime);
        float tipHeight = this.width / 2.0F;
        Lines.lineAngle(b.x, b.y, b.rotation(), b.fdata);

        for(int i = 0; i < this.colors.length; ++i) {
            float f = (float)(this.colors.length - i) / (float)this.colors.length;
            float w = f * (this.width + Mathf.absin(Time.time + (float)i * 1.4F, 1.1F, this.width / 4.0F)) * fade;
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
        b.data = new AcceleratingLaserBulletType.LaserData();
    }

    public void update(Bullet b) {
        if (b.data instanceof AcceleratingLaserBulletType.LaserData) {
            AcceleratingLaserBulletType.LaserData vec = (AcceleratingLaserBulletType.LaserData)b.data;
            if (vec.restartTime >= 5.0F) {
                vec.velocity = Mathf.clamp(vec.velocityTime / this.accel + vec.velocity, 0.0F, this.laserSpeed);
                b.fdata = Mathf.clamp(b.fdata + vec.velocity * Time.delta, 0.0F, this.maxLength);
                vec.velocityTime += Time.delta;
            } else {
                vec.restartTime += Time.delta;
            }
        }

        if (b.timer(0, 5.0F)) {
            this.hit = false;
            Tmp.v1.trns(b.rotation(), b.fdata).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, (building, direct) -> {
                if (this.hit) {
                    return true;
                } else {
                    if (direct) {
                        if (building.health > this.damage * this.buildingDamageMultiplier * 0.5F) {
                            Tmp.v2.trns(b.rotation(), this.maxLength * 1.5F).add(b);
                            float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, building.x, building.y);
                            b.fdata = b.dst(building) - (float)(building.block.size * 8) / 2.0F + dst + 4.0F;
                            if (b.data instanceof AcceleratingLaserBulletType.LaserData) {
                                AcceleratingLaserBulletType.LaserData data = (AcceleratingLaserBulletType.LaserData)b.data;
                                data.velocity = 0.0F;
                                data.restartTime = 0.0F;
                                data.velocityTime = 0.0F;
                            }

                            Tmp.v2.trns(b.rotation(), b.fdata).add(b);

                            for(int i = 0; i < 2; ++i) {
                                HitFx.tenmeikiriTipHit.at(Tmp.v2.x + Mathf.range(4.0F), Tmp.v2.y + Mathf.range(4.0F), b.rotation() + 180.0F);
                            }

                            this.hitBuildingAntiCheat(b, building);
                            this.hit = true;
                            return true;
                        }

                        this.hitBuildingAntiCheat(b, building);
                    }

                    return false;
                }
            }, (unit) -> {
                if (!this.hit) {
                    if (unit.health > this.damage) {
                        Tmp.v2.trns(b.rotation(), this.maxLength * 1.5F).add(b);
                        float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, unit.x, unit.y);
                        b.fdata = b.dst(unit) - unit.hitSize / 2.0F + dst + 4.0F;
                        if (b.data instanceof AcceleratingLaserBulletType.LaserData) {
                            AcceleratingLaserBulletType.LaserData data = (AcceleratingLaserBulletType.LaserData)b.data;
                            data.velocity = 0.0F;
                            data.restartTime = 0.0F;
                            data.velocityTime = 0.0F;
                        }

                        Tmp.v2.trns(b.rotation(), b.fdata).add(b);

                        for(int i = 0; i < 2; ++i) {
                            HitFx.tenmeikiriTipHit.at(Tmp.v2.x + Mathf.range(4.0F), Tmp.v2.y + Mathf.range(4.0F), b.rotation() + 180.0F);
                        }

                        this.hit = true;
                    }

                    this.hitUnitAntiCheat(b, unit);
                    if ((unit.dead || unit.health >= Float.MAX_VALUE) && (unit.hitSize >= 30.0F || unit.health >= Float.MAX_VALUE)) {
                        AntiCheat.annihilateEntity(unit, true);
                        Tmp.v2.trns(b.rotation(), this.maxLength * 1.5F).add(b);
                        UnitCutEffect.createCut(unit, b.x, b.y, Tmp.v2.x, Tmp.v2.y);
                    }

                }
            }, (ex, ey) -> this.hitEffect.at(ex, ey, b.rotation()), true);
        }

        if (b.data instanceof AcceleratingLaserBulletType.LaserData) {
            AcceleratingLaserBulletType.LaserData vec = (AcceleratingLaserBulletType.LaserData)b.data;
            if (vec.lightningTime >= 1.0F && b.fdata > vec.lastLength) {
                int dst = Math.max(Mathf.round((b.fdata - vec.lastLength) / 5.0F), 1);

                for(int i = 0; i < dst; ++i) {
                    float f = Mathf.lerp(vec.lastLength, b.fdata, (float)i / (float)dst);
                    Tmp.v1.trns(b.rotation(), f).add(b);
                    Lightning.create(b.team, this.lightningColor, this.lightningDamage, Tmp.v1.x, Tmp.v1.y, b.rotation() + Mathf.range(20.0F), this.lightningLength);
                }

                --vec.lightningTime;
                vec.lastLength = b.fdata;
            }

            vec.lightningTime += Time.delta;
        }

    }

    public void init() {
        super.init();
        this.drawSize = this.maxLength * 2.0F;
    }
}
