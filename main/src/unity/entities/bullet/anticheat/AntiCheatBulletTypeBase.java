package unity.entities.bullet.anticheat;

import arc.math.*;
import arc.util.*;
import mindustry.ai.types.MissileAI;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.blocks.ControlBlock;
import unity.*;
import unity.entities.CircleCollisionBullet;
import unity.entities.bullet.anticheat.modules.*;
import unity.gen.*;
import unity.mod.*;

import static mindustry.Vars.net;
import static mindustry.Vars.world;

public abstract class AntiCheatBulletTypeBase extends BulletType{
    /** 0-1 */
    protected float ratioDamage = 0f;
    /** Ratio Damage starts if health is higher than this value */
    protected float ratioStart = 25000f;
    /** Prevents regeneration */
    protected float bleedDuration = -1f;
    /** If the targets health exceeds this value, damage starts increasing */
    protected float overDamage = 1000000f;
    protected float overDamageScl = 2000f;
    protected float overDamagePower = 2f;

    protected boolean pierceShields = false;

    protected AntiCheatBulletModule[] modules;
    private float[] moduleDataTmp;

    public boolean backMove = true;

    public AntiCheatBulletTypeBase(float speed, float damage){
        super(speed, damage);
    }

    public AntiCheatBulletTypeBase(){

    }

    @Override
    public void init(){
        super.init();
        if(modules != null) moduleDataTmp = new float[modules.length];
    }

    public void hitUnitAntiCheat(Bullet b, Unit unit){
        hitUnitAntiCheat(b, unit, 0f);
    }

    public void hitUnitAntiCheat(Bullet b, Unit unit, float extraDamage){
        float health = unit.health * unit.healthMultiplier;
        if(health >= Float.MAX_VALUE || Float.isNaN(health) || health >= Float.POSITIVE_INFINITY){
            AntiCheat.annihilateEntity(unit, true);
            return;
        }
        float lh = unit.health, ls = unit.shield;
        float score = health + unit.type.dpsEstimate;
        float pow = score > overDamage ? Mathf.pow((score - overDamage) / overDamageScl, overDamagePower) : 0f;
        float ratio = health > ratioStart ? ratioDamage * Math.max(unit.maxHealth, unit.health) : 0f;
        float damage = Math.max(ratio, ((b.damage + extraDamage) * b.damageMultiplier()) + pow);
        if(bleedDuration > 0){
            Unity.antiCheat.applyStatus(unit, bleedDuration);
        }
        if(modules != null){
            int i = 0;
            for(AntiCheatBulletModule mod : modules){
                moduleDataTmp[i] = mod.getUnitData(unit);
                mod.hitUnit(unit, b);
                i++;
            }
            for(Ability ability : unit.abilities){
                for(AntiCheatBulletModule mod : modules){
                    mod.handleAbility(ability, unit, b);
                }
            }
        }

        if(pierceShields){
            unit.damagePierce(damage);
        }else{
            unit.damage(damage);
        }
        float hd = unit.health - lh, sd = unit.shield - ls;
        Unity.antiCheat.notifyDamage(unit.id, hd);
        Unity.antiCheat.samplerAdd(unit, (hd + sd) < 0.00001f && damage < Float.MAX_VALUE);

        Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
        if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
        unit.impulse(Tmp.v3);
        unit.apply(status, statusDuration);

        if(modules != null){
            for(int i = 0; i < modules.length; i++){
                modules[i].handleUnitPost(unit, b, moduleDataTmp[i]);
            }
        }
    }

    public void hitBuildingAntiCheat(Bullet b, Building building){
        hitBuildingAntiCheat(b, building, 0f);
    }

    public void hitBuildingAntiCheat(Bullet b, Building building, float extraDamage){
        if(building.health >= Float.MAX_VALUE || Float.isNaN(building.health) || building.health >= Float.POSITIVE_INFINITY){
            AntiCheat.annihilateEntity(building, true);
            return;
        }
        boolean col = !(collidesTiles && collides);
        float pow = building.health > overDamage ? Mathf.pow((building.health - overDamage) / overDamageScl, overDamagePower) : 0f;
        if(col || pow > 0f || ratioDamage > 0f){
            float ratio = building.health > ratioStart ? ratioDamage * Math.max(building.maxHealth, building.health) : 0f;
            float damage = Math.max(ratio, (col ? (b.damage + extraDamage) * b.damageMultiplier() * buildingDamageMultiplier : 0f) + pow);
            float lh = building.health;
            building.damage(damage);
            if(building.health >= lh && damage >= Float.MAX_VALUE){
                AntiCheat.annihilateEntity(building, true);
            }
        }
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health){
        if(entity instanceof Unit){
            hitUnitAntiCheat(b, (Unit)entity);
        }else{
            super.hitEntity(b, entity, health);
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
        hitBuildingAntiCheat(b, build);
        super.hitTile(b, build, x, y, initialHealth, direct);
    }

    @Override
    public @Nullable Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ){
        angle += angleOffset + Mathf.range(randomAngleOffset);

        if(!Mathf.chance(createChance)) return null;
        if(ignoreSpawnAngle) angle = 0;
        if(spawnUnit != null){
            //don't spawn units clientside!
            if(!net.client()){
                Unit spawned = spawnUnit.create(team);
                spawned.set(x, y);
                spawned.rotation = angle;
                //immediately spawn at top speed, since it was launched
                if(spawnUnit.missileAccelTime <= 0f){
                    spawned.vel.trns(angle, spawnUnit.speed);
                }
                //assign unit owner
                if(spawned.controller() instanceof MissileAI ai){
                    if(shooter instanceof Unit unit){
                        ai.shooter = unit;
                    }

                    if(shooter instanceof ControlBlock control){
                        ai.shooter = control.unit();
                    }

                }
                spawned.add();
                Units.notifyUnitSpawn(spawned);
            }
            //Since bullet init is never called, handle killing shooter here
            if(killShooter && owner instanceof Healthc h && !h.dead()) h.kill();

            //no bullet returned
            return null;
        }

        EndBullet bullet = EndBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.shooter = (shooter == null ? owner : shooter);
        bullet.team = team;
        bullet.time = 0f;
        if(owner instanceof Teamc) bullet.setTrueOwner((Teamc)owner);
        bullet.originX = x;
        bullet.originY = y;
        if(!(aimX == -1f && aimY == -1f)){
            bullet.aimTile = target instanceof Building b ? b.tile : world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, speed * velocityScl * (velocityScaleRandMin != 1f || velocityScaleRandMax != 1f ? Mathf.random(velocityScaleRandMin, velocityScaleRandMax) : 1f));
        bullet.set(x, y);
        bullet.lastX = x;
        bullet.lastY = y;
        bullet.lifetime = lifetime * lifetimeScl * (lifeScaleRandMin != 1f || lifeScaleRandMax != 1f ? Mathf.random(lifeScaleRandMin, lifeScaleRandMax) : 1f);
        bullet.data = data;
        bullet.hitSize = hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        bullet.buildingDamageMultiplier = buildingDamageMultiplier;
        //reset trail
        if(bullet.trail != null){
            bullet.trail.clear();
        }
        bullet.add();

        if(keepVelocity && owner instanceof Velc v){
            float len = bullet.vel.len();
            bullet.vel.add(v.vel());

            if(scaleKeepVelocity){
                float newLen = bullet.vel.len();
                //only reduce lifetime, never add
                if(newLen > 0f) bullet.lifetime *= Math.min(1f, len / newLen);
            }
        }
        return bullet;
    }
}
