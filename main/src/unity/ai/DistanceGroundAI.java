package unity.ai;

import arc.util.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DistanceGroundAI extends GroundAI{
    protected boolean lockTarget;
    protected float lockTimer = 60f;

    @Override
    public void updateMovement(){
        Building core = unit.closestEnemyCore();

        float range = unit.range();
        Team team = unit.team;
        UnitType type = unit.type;

        if(core != null && unit.within(core, range / 1.1f + core.block.size * tilesize / 2f)){
            target = core;
        }

        if(target != null && target.team() != team && unit.within(target, range / 1.7f)){
            lockTarget = true;
            lockTimer = 0f;
        }

        if(lockTimer >= 60f) lockTarget = false;
        else lockTimer += Time.delta;


        if((core == null || !unit.within(core, range * 0.5f))){
            boolean move = true;
            if(state.rules.waves && team == state.rules.defaultTeam){
                Tile spawner = getClosestSpawner();
                if(spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)) move = false;
            }
            if(move) pathfind(Pathfinder.fieldCore);
        }

        if(!Units.invalidateTarget(target, unit, range) && type.faceTarget){
            if(type.hasWeapons()) unit.lookAt(Predict.intercept(unit, target, type.weapons.first().bullet.speed));
        }else if(unit.moving()) unit.lookAt(unit.vel.angle());
    }
}
