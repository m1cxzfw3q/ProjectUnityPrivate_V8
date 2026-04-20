package unity.ai;

import arc.math.geom.Position;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import unity.content.UnityFx;

/** @deprecated */
@Deprecated
public class HealerAI extends FlyingAI {
    protected float score;

    public boolean invalid(Teamc target) {
        boolean var10000;
        label33: {
            if (target instanceof Healthc) {
                Healthc t = (Healthc)target;
                if (t.damaged() || t.isValid()) {
                    var10000 = false;
                    break label33;
                }
            }

            var10000 = true;
        }

        boolean damaged = var10000;
        return target == null || target.team() != this.unit.team || damaged;
    }

    public void updateMovement() {
        Teamc var2 = this.target;
        if (var2 instanceof Unit) {
            Unit temp = (Unit)var2;
            vec.trns(this.unit.angleTo(temp) + 180.0F, this.unit.type.range + temp.hitSize);
            vec.add(this.target).sub(this.unit).scl(0.01F).limit(1.0F).scl(this.unit.speed());
            this.unit.moveAt(vec);
            this.unit.lookAt(this.target);
        }

    }

    public void updateWeapons() {
        if (this.target != null && (this.unit.ammo > 1.0E-4F || !Vars.state.rules.unitAmmo)) {
            Teamc var2 = this.target;
            if (var2 instanceof Unit) {
                Unit temp = (Unit)var2;
                if (this.timer.get(3, 5.0F) && this.unit.within(this.target, this.unit.type.range + temp.hitSize)) {
                    if (Vars.state.rules.unitAmmo) {
                        --this.unit.ammo;
                    }

                    UnityFx.healLaser.at(this.unit.x, this.unit.y, 0.0F, new Position[]{this.unit, temp});
                    temp.heal(this.unit.type.buildSpeed);
                }
            }
        }

    }

    public void updateTargeting() {
        if (this.retarget()) {
            this.score = 0.0F;
            this.target = null;
            Groups.unit.each((x) -> x.team == this.unit.team, (e) -> {
                float scoreB = (1.0F - e.healthf()) * 200.0F + (1000000.0F - this.unit.dst(e)) / 500.0F;
                if (scoreB > this.score && e.damaged() && e != this.unit && e.isValid()) {
                    this.score = scoreB;
                    this.target = e;
                }

            });
        }

        this.updateWeapons();
    }
}
