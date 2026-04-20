package unity.ai;

import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.entities.units.UnitCommand;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

public class DistanceGroundAI extends GroundAI {
    protected boolean lockTarget;
    protected float lockTimer = 60.0F;

    public void updateMovement() {
        Building core = this.unit.closestEnemyCore();
        float range = this.unit.range();
        Team team = this.unit.team;
        UnitType type = this.unit.type;
        if (core != null && this.unit.within(core, range / 1.1F + (float)(core.block.size * 8) / 2.0F)) {
            this.target = core;
        }

        if (this.target != null && this.target.team() != team && this.unit.within(this.target, range / 1.7F)) {
            this.lockTarget = true;
            this.lockTimer = 0.0F;
        }

        if (this.lockTimer >= 60.0F) {
            this.lockTarget = false;
        } else {
            this.lockTimer += Time.delta;
        }

        if (this.lockTarget) {
            if (this.target != null && this.target.team() != team && this.command() != UnitCommand.rally && this.unit.within(this.target, range / 1.72F)) {
                this.unit.moveAt(vec.trns(this.unit.angleTo(this.target) + 180.0F, this.unit.speed()));
            }
        } else if ((core == null || !this.unit.within(core, range * 0.5F)) && this.command() == UnitCommand.attack) {
            boolean move = true;
            if (Vars.state.rules.waves && team == Vars.state.rules.defaultTeam) {
                Tile spawner = this.getClosestSpawner();
                if (spawner != null && this.unit.within(spawner, Vars.state.rules.dropZoneRadius + 120.0F)) {
                    move = false;
                }
            }

            if (move) {
                this.pathfind(0);
            }
        }

        if (this.command() == UnitCommand.rally) {
            Teamc target = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);
            if (target != null && this.unit.within(target, 70.0F)) {
                this.pathfind(1);
            }
        }

        if (!Units.invalidateTarget(this.target, this.unit, range) && type.rotateShooting) {
            if (type.hasWeapons()) {
                this.unit.lookAt(Predict.intercept(this.unit, this.target, ((Weapon)type.weapons.first()).bullet.speed));
            }
        } else if (this.unit.moving()) {
            this.unit.lookAt(this.unit.vel.angle());
        }

    }
}
