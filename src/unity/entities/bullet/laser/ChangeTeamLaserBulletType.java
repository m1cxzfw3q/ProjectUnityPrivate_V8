package unity.entities.bullet.laser;

import mindustry.content.StatusEffects;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Statusc;
import mindustry.gen.Teamc;
import mindustry.type.StatusEffect;

public class ChangeTeamLaserBulletType extends ContinuousLaserBulletType {
    public float minimumHealthPercent = 0.125F;
    public float minimumHealthOverride = 90.0F;
    public float ownerDamageRatio = 0.5F;
    public boolean convertUnits = true;
    public boolean convertBlocks = true;
    public StatusEffect conversionStatusEffect;

    public ChangeTeamLaserBulletType(float damage) {
        super(damage);
        this.conversionStatusEffect = StatusEffects.none;
    }

    public void hitEntity(Bullet b, Hitboxc other, float initialHealth) {
        super.hitEntity(b, other, initialHealth);
        if (other instanceof Teamc) {
            Teamc t = (Teamc)other;
            if (other instanceof Statusc) {
                Statusc s = (Statusc)other;
                if (this.convertUnits && (s.healthf() <= this.minimumHealthPercent || s.health() < this.minimumHealthOverride)) {
                    t.team(b.team);
                    this.damageOwner(b, initialHealth * this.ownerDamageRatio);
                    s.apply(this.conversionStatusEffect);
                }

                return;
            }
        }

    }

    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct) {
        super.hitTile(b, build, initialHealth, direct);
        if (this.convertBlocks && (build.healthf() <= this.minimumHealthPercent || build.health < this.minimumHealthOverride)) {
            build.team(b.team);
            this.damageOwner(b, initialHealth * this.ownerDamageRatio);
        }

    }

    void damageOwner(Bullet b, float damage) {
        if (damage != 0.0F) {
            Entityc var4 = b.owner;
            if (var4 instanceof Healthc) {
                Healthc h = (Healthc)var4;
                if (damage < 0.0F) {
                    h.heal(Math.abs(damage));
                    return;
                }

                if (!(h.health() - damage > 1.0F) && !(h.health() < h.maxHealth() / 2.0F)) {
                    h.health(1.0F);
                } else {
                    h.damage(damage);
                }
            }

        }
    }
}
