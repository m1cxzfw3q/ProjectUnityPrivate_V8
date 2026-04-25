package unity.entities.abilities;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import unity.content.UnityFx;

public class BulletReflectPulseAbility extends Ability {
    protected int maxReflect = 1;
    protected float reload = 100.0F;
    protected float range = 60.0F;
    protected float reflectTime = 60.0F;
    protected float maxDamage = 100.0F;
    protected Effect healEffect;
    protected Effect pauseEffect;
    protected Effect resumeEffect;
    protected Effect activeEffect;
    protected float reloadTimer;
    protected float timer;
    protected boolean active;
    protected Seq<Bullet> bullets;
    protected Seq<Object> bulletTimes;
    protected Seq<Object> bulletSpeeds;
    protected float dmg;

    protected float bulletDamage(Bullet b) {
        BulletType type = b.type;
        float dmg;
        if (type.fragBullet == null) {
            dmg = b.damage + type.splashDamage + type.lightningDamage * (float)type.lightning * (float)type.lightningLength;
        } else {
            dmg = b.damage + type.splashDamage + type.lightningDamage * (float)type.lightning * (float)type.lightningLength + this.bulletDamage(type.fragBullet) * (float)type.fragBullets;
        }

        return type instanceof ContinuousLaserBulletType ? dmg * b.lifetime / 5.0F : dmg;
    }

    protected float bulletDamage(BulletType b) {
        return b.fragBullet == null ? b.damage + b.splashDamage + b.lightningDamage * (float)b.lightning * (float)b.lightningLength : b.damage + b.splashDamage + b.lightningDamage * (float)b.lightning * (float)b.lightningLength + this.bulletDamage(b.fragBullet) * (float)b.fragBullets;
    }

    BulletReflectPulseAbility() {
        this.healEffect = Fx.heal;
        this.pauseEffect = UnityFx.reflectPulseDynamic;
        this.resumeEffect = UnityFx.reflectResumeDynamic;
        this.activeEffect = UnityFx.reflectPulseDynamic;
        this.bullets = new Seq();
        this.bulletTimes = new Seq();
        this.bulletSpeeds = new Seq();
    }

    public BulletReflectPulseAbility(int maxReflect, float reload, float range, float reflectTime, float maxDamage) {
        this.healEffect = Fx.heal;
        this.pauseEffect = UnityFx.reflectPulseDynamic;
        this.resumeEffect = UnityFx.reflectResumeDynamic;
        this.activeEffect = UnityFx.reflectPulseDynamic;
        this.bullets = new Seq();
        this.bulletTimes = new Seq();
        this.bulletSpeeds = new Seq();
        this.maxReflect = maxReflect;
        this.reload = reload;
        this.range = range;
        this.reflectTime = reflectTime;
        this.maxDamage = maxDamage;
    }

    public void update(Unit unit) {
        this.timer += Time.delta;
        if (this.timer >= this.reload + this.reflectTime) {
            this.bullets.clear();
            this.bulletTimes.clear();
            this.bulletSpeeds.clear();
            Groups.bullet.intersect(unit.x - this.range, unit.y - this.range, this.range * 2.0F, this.range * 2.0F, (b) -> {
                if (b != null && unit.team != b.team && Mathf.within(unit.x, unit.y, b.x, b.y, this.range)) {
                    BulletType type = b.type;
                    if ((double)type.speed > 0.01 && this.bulletDamage(b) <= this.maxDamage) {
                        this.bullets.add(b);
                    }
                }

            });
            this.bullets.sort((e) -> -this.bulletDamage(e));

            for(int i = Math.min(this.maxReflect, this.bullets.size - 1); i < this.bullets.size - 1; ++i) {
                this.bullets.remove(i);
            }

            for(int i = 0; i < this.bullets.size - 1; ++i) {
                Bullet target = (Bullet)this.bullets.get(i);
                if (target != null && target.type != null) {
                    this.bulletTimes.add(target.time);
                    this.bulletSpeeds.add(target.vel.len());
                    target.vel.trns(target.vel.angle(), 0.001F);
                    this.pauseEffect.at(target, target.type.hitSize * 4.0F);
                }

                this.active = true;
            }

            Time.run(this.reflectTime, () -> {
                for(int i = 0; i < this.bullets.size - 1; ++i) {
                    Bullet target = (Bullet)this.bullets.get(i);
                    if (target != null && target.type != null) {
                        target.team = unit.team;
                        this.resumeEffect.at(target, target.type.hitSize * 4.0F);
                        target.vel.trns(Angles.angle(unit.x, unit.y, target.x, target.y), (Float)this.bulletSpeeds.get(i));
                    }
                }

                this.active = false;
            });
            this.activeEffect.at(unit, this.range);
            this.timer = 0.0F;
        }

        if (this.active) {
            for(int i = 0; i < this.bullets.size - 1; ++i) {
                Bullet target = (Bullet)this.bullets.get(i);
                target.time = (Float)this.bulletTimes.get(i);
            }
        }

    }

    public String localized() {
        return Core.bundle.get("ability.reflect-pulse-ability");
    }
}
