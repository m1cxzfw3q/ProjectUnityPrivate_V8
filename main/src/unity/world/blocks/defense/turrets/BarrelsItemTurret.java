package unity.world.blocks.defense.turrets;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Time;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.ShootPattern;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.DrawTurret;

public class BarrelsItemTurret extends ItemTurret{
    protected final Seq<Barrel> barrels = new Seq<>(1);
    protected boolean focus;

    public BarrelsItemTurret(String name){
        super(name);

        ((DrawTurret) drawer).basePrefix = "unity-block-";
    }

    protected void addBarrel(float x, float y, float reload){
        barrels.add(new Barrel(x, y, reload));
    }

    protected class Barrel{
        public final float x, y, reload;

        public Barrel(float x, float y, float reload){
            this.x = x;
            this.y = y;
            this.reload = reload;
        }
    }

    public class BarrelsItemTurretBuild extends ItemTurretBuild{
        protected float[] barrelReloads = new float[barrels.size];
        protected int[] barrelCounters = new int[barrels.size];

        @Override
        protected void updateShooting() {
            super.updateShooting();

            for (int i = 0;i < barrels.size;i++) {
                Barrel barrel = barrels.get(i);
                if(barrelReloads[i] >= barrel.reload && !charging() && shootWarmup >= minWarmup){
                    shootBarrel(peekAmmo(), i);
                    barrelReloads[i] %= barrel.reload;
                }
            }
        }

        @Override
        protected void updateReload() {
            super.updateReload();

            for (int i = 0;i < barrels.size;i++) {
                Barrel barrel = barrels.get(i);
                barrelReloads[i] += delta() * ammoReloadMultiplier() * baseReloadSpeed();

                //cap reload for visual reasons
                barrelReloads[i] = Math.min(barrelReloads[i], barrel.reload);
            }
        }

        protected void shootBarrel(BulletType type, int index){
            Barrel barrel = barrels.get(index);
            float
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + barrel.x, shootY + barrel.y),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + barrel.x, shootY + barrel.y);

            if(shoot.firstShotDelay > 0){
                chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                type.chargeEffect.at(bulletX, bulletY, rotation);
            }

            ShootPattern pattern = type.shootPattern != null ? type.shootPattern : shoot;

            pattern.shoot(barrelCounters[index], (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                int barrelCount = barrelCounters[index];

                if(delay > 0f){
                    Time.run(delay, () -> {
                        //hack: make sure the barrel is the same as what it was when the bullet was queued to fire
                        int prev = barrelCounters[index];
                        barrelCounters[index] = barrelCount;
                        bullet(type, xOffset + barrel.x, yOffset + barrel.y, angle, mover);
                        barrelCounters[index] = prev;
                    });
                }else{
                    bullet(type, xOffset + barrel.x, yOffset + barrel.y, angle, mover);
                }
            }, () -> barrelCounters[index]++);

            if(consumeAmmoOnce){
                useAmmo();
            }
        }
    }
}
