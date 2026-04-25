package unity.entities.bullet.energy;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Puddles;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import unity.graphics.UnityDrawf;
import unity.util.Utils;

public class LightningTurretBulletType extends BulletType {
    public float range = 100.0F;
    public float reload = 15.0F;
    public float duration = 120.0F;
    public float size = 9.0F;
    public Effect lightningEffect;
    public Sound lightningSound;
    public Color color;
    private static Healthc tmp;

    public LightningTurretBulletType(float speed, float damage) {
        super(speed, damage);
        this.lightningEffect = Fx.chainLightning;
        this.lightningSound = Sounds.none;
        this.color = Pal.lancerLaser;
        this.scaleVelocity = true;
    }

    public void update(Bullet b) {
        if (b.fdata <= 0.0F) {
            super.update(b);
        } else if (b.timer(1, this.reload)) {
            Seq<Healthc> seq = Utils.nearbyEnemySorted(b.team, b.x, b.y, this.range, 1.0F);

            for(int i = 0; i < Math.min(seq.size, this.lightning); ++i) {
                tmp = (Healthc)seq.get(i);
                Vars.world.raycastEachWorld(b.x, b.y, tmp.x(), tmp.y(), (cx, cy) -> {
                    Building bl = Vars.world.build(cx, cy);
                    if (bl != null && bl.block.absorbLasers) {
                        tmp = bl;
                        return true;
                    } else {
                        return false;
                    }
                });
                this.lightningSound.at(b.x, b.y, Mathf.random(0.9F, 1.1F));
                this.lightningEffect.at(b.x, b.y, 0.0F, this.lightningColor, tmp);
                tmp.damage(this.lightningDamage);
                this.hit(b, tmp.x(), tmp.y());
                Healthc var5 = tmp;
                if (var5 instanceof Unit) {
                    Unit u = (Unit)var5;
                    u.apply(this.status, this.statusDuration);
                }
            }

            seq.clear();
        }

    }

    public void despawned(Bullet b) {
        if (b.fdata > 0.0F) {
            super.despawned(b);
        } else {
            this.hit(b, b.x, b.y);
        }

    }

    public void hit(Bullet b, float x, float y) {
        this.hitEffect.at(x, y, b.rotation(), this.hitColor);
        this.hitSound.at(x, y, this.hitSoundPitch, this.hitSoundVolume);
        Effect.shake(this.hitShake, this.hitShake, b);
        if (b.fdata > 0.0F) {
            if (this.fragBullet != null) {
                for(int i = 0; i < this.fragBullets; ++i) {
                    float len = Mathf.random(1.0F, 7.0F);
                    float a = b.rotation() + Mathf.range(this.fragCone / 2.0F) + this.fragAngle;
                    this.fragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(this.fragVelocityMin, this.fragVelocityMax), Mathf.random(this.fragLifeMin, this.fragLifeMax));
                }
            }

            if (this.puddleLiquid != null && this.puddles > 0) {
                for(int i = 0; i < this.puddles; ++i) {
                    Tile tile = Vars.world.tileWorld(x + Mathf.range(this.puddleRange), y + Mathf.range(this.puddleRange));
                    Puddles.deposit(tile, this.puddleLiquid, this.puddleAmount);
                }
            }

            if (this.incendChance > 0.0F && Mathf.chance((double)this.incendChance)) {
                Damage.createIncend(x, y, this.incendSpread, this.incendAmount);
            }

            if (this.splashDamageRadius > 0.0F && !b.absorbed) {
                Damage.damage(b.team, x, y, this.splashDamageRadius, this.splashDamage * b.damageMultiplier(), this.collidesAir, this.collidesGround);
                if (this.status != StatusEffects.none) {
                    Damage.status(b.team, x, y, this.splashDamageRadius, this.status, this.statusDuration, this.collidesAir, this.collidesGround);
                }

                if (this.healPercent > 0.0F) {
                    Vars.indexer.eachBlock(b.team, x, y, this.splashDamageRadius, Building::damaged, (other) -> {
                        Fx.healBlockFull.at(other.x, other.y, (float)other.block.size, Pal.heal);
                        other.heal(this.healPercent / 100.0F * other.maxHealth());
                    });
                }

                if (this.makeFire) {
                    Vars.indexer.eachBlock((Team)null, x, y, this.splashDamageRadius, (other) -> other.team != b.team, (other) -> Fires.create(other.tile));
                }
            }
        } else {
            Bullet n = this.create(b, b.x, b.y, b.rotation());
            n.vel.setZero();
            n.fdata = 1.0F;
            n.lifetime = this.duration;
        }

    }

    public void draw(Bullet b) {
        super.draw(b);
        Draw.color(this.color);
        Fill.circle(b.x, b.y, this.size);
        if (b.fdata <= 0.0F) {
            Draw.color(Color.white);
            Fill.circle(b.x, b.y, this.size / 2.0F);
        } else {
            float in = Mathf.clamp(b.time / 15.0F) * this.range;
            float fin = b.time % this.reload / this.reload * this.size;
            Lines.stroke(1.5F);
            UnityDrawf.dashCircleAngle(b.x, b.y, in, b.time / 20.0F * (float)Mathf.signs[Mathf.randomSeed((long)b.id, 0, 1)]);
            Draw.color(Color.white);
            Lines.circle(b.x, b.y, fin);
            Fill.circle(b.x, b.y, this.size * 0.05F);
        }

    }
}
