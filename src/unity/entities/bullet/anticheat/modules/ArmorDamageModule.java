package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public class ArmorDamageModule implements AntiCheatBulletModule {
    private final float armorDamage;
    private final float shieldDamage;
    private final float efficiencyDamage;
    private final float ratioDamage;
    private float minimumArmorShield = 20.0F;
    private float minimumEfficiency = 2.0F;

    public ArmorDamageModule(float armorDamage, float shieldDamage, float efficiencyDamage) {
        this.armorDamage = armorDamage;
        this.shieldDamage = shieldDamage;
        this.efficiencyDamage = efficiencyDamage;
        this.ratioDamage = 0.0F;
    }

    public ArmorDamageModule(float ratioDamage, float armorDamage, float shieldDamage, float efficiencyDamage) {
        this.armorDamage = armorDamage;
        this.shieldDamage = shieldDamage;
        this.ratioDamage = ratioDamage;
        this.efficiencyDamage = efficiencyDamage;
    }

    public ArmorDamageModule set(float minAS, float minE) {
        this.minimumArmorShield = minAS;
        this.minimumEfficiency = minE;
        return this;
    }

    public void hitUnit(Unit unit, Bullet bullet) {
        if (unit.armor > this.minimumArmorShield) {
            unit.armor = Math.max(unit.armor - Math.max(this.armorDamage, unit.armor * this.ratioDamage), 0.0F);
            if (unit.armor < this.minimumArmorShield) {
                unit.armor = this.minimumArmorShield;
            }
        }

        if (unit.shield > this.minimumArmorShield) {
            unit.shield = Math.max(unit.shield - Math.max(this.shieldDamage, unit.shield * this.ratioDamage), 0.0F);
            if (unit.shield < this.minimumArmorShield) {
                unit.shield = this.minimumArmorShield;
            }
        }

    }

    public void handleAbility(Ability ability, Unit unit, Bullet bullet) {
        if (ability instanceof ShieldRegenFieldAbility) {
            ShieldRegenFieldAbility s = (ShieldRegenFieldAbility)ability;
            if (s.max > this.minimumEfficiency) {
                s.max = Math.max(s.max - Math.max(this.efficiencyDamage, s.max * this.ratioDamage), 0.0F);
                if (s.max < this.minimumEfficiency) {
                    s.max = this.minimumEfficiency;
                }
            }
        }

    }
}
