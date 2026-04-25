package unity.entities.bullet.anticheat.modules;

import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public class ForceFieldDamageModule implements AntiCheatBulletModule {
    private final float maxRadius;
    private final float maxShield;
    private final float maxRegen;
    private final float ratioDamage;
    private final float damage;
    private final float cooldown;

    public ForceFieldDamageModule(float damage, float maxRadius, float maxShield, float maxRegen, float ratioDamage, float cooldown) {
        this.maxRadius = maxRadius;
        this.maxShield = maxShield;
        this.maxRegen = maxRegen;
        this.ratioDamage = ratioDamage;
        this.damage = damage;
        this.cooldown = cooldown;
    }

    public ForceFieldDamageModule(float damage, float maxRadius, float maxShield, float maxRegen, float ratioDamage) {
        this(damage, maxRadius, maxShield, maxRegen, ratioDamage, 0.0F);
    }

    public float getUnitData(Unit unit) {
        return unit.shield;
    }

    public void handleAbility(Ability ability, Unit unit, Bullet bullet) {
        if (ability instanceof ForceFieldAbility) {
            ForceFieldAbility f = (ForceFieldAbility)ability;
            if (f.regen > this.maxRegen) {
                f.regen = Math.max(this.maxRegen, f.regen - Math.max(this.damage / 5.0F, f.regen * this.ratioDamage));
            }

            if (f.max > this.maxShield) {
                f.max = Math.max(this.maxShield, f.max - Math.max(this.damage, f.max * this.ratioDamage));
            }

            if (f.radius > this.maxRadius + unit.hitSize / 2.0F) {
                f.radius = Math.max(this.maxRadius + unit.hitSize / 2.0F, f.radius - Math.max(this.damage, f.radius * this.ratioDamage));
            }
        }

    }

    public void handleUnitPost(Unit unit, Bullet bullet, float data) {
        if (data > 0.0F && unit.shield <= 0.0F) {
            for(Ability a : unit.abilities) {
                if (a instanceof ForceFieldAbility) {
                    ForceFieldAbility f = (ForceFieldAbility)a;
                    unit.shield -= Math.max(f.cooldown * f.regen, this.cooldown * f.regen);
                    Fx.shieldBreak.at(unit.x, unit.y, f.radius, unit.team.color);
                    break;
                }
            }
        }

    }
}
