package unity.entities.bullet.kami;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import mindustry.ai.types.MissileAI;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.blocks.ControlBlock;
import unity.gen.*;

import static mindustry.Vars.net;
import static mindustry.Vars.world;

public class NewKamiLaserBulletType extends BulletType{
    static TextureRegion hcircle;

    public NewKamiLaserBulletType(){
        speed = 0f;
        damage = 9f;
        absorbable = false;
        hittable = false;
        collidesTiles = false;
        pierce = true;
        keepVelocity = false;
    }

    @Override
    public void load(){
        hcircle = Core.atlas.find("hcircle");
    }

    @Override
    public void draw(Bullet b){
        KamiLaser lb = (KamiLaser)b;
        TextureRegion r = KamiBulletType.region;
        float time = (b.time * 2f) + (Time.time / 2f);
        Tmp.c1.set(Color.red).shiftHue(time);
        if(lb.ellipseCollision){
            Vec2 v = Tmp.v1.set(lb.x, lb.y).sub(lb.x2, lb.y2).setLength(3f);
            Lines.stroke((lb.width + 3.5f) * 2f);
            Draw.color(Tmp.c1);
            Lines.line(r, lb.x + v.x, lb.y + v.y, lb.x2 - v.x, lb.y2 - v.y, false);
            Draw.color();
            Lines.stroke(lb.width * 2f);
            Lines.line(r, lb.x, lb.y, lb.x2, lb.y2, false);
            Draw.reset();
        }else{
            float ang = lb.angleTo(lb.x2, lb.y2);
            Draw.blend(Blending.additive);
            Draw.color(Tmp.c1);
            Lines.stroke(lb.width * 2f);
            Lines.line(lb.x, lb.y, lb.x2, lb.y2, false);
            Draw.rect(hcircle, lb.x, lb.y, lb.width * 2, lb.width * 2f, ang + 180f);
            Draw.rect(hcircle, lb.x2, lb.y2, lb.width * 2, lb.width * 2f, ang);
            Draw.blend();
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    public KamiLaser createL(Entityc owner, Team team, float x, float y, float x2, float y2, Object data){
        KamiLaser b = (KamiLaser)create(owner, team, x, y, 0f, -1f, 1f, 1f, data);
        b.x2 = x2;
        b.y2 = y2;
        return b;
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

        KamiLaser bullet = KamiLaser.create();
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
        return bullet;
    }
}
