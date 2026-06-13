package unity.entities.bullet.anticheat;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.MissileAI;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.ControlBlock;
import unity.content.effects.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;

import static mindustry.Vars.world;

public class TimeStopBulletType extends AntiCheatBulletTypeBase{
    public float duration = 45f;

    public TimeStopBulletType(float speed, float damage){
        super(speed, damage);
        despawnEffect = hitEffect = HitFx.endHitRedSmall;
        trailColor = UnityPal.scarColor;
        trailLength = 10;
        trailWidth = 4f;
        pierce = true;
        pierceCap = 3;
        lifetime = 110f;

        modules = new AntiCheatBulletModule[]{
            new ArmorDamageModule(1f / 100, 2f, 8f, 3f)
        };
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);
        Draw.color(trailColor);
        Drawf.tri(b.x, b.y, trailWidth * 2 * 1.22f, 14f, b.rotation());
        Drawf.tri(b.x, b.y, trailWidth * 2 * 1.22f, 7f, b.rotation() + 180f);
        Draw.color();
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
            if(!Vars.net.client()){
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

        Bullet bullet = TimeStopBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.shooter = (shooter == null ? owner : shooter);
        bullet.team = team;
        bullet.time = 0f;
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

        if(TimeStop.inTimeStop() && owner != null){
            float duration = Math.min(this.duration, TimeStop.getTime(owner));
            if(duration > 0f){
                TimeStop.addEntity(bullet, duration);
            }
        }
        return bullet;
    }
}
