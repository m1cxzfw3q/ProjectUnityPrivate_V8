package unity.entities.bullet.energy;

import arc.struct.IntSeq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

public class TrailingEmpBulletType extends EmpBulletType {
    public float empRadius = 50.0F;
    public float empTimer = 15.0F;
    public Effect zapEffect;

    public TrailingEmpBulletType() {
        this.zapEffect = Fx.none;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new IntSeq();
    }

    public void update(Bullet b) {
        super.update(b);
        IntSeq zapped = (IntSeq)b.data;
        if (b.timer(1, this.empTimer)) {
            Vars.indexer.allBuildings(b.x, b.y, this.empRadius, (other) -> {
                if (!zapped.contains(other.id)) {
                    if (other.team == b.team) {
                        if (other.block.hasPower && other.block.canOverdrive && other.timeScale < this.timeIncrease) {
                            other.timeScale = Math.max(other.timeScale, this.timeIncrease);
                            other.timeScaleDuration = Math.max(other.timeScaleDuration, this.timeDuration);
                            this.chainEffect.at(b.x, b.y, 0.0F, this.hitColor, other);
                            this.applyEffect.at(other, (float)other.block.size * 7.0F);
                            this.zapEffect.at(b.x, b.y, b.angleTo(other));
                            zapped.add(other.id);
                        }

                        if (other.block.hasPower && other.damaged()) {
                            other.heal(this.healPercent / 100.0F * other.maxHealth());
                            Fx.healBlockFull.at(other.x, other.y, (float)other.block.size, this.hitColor);
                            this.applyEffect.at(other, (float)other.block.size * 7.0F);
                        }
                    } else if (other.power != null) {
                        Building absorber = Damage.findAbsorber(b.team, b.x, b.y, other.x, other.y);
                        if (absorber != null) {
                            other = absorber;
                        }

                        if (other.power != null && other.power.graph.getLastPowerProduced() > 0.0F) {
                            other.timeScale = Math.min(other.timeScale, this.powerSclDecrease);
                            other.timeScaleDuration = this.timeDuration;
                            other.damage(this.damage * this.powerDamageScl);
                            this.hitPowerEffect.at(other.x, other.y, b.angleTo(other), this.hitColor);
                            this.chainEffect.at(b.x, b.y, 0.0F, this.hitColor, other);
                            this.zapEffect.at(b.x, b.y, b.angleTo(other));
                            zapped.add(other.id);
                        }
                    }
                }

            });
            if (this.hitUnits) {
                Units.nearbyEnemies(b.team, b.x, b.y, this.empRadius, (other) -> {
                    if (other.team != b.team && !zapped.contains(other.id)) {
                        Building absorber = Damage.findAbsorber(b.team, b.x, b.y, other.x, other.y);
                        if (absorber != null) {
                            return;
                        }

                        this.hitPowerEffect.at(other.x, other.y, b.angleTo(other), this.hitColor);
                        this.chainEffect.at(b.x, b.y, 0.0F, this.hitColor, other);
                        other.damage(this.damage * this.unitDamageScl);
                        other.apply(this.status, this.statusDuration);
                        this.zapEffect.at(b.x, b.y, b.angleTo(other));
                        zapped.add(other.id);
                    }

                });
            }
        }

    }
}
