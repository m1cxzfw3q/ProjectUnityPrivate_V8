package unity.ai;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.units.UnitCommand;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.BlockFlag;
import unity.gen.Wormc;
import unity.type.UnityUnitType;

public class WormAI extends FlyingAI {
    public Vec2 pos = new Vec2();
    public float score = 0.0F;
    public float time = 0.0F;
    protected float rotateTime = 0.0F;

    public void updateMovement() {
        if (this.target == null && this.time > 0.0F) {
            this.moveTo(this.pos, 0.0F);
        }

        if (this.target != null && this.unit.hasWeapons() && this.command() == UnitCommand.attack) {
            if (!this.unit.type.circleTarget) {
                this.moveTo(this.target, this.unit.range() * 0.8F);
                this.unit.lookAt(this.target);
            } else {
                this.attack(120.0F);
            }
        }

        if (this.target == null && this.time <= 0.0F && this.command() == UnitCommand.attack && Vars.state.rules.waves && this.unit.team == Vars.state.rules.defaultTeam) {
            this.moveTo(this.getClosestSpawner(), Vars.state.rules.dropZoneRadius + 120.0F);
        }

        if (this.command() == UnitCommand.rally) {
            this.moveTo(this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false), 60.0F);
        }

        this.rotateTime = Math.max(0.0F, this.rotateTime - Time.delta);
        if (this.time <= 0.0F) {
            this.score = 0.0F;
        }

        this.time = Math.max(0.0F, this.time - Time.delta);
    }

    public void updateWeapons() {
        Unit var3 = this.unit;
        if (var3 instanceof Wormc) {
            Wormc w = (Wormc)var3;
            UnitType var9 = this.unit.type;
            if (var9 instanceof UnityUnitType) {
                UnityUnitType uType = (UnityUnitType)var9;
                if (w.head() != null && w.head().isShooting && w.head().controller() instanceof Player && this.unit.within(w.head(), uType.barrageRange + this.unit.hitSize / 2.0F)) {
                    Unit head = w.head();

                    for(WeaponMount mount : this.unit.mounts) {
                        Weapon weapon = mount.weapon;
                        if (weapon.controllable) {
                            mount.aimX = head.aimX;
                            mount.aimY = head.aimY;
                            mount.shoot = mount.rotate = true;
                        }
                    }

                    return;
                }
            }
        }

        super.updateWeapons();
    }

    protected void attack(float circleLength) {
        vec.trns(this.unit.rotation, this.unit.speed());
        float diff = Angles.angleDist(this.unit.rotation, this.unit.angleTo(this.target));
        if (diff > 100.0F && !this.unit.within(this.target, circleLength) || this.rotateTime > 0.0F) {
            vec.setAngle(Mathf.slerpDelta(vec.angle(), this.unit.angleTo(this.target), 0.2F));
            if (this.rotateTime <= 0.0F) {
                this.rotateTime = 40.0F;
            }
        }

        this.unit.moveAt(vec);
    }

    public void setTarget(float x, float y, float score) {
        if (!(score < this.score)) {
            this.pos.set(x, y);
            this.score = score;
            this.time = 180.0F;
        }
    }
}
