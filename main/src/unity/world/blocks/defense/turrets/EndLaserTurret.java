package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.Mover;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.DrawTurret;
import unity.gen.*;

public class EndLaserTurret extends PowerTurret{
    public float minDamage = 170f, minDamageTaken = 600f;
    public float resistScl = 0.25f;
    public TextureRegion[] lightRegions;
    protected static float turretRotation = 0f;

    public EndLaserTurret(String name){
        super(name);
        drawer = new DrawTurret("unity-block-") {
            @Override
            public void drawTurret(Turret block, TurretBuild build) {
                Draw.rect(Regions.tenmeikiriBaseOutlineRegion, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.rotation - 90f);
                Draw.blend(Blending.additive);
                for(int i = 0; i < lightRegions.length; i++){
                    float offset = Time.time + ((360f / lightRegions.length) * i);
                    float alpha = 1f;
                    if(build instanceof EndLaserTurretBuild) alpha = ((EndLaserTurretBuild)build).lightsAlpha;
                    Draw.color(1f, Mathf.absin(offset, 5f, 0.5f) + 0.5f, Mathf.absin(offset + (90f * Mathf.radDeg), 5f, 0.5f) + 0.5f, alpha);
                    Draw.rect(lightRegions[i], build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.rotation - 90f);
                }
                Draw.blend();
                Draw.color();
            }
        };
        unitSort = (e, x, y) -> e.dst2(x, y) + (float)Math.pow(Angles.angleDist(e.rotation, turretRotation), 2);
    }

    @Override
    public void load(){
        super.load();
        lightRegions = new TextureRegion[7];
        for(int i = 0; i < 7; i++){
            lightRegions[i] = Core.atlas.find(name + "-lights-" + i);
        }
    }

    public class EndLaserTurretBuild extends PowerTurretBuild{
        float resistance = 1f;
        float lastHealth;
        float lightsAlpha = 0f;
        boolean rotate = true;
        Bullet bullet;
        private float invFrame = 0f;

        @Override
        public void updateTile(){
            if(health < lastHealth) health = lastHealth;
            if(invFrame < 30f) invFrame += Time.delta;

            super.updateTile();

            boolean b = power.status > 0.0001f;
            lightsAlpha = Mathf.lerpDelta(lightsAlpha, b ? power.status : 0f, !b ? 0.07f : Math.max(power.status * 0.1f, 0.07f));
            resistance = Math.max(1f, resistance - (Time.delta / 20f));

            if(bullet != null){
                rotate = false;
                bullet.rotation(rotation);
                bullet.set(x + recoilOffset.x, y + recoilOffset.y);
                heat = 1f;
                curRecoil = recoil;
                if(bullet.time >= bullet.lifetime || bullet.owner != this) bullet = null;
            }else{
                rotate = true;
            }
        }

        @Override
        protected void findTarget(){
            turretRotation = rotation;
            super.findTarget();
        }

        @Override
        public void damage(float damage){
            if(damage > minDamage) resistance += (damage - minDamage) * resistScl;
            if(invFrame < 30f) return;
            float trueDamage = Mathf.clamp(damage, 0f, minDamageTaken) / resistance;
            lastHealth -= trueDamage;
            super.damage(trueDamage);
        }

        @Override
        public void add(){
            if(added) return;
            super.add();
            if(lastHealth <= 0) lastHealth = block.health;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lastHealth = read.f();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(lastHealth);
        }

        @Override
        public boolean shouldTurn(){
            return true;
        }

        @Override
        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover){
            float
                    xSpread = Mathf.range(xRand),
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);
            bullet = type.create(tile.build, team, bulletX, bulletY, shootAngle);
        }

        @Override
        protected void turnToTarget(float targetRot){
            float speed = rotate ? rotateSpeed * delta() * baseReloadSpeed() : 0f;
            rotation = Angles.moveToward(rotation, targetRot, speed);
        }

        @Override
        protected void updateCooling(){
            if(bullet == null) super.updateCooling();
        }

        @Override
        protected void updateShooting(){
            if(shouldConsume() && queuedBullets <= 0){
                super.updateShooting();
            }
        }

        @Override
        protected float baseReloadSpeed(){
            return bullet == null ? super.baseReloadSpeed() : 0f;
        }

        @Override
        public boolean shouldActiveSound(){
            return bullet != null;
        }
    }
}
