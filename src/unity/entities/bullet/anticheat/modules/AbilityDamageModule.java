package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.RepairFieldAbility;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public class AbilityDamageModule implements AntiCheatBulletModule {
    private final float minimumEfficiency;
    private final float maximumReload;
    private final float efficiencyDamage;
    private final float ratioDamage;
    private final float reloadDamage;

    public AbilityDamageModule(float minimumEfficiency, float maximumReload, float efficiencyDamage, float ratioDamage, float reloadDamage) {
        this.minimumEfficiency = minimumEfficiency;
        this.maximumReload = maximumReload;
        this.efficiencyDamage = efficiencyDamage;
        this.ratioDamage = ratioDamage;
        this.reloadDamage = reloadDamage;
    }

    public void handleAbility(Ability ability, Unit unit, Bullet bullet) {
        if (ability instanceof StatusFieldAbility) {
            StatusFieldAbility s = (StatusFieldAbility)ability;
            if (s.duration > this.minimumEfficiency) {
                s.duration = Math.max(this.minimumEfficiency, s.duration - Math.max(this.efficiencyDamage, s.duration * this.ratioDamage));
            }

            if (s.reload < this.maximumReload) {
                s.reload = Math.min(s.reload + Math.max(this.reloadDamage, this.ratioDamage * s.reload), this.maximumReload);
            }
        } else if (ability instanceof RepairFieldAbility) {
            RepairFieldAbility r = (RepairFieldAbility)ability;
            if (r.amount > this.minimumEfficiency) {
                r.amount = Math.max(this.minimumEfficiency, r.amount - Math.max(this.efficiencyDamage, r.amount * this.ratioDamage));
            }

            if (r.reload < this.maximumReload) {
                r.reload = Math.min(r.reload + Math.max(this.reloadDamage, this.ratioDamage * r.reload), this.maximumReload);
            }
        }

    }
}
