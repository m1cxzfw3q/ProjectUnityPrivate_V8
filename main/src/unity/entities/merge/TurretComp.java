package unity.entities.merge;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.LaserTurret.*;
import mindustry.world.blocks.defense.turrets.LiquidTurret.*;
import unity.annotations.Annotations.*;
import unity.gen.Expc.*;
import unity.gen.*;
import unity.gen.Turretc.*;
import unity.v8.V7Bullets;

//this is as if disappointment is interpreted through codes
@SuppressWarnings({"unused", "unchecked"})
@MergeComponent
abstract class TurretComp extends Turret implements Stemc{
    /** Color of shoot effects. Shifts to second color as the turret levels up. */
    Color fromColor = Pal.lancerLaser, toColor = Pal.sapBullet;
    boolean lerpColor = false;

    Color rangeColor;

    /** Whether to accept ammo type of all kinds */
    boolean omni = false;
    BulletType defaultBullet = V7Bullets.standardCopper;

    float basicFieldRadius = -1f;

    @ReadOnly Func<TurretBuildc, Object> bulletData = b -> null;
    @ReadOnly Cons2<BulletType, Bullet> bulletCons = (type, b) -> {};

    public TurretComp(String name){
        super(name);
    }

    public <T extends TurretBuildc> void bulletData(Func<T, Object> bulletData){
        this.bulletData = (Func<TurretBuildc, Object>)bulletData;
    }

    public <T extends BulletType> void bulletCons(Cons2<T, Bullet> bulletCons){
        this.bulletCons = (Cons2<BulletType, Bullet>)bulletCons;
    }

    public abstract class TurretBuildComp extends TurretBuild implements StemBuildc{
        @Override
        @Replace
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(self() instanceof LiquidTurretBuild && omni){
                return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
            }else{
                return super.acceptLiquid(source, liquid);
            }
        }

        @Override
        @Replace
        public BulletType useAmmo(){
            if(self() instanceof LiquidTurretBuild && omni){
                BulletType type = peekAmmo();
                if(cheating()) return type;
                liquids.remove(liquids.current(), 1f / type.ammoMultiplier);
                return type;
            }else{
                return super.useAmmo();
            }
        }

        @Override
        @Replace
        public BulletType peekAmmo(){
            if(block instanceof LiquidTurret l && omni){
                BulletType b = l.ammoTypes.get(liquids.current());
                return b == null ? defaultBullet : b;
            }else{
                return super.peekAmmo();
            }
        }

        @Override
        @Replace
        public boolean hasAmmo(){
            if(self() instanceof LiquidTurretBuild && omni){
                return peekAmmo() != null && liquids.currentAmount() >= 1f / peekAmmo().ammoMultiplier;
            }else{
                return super.hasAmmo();
            }
        }

        @Override
        @Replace
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, rangeColor == null ? team.color : rangeColor);
        }

        @Override
        @Replace
        public void shoot(BulletType type){
            if(shoot.firstShotDelay > 0f && this instanceof ExpBuildc){
                var exp = (TurretBuild & ExpBuildc)this;

                useAmmo();

                float bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
                        bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);

                if(shoot.firstShotDelay > 0){
                    chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                    type.chargeEffect.at(bulletX, bulletY, rotation, getShootColor(exp.levelf()));
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
                            bullet1(type, xOffset, yOffset, angle, mover);
                            barrelCounter = prev;
                        });
                    }else{
                        bullet(type, xOffset, yOffset, angle, mover);
                    }
                }, () -> barrelCounter++);
            }else{
                super.shoot(type);
            }
        }

        public Object bulletData(){
            return bulletData.get(self());
        }

        public void bulletCons(BulletType type, Bullet b){
            bulletCons.get(type, b);
        }

        protected Bullet bullet1(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover){
            queuedBullets --;
            Bullet b = null;

            if(!(dead || (!consumeAmmoOnce && !hasAmmo()))) {
                float
                        xSpread = Mathf.range(xRand),
                        bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                        bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                        shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);

                float lifeScl = type.scaleLife ? Mathf.clamp((1 + scaleLifetimeOffset) * Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange() / type.range, range() / type.range) : 1f;

                 b = type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y);

                //TODO aimX / aimY for multi shot turrets?
                handleBullet(b, xOffset, yOffset, shootAngle - rotation);

                (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
                (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
                (type.shootSound != Sounds.none ? type.shootSound : shootSound).at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax), shootSoundVolume);

                ammoUseEffect.at(x - Angles.trnsx(rotation, ammoEjectBack), y - Angles.trnsy(rotation, ammoEjectBack), rotation * Mathf.sign(xOffset));

                if (shake > 0) {
                    Effect.shake(shake, shake, this);
                }

                curRecoil = 1f;
                if (recoils > 0) {
                    curRecoils[barrelCounter % recoils] = 1f;
                }
                heat = 1f;
                totalShots++;

                if (!consumeAmmoOnce) {
                    useAmmo();
                }
            }
            return b;
        }

        public Color getShootColor(float progress){
            return Tmp.c1.set(fromColor).lerp(toColor, progress).cpy();
        }
    }
}
