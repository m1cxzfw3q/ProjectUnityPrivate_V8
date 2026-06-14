package unity.world.blocks.defense.turrets;

import arc.audio.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

public class BurstPowerTurret extends PowerTurret{
    protected BulletType subShootType;
    protected int subShots = 1;
    protected float subBurstSpacing;
    protected Effect subShootEffect = Fx.none;
    protected Sound subShootSound = Sounds.none;

    public BurstPowerTurret(String name){
        super(name);
    }

    public class BurstPowerTurretBuild extends PowerTurretBuild{
        @Override
        public void shoot(BulletType type){
            float
                    bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);

            if(shoot.firstShotDelay > 0){
                chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                type.chargeEffect.at(bulletX, bulletY, rotation);
            }

            ShootPattern pattern = type.shootPattern != null ? type.shootPattern : shoot;

            pattern.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                int barrel = barrelCounter;

                if(delay > 0f){
                    Time.run(delay, () -> {
                        //hack: make sure the barrel is the same as what it was when the bullet was queued to fire
                        int prev = barrelCounter;
                        barrelCounter = barrel;
                        bullet(type, xOffset, yOffset, angle, mover);
                        barrelCounter = prev;
                    });
                }else{
                    bullet(type, xOffset, yOffset, angle, mover);
                }
            }, () -> barrelCounter++);
            for(int i = 0; i < subShots; i++){
                Time.run(subBurstSpacing * i, () -> {
                    bullet(subShootType, 0, 0, 0, null);
                    subShootEffect.at(x + recoilOffset.x, y + recoilOffset.y, rotation);
                    subShootSound.at(x + recoilOffset.x, y + recoilOffset.y, 1f);
                });
            }

            if(consumeAmmoOnce){
                useAmmo();
            }
        }
    }
}
