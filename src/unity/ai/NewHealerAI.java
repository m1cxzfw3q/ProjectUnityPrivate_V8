package unity.ai;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Sized;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.weapons.RepairBeamWeapon;

public class NewHealerAI extends FlyingAI {
    static final int depth = 32;
    float switchTime = 0.0F;
    boolean findTile = false;

    public void init() {
        super.init();
        this.findTile = this.unit.type.canHeal;

        for(WeaponMount mount : this.unit.mounts) {
            this.findTile |= mount.weapon instanceof RepairBeamWeapon && ((RepairBeamWeapon)mount.weapon).targetBuildings;
        }

    }

    public void updateUnit() {
        super.updateUnit();
        this.switchTime -= Time.delta;
    }

    public void updateMovement() {
        if (this.target != null) {
            float range = this.unit.type.range * 0.8F;
            if (this.target instanceof Sized) {
                range += ((Sized)this.target).hitSize() / 4.0F;
            }

            if (!this.unit.type.circleTarget) {
                this.moveTo(this.target, range, 40.0F);
                this.unit.lookAt(this.target);
            } else {
                this.circle(range);
            }
        }

    }

    void circle(float range) {
        vec.set(this.target).sub(this.unit);
        if (vec.len() < range) {
            float side = Mathf.randomSeed((long)this.unit.id, 0, 1) == 0 ? -1.0F : 1.0F;
            vec.rotate((range - vec.len()) / range * 180.0F * side);
        }

        vec.setLength(this.unit.speed());
        this.unit.moveAt(vec);
    }

    public boolean invalid(Teamc target) {
        boolean in = target == null || target.team() != this.unit.team || target instanceof Healthc && (!((Healthc)target).isValid() || !((Healthc)target).damaged());
        if (in) {
            this.switchTime = 0.0F;
        }

        return in;
    }

    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        float sd = this.unit.speed() / 3.0F;
        Building build = null;
        float buildScore = -Float.MAX_VALUE;
        if (ground && this.findTile) {
            Seq<Building> buildings = Vars.indexer.getDamaged(this.unit.team);
            int len = Math.min(buildings.size, 32);

            for(int i = 0; i < len; ++i) {
                float s = this.calculateScore((Healthc)buildings.get(i), sd);
                if (s > buildScore) {
                    buildScore = s;
                    build = (Building)buildings.get(i);
                }
            }
        }

        Seq<Unit> units = this.unit.team.data().units;
        Unit un = null;
        float score = -Float.MAX_VALUE;

        for(Unit u : units) {
            if (!u.dead && u != this.unit && u.damaged() && u.checkTarget(air, ground)) {
                float sc = this.calculateScore(u, sd);
                if (sc > score) {
                    score = sc;
                    un = u;
                }
            }
        }

        if (un != null || build != null) {
            this.switchTime = 160.0F;
        }

        return (Teamc)(!(score > buildScore) && build != null ? build : un);
    }

    public boolean retarget() {
        return this.switchTime <= 0.0F && super.retarget();
    }

    float calculateScore(Healthc target, float s) {
        float h = Mathf.sqrt(Math.max(target.maxHealth() - target.health(), 0.0F)) * 500.0F;
        return -this.unit.dst2(target) / (s * s) / 2000.0F + h;
    }
}
