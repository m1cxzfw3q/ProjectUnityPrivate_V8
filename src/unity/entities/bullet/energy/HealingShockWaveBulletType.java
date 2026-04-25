package unity.entities.bullet.energy;

import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import unity.util.Utils;

public class HealingShockWaveBulletType extends BulletType {
    public int segments = 64;
    public float width = 17.0F;
    public float shockwaveSpeed = 8.0F;
    public Color color;
    public StatusEffect allyStatus;
    public float allyStatusDuration;
    public float fadeTime;

    public HealingShockWaveBulletType(float damage) {
        super(0.0F, damage);
        this.color = Pal.heal;
        this.allyStatus = StatusEffects.overclock;
        this.allyStatusDuration = 300.0F;
        this.fadeTime = 20.0F;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
        this.reflectable = false;
        this.despawnEffect = this.shootEffect = this.smokeEffect = Fx.none;
        this.hitEffect = Fx.hitLaser;
        this.hitColor = Pal.heal;
    }

    public void init() {
        super.init();
        this.drawSize = this.shockwaveSpeed * this.lifetime * 2.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new HealingShockWaveData(b);
    }

    public void removed(Bullet b) {
        super.removed(b);
        Object var3 = b.data;
        if (var3 instanceof HealingShockWaveData) {
            HealingShockWaveData data = (HealingShockWaveData)var3;
            data.remove();
        }

    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof HealingShockWaveData) {
            HealingShockWaveData data = (HealingShockWaveData)var3;
            float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);
            Draw.color(this.color);
            Lines.stroke(this.width * fout);
            Lines.beginLine();

            for(int i = 0; i < data.posData.length; ++i) {
                ShockWavePositionData ad = data.posData[i];
                ShockWavePositionData bd = data.posData[Mathf.mod(i + 1, data.posData.length)];
                Lines.linePoint(ad.x, ad.y);
                Drawf.light(b.team, ad.x, ad.y, bd.x, bd.y, this.width * 3.0F, this.color, 0.3F * fout);
            }

            Lines.endLine(true);
            Draw.color();
        }

    }

    public void drawLight(Bullet b) {
    }

    public void update(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof HealingShockWaveData) {
            HealingShockWaveData data = (HealingShockWaveData)var3;

            for(int i = 0; i < data.posData.length; ++i) {
                ShockWavePositionData d = data.posData[i];
                float ang = (float)i * 360.0F / (float)data.posData.length;
                if (!d.hit) {
                    d.x += Angles.trnsx(ang, this.shockwaveSpeed) * Time.delta;
                    d.y += Angles.trnsy(ang, this.shockwaveSpeed) * Time.delta;
                }

                Building ins = Vars.world.buildWorld(d.x, d.y);
                if (ins != null && ins.block.absorbLasers) {
                    d.hit = true;
                }
            }

            if (b.timer(1, 5.0F)) {
                int idx = 0;
                float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);

                for(ShockWavePositionData d : data.posData) {
                    ShockWavePositionData a = data.posData[Mathf.mod(idx + 1, data.posData.length)];
                    if (!d.hit && !a.hit) {
                        Utils.collideLineRaw(d.x, d.y, a.x, a.y, this.width * fout, (bd) -> true, (u) -> true, (building, direct) -> {
                            if (direct && data.collided.add(building.id)) {
                                if (building.team == b.team) {
                                    building.heal(this.healPercent / 100.0F * building.maxHealth);
                                } else {
                                    building.damage(this.damage);
                                }
                            }

                            return false;
                        }, (unit) -> {
                            if (data.collided.add(unit.id)) {
                                if (unit.team == b.team) {
                                    unit.heal(this.healPercent / 100.0F * unit.maxHealth);
                                    unit.apply(this.allyStatus, this.allyStatusDuration);
                                } else {
                                    unit.damage(this.damage);
                                    unit.apply(this.status, this.statusDuration);
                                }
                            }

                            return false;
                        }, (Floatf)null, (ex, ey) -> this.hit(b, ex, ey), true);
                    }

                    ++idx;
                }
            }
        }

    }

    class HealingShockWaveData {
        ShockWavePositionData[] posData;
        IntSet collided;

        HealingShockWaveData(Bullet b) {
            this.posData = new ShockWavePositionData[HealingShockWaveBulletType.this.segments];
            this.collided = new IntSet(409);

            for(int i = 0; i < this.posData.length; ++i) {
                ShockWavePositionData d = (ShockWavePositionData)Pools.obtain(ShockWavePositionData.class, ShockWavePositionData::new);
                d.x = b.x;
                d.y = b.y;
                d.hit = false;
                this.posData[i] = d;
            }

        }

        void remove() {
            for(ShockWavePositionData d : this.posData) {
                Pools.free(d);
            }

        }
    }

    static class ShockWavePositionData {
        float x;
        float y;
        boolean hit = false;
    }
}
