package unity.entities.bullet.anticheat;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Velc;
import unity.Unity;
import unity.entities.bullet.anticheat.modules.AntiCheatBulletModule;
import unity.gen.EndBullet;
import unity.mod.AntiCheat;

public abstract class AntiCheatBulletTypeBase extends BulletType {
    protected float ratioDamage = 0.0F;
    protected float ratioStart = 25000.0F;
    protected float bleedDuration = -1.0F;
    protected float overDamage = 1000000.0F;
    protected float overDamageScl = 2000.0F;
    protected float overDamagePower = 2.0F;
    protected boolean pierceShields = false;
    protected AntiCheatBulletModule[] modules;
    private float[] moduleDataTmp;

    public AntiCheatBulletTypeBase(float speed, float damage) {
        super(speed, damage);
    }

    public AntiCheatBulletTypeBase() {
    }

    public void init() {
        super.init();
        if (this.modules != null) {
            this.moduleDataTmp = new float[this.modules.length];
        }

    }

    public void hitUnitAntiCheat(Bullet b, Unit unit) {
        this.hitUnitAntiCheat(b, unit, 0.0F);
    }

    public void hitUnitAntiCheat(Bullet b, Unit unit, float extraDamage) {
        float health = unit.health * unit.healthMultiplier;
        if (!(health >= Float.MAX_VALUE) && !Float.isNaN(health) && !(health >= Float.POSITIVE_INFINITY)) {
            float lh = unit.health;
            float ls = unit.shield;
            float score = health + unit.type.dpsEstimate;
            float pow = score > this.overDamage ? Mathf.pow((score - this.overDamage) / this.overDamageScl, this.overDamagePower) : 0.0F;
            float ratio = health > this.ratioStart ? this.ratioDamage * Math.max(unit.maxHealth, unit.health) : 0.0F;
            float damage = Math.max(ratio, (b.damage + extraDamage) * b.damageMultiplier() + pow);
            if (this.bleedDuration > 0.0F) {
                Unity.antiCheat.applyStatus(unit, this.bleedDuration);
            }

            if (this.modules != null) {
                int i = 0;

                for(AntiCheatBulletModule mod : this.modules) {
                    this.moduleDataTmp[i] = mod.getUnitData(unit);
                    mod.hitUnit(unit, b);
                    ++i;
                }

                for(Ability ability : unit.abilities) {
                    for(AntiCheatBulletModule mod : this.modules) {
                        mod.handleAbility(ability, unit, b);
                    }
                }
            }

            if (this.pierceShields) {
                unit.damagePierce(damage);
            } else {
                unit.damage(damage);
            }

            float hd = unit.health - lh;
            float sd = unit.shield - ls;
            Unity.antiCheat.notifyDamage(unit.id, hd);
            Unity.antiCheat.samplerAdd(unit, hd + sd < 1.0E-5F && damage < Float.MAX_VALUE);
            Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F);
            if (this.impact) {
                Tmp.v3.setAngle(b.rotation() + (this.knockback < 0.0F ? 180.0F : 0.0F));
            }

            unit.impulse(Tmp.v3);
            unit.apply(this.status, this.statusDuration);
            if (this.modules != null) {
                for(int i = 0; i < this.modules.length; ++i) {
                    this.modules[i].handleUnitPost(unit, b, this.moduleDataTmp[i]);
                }
            }

        } else {
            AntiCheat.annihilateEntity(unit, true);
        }
    }

    public void hitBuildingAntiCheat(Bullet b, Building building) {
        this.hitBuildingAntiCheat(b, building, 0.0F);
    }

    public void hitBuildingAntiCheat(Bullet b, Building building, float extraDamage) {
        if (!(building.health >= Float.MAX_VALUE) && !Float.isNaN(building.health) && !(building.health >= Float.POSITIVE_INFINITY)) {
            boolean col = !this.collidesTiles || !this.collides;
            float pow = building.health > this.overDamage ? Mathf.pow((building.health - this.overDamage) / this.overDamageScl, this.overDamagePower) : 0.0F;
            if (col || pow > 0.0F || this.ratioDamage > 0.0F) {
                float ratio = building.health > this.ratioStart ? this.ratioDamage * Math.max(building.maxHealth, building.health) : 0.0F;
                float damage = Math.max(ratio, (col ? (b.damage + extraDamage) * b.damageMultiplier() * this.buildingDamageMultiplier : 0.0F) + pow);
                float lh = building.health;
                building.damage(damage);
                if (building.health >= lh && damage >= Float.MAX_VALUE) {
                    AntiCheat.annihilateEntity(building, true);
                }
            }

        } else {
            AntiCheat.annihilateEntity(building, true);
        }
    }

    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        if (entity instanceof Unit) {
            this.hitUnitAntiCheat(b, (Unit)entity);
        } else {
            super.hitEntity(b, entity, health);
        }

    }

    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct) {
        this.hitBuildingAntiCheat(b, build);
        super.hitTile(b, build, initialHealth, direct);
    }

    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        EndBullet bullet = EndBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0.0F;
        if (owner instanceof Teamc) {
            bullet.setTrueOwner((Teamc)owner);
        }

        bullet.initVel(angle, this.speed * velocityScl);
        if (this.backMove) {
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        } else {
            bullet.set(x, y);
        }

        bullet.lifetime = this.lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = this.drag;
        bullet.hitSize = this.hitSize;
        bullet.damage = (damage < 0.0F ? this.damage : damage) * bullet.damageMultiplier();
        if (bullet.trail != null) {
            bullet.trail.clear();
        }

        bullet.add();
        if (this.keepVelocity && owner instanceof Velc) {
            bullet.vel.add(((Velc)owner).vel());
        }

        return bullet;
    }
}
