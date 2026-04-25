package unity.type.weapons;

import arc.func.Cons2;
import arc.func.Cons3;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.Effect;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class EnergyChargeWeapon extends Weapon {
    public Cons3<Unit, WeaponMount, Float> drawCharge = (unit, mount, charge) -> {
    };
    public Cons2<Unit, WeaponMount> chargeCondition;
    public boolean drawTop = true;
    public boolean startUncharged = true;
    public boolean drawRegion = true;
    private int sequenceNum;

    public EnergyChargeWeapon(String name) {
        super(name);
        this.mountType = (w) -> {
            WeaponMount m = new ChargeMount(w);
            m.reload = this.startUncharged ? this.reload : 0.0F;
            return m;
        };
    }

    public void drawOutline(Unit unit, WeaponMount mount) {
        if (this.drawRegion) {
            super.drawOutline(unit, mount);
        }

    }

    public void draw(Unit unit, WeaponMount mount) {
        float tmp = mount.reload;
        mount.reload = Mathf.clamp(mount.reload, 0.0F, this.reload);
        if (!this.drawTop) {
            this.drawCharge.get(unit, mount, 1.0F - Mathf.clamp(mount.reload / this.reload));
        }

        if (this.drawRegion) {
            super.draw(unit, mount);
        }

        mount.reload = tmp;
        if (this.drawTop) {
            this.drawCharge.get(unit, mount, 1.0F - Mathf.clamp(mount.reload / this.reload));
        }

    }

    public void update(Unit unit, WeaponMount mount) {
        if (this.chargeCondition == null) {
            super.update(unit, mount);
        } else {
            boolean can = unit.canShoot();
            this.chargeCondition.get(unit, mount);
            float weaponRotation = unit.rotation - 90.0F + (this.rotate ? mount.rotation : 0.0F);
            float mountX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
            float mountY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
            float bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
            float bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
            float shootAngle = this.rotate ? weaponRotation + 90.0F : Angles.angle(bulletX, bulletY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
            if (!this.controllable && this.autoTarget) {
                if ((mount.retarget -= Time.delta) <= 0.0F) {
                    mount.target = this.findTarget(unit, mountX, mountY, this.bullet.range(), this.bullet.collidesAir, this.bullet.collidesGround);
                    mount.retarget = mount.target == null ? this.targetInterval : this.targetSwitchInterval;
                }

                if (mount.target != null && this.checkTarget(unit, mount.target, mountX, mountY, this.bullet.range())) {
                    mount.target = null;
                }

                boolean shoot = false;
                if (mount.target != null) {
                    Teamc var10000 = mount.target;
                    float var10003 = this.bullet.range() + Math.abs(this.shootY);
                    Teamc var12 = mount.target;
                    float var10004;
                    if (var12 instanceof Sized) {
                        Sized s = (Sized)var12;
                        var10004 = s.hitSize() / 2.0F;
                    } else {
                        var10004 = 0.0F;
                    }

                    shoot = var10000.within(mountX, mountY, var10003 + var10004) && can;
                    if (this.predictTarget) {
                        Vec2 to = Predict.intercept(unit, mount.target, this.bullet.speed);
                        mount.aimX = to.x;
                        mount.aimY = to.y;
                    } else {
                        mount.aimX = mount.target.x();
                        mount.aimY = mount.target.y();
                    }
                }

                mount.shoot = mount.rotate = shoot;
            }

            if (this.continuous && mount.bullet != null) {
                if (mount.bullet.isAdded() && !(mount.bullet.time >= mount.bullet.lifetime) && mount.bullet.type == this.bullet) {
                    mount.bullet.rotation(weaponRotation + 90.0F);
                    mount.bullet.set(bulletX, bulletY);
                    mount.reload = this.reload;
                    unit.vel.add(Tmp.v1.trns(unit.rotation + 180.0F, mount.bullet.type.recoil));
                    if (this.shootSound != Sounds.none && !Vars.headless) {
                        if (mount.sound == null) {
                            mount.sound = new SoundLoop(this.shootSound, 1.0F);
                        }

                        mount.sound.update(bulletX, bulletY, true);
                    }
                } else {
                    mount.bullet = null;
                }
            } else {
                mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / mount.weapon.cooldownTime, 0.0F);
                if (mount.sound != null) {
                    mount.sound.update(bulletX, bulletY, false);
                }
            }

            if (this.otherSide != -1 && this.alternate && mount.side == this.flipSprite && mount.reload + Time.delta * unit.reloadMultiplier > this.reload / 2.0F && mount.reload <= this.reload / 2.0F) {
                unit.mounts[this.otherSide].side = !unit.mounts[this.otherSide].side;
                mount.side = !mount.side;
            }

            if (this.rotate && (mount.rotate || mount.shoot) && can) {
                float axisX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
                float axisY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
                mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
                mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, this.rotateSpeed * Time.delta);
            } else if (!this.rotate) {
                mount.rotation = 0.0F;
                mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
            }

            if (mount.shoot && can && (!this.useAmmo || unit.ammo > 0.0F || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && (!this.alternate || mount.side == this.flipSprite) && unit.vel.len() >= mount.weapon.minShootVelocity && mount.reload <= 1.0E-4F && Angles.within(this.rotate ? mount.rotation : unit.rotation, mount.targetRotation, mount.weapon.shootCone)) {
                this.shoot(unit, mount, bulletX, bulletY, mount.aimX, mount.aimY, mountX, mountY, shootAngle, Mathf.sign(this.x));
                mount.reload = this.reload;
                if (this.useAmmo) {
                    --unit.ammo;
                    if (unit.ammo < 0.0F) {
                        unit.ammo = 0.0F;
                    }
                }
            }
        }

    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        if (this.chargeCondition == null) {
            super.shoot(unit, mount, shootX, shootY, aimX, aimY, mountX, mountY, rotation, side);
        } else {
            float baseX = unit.x;
            float baseY = unit.y;
            boolean delay = this.firstShotDelay + this.shotDelay > 0.0F;
            (delay ? this.chargeSound : (this.continuous ? Sounds.none : this.shootSound)).at(shootX, shootY, Mathf.random(this.soundPitchMin, this.soundPitchMax));
            BulletType ammo = this.bullet;
            float lifeScl = ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(shootX, shootY, aimX, aimY) / ammo.range()) : 1.0F;
            float charge = Math.max(0.0F, -mount.reload);
            this.sequenceNum = 0;
            if (delay) {
                Angles.shotgun(this.shots, this.spacing, rotation, (f) -> {
                    Time.run((float)this.sequenceNum * this.shotDelay + this.firstShotDelay, () -> {
                        if (unit.isAdded()) {
                            mount.bullet = this.bulletC(unit, shootX + unit.x - baseX, shootY + unit.y - baseY, f + Mathf.range(this.inaccuracy), lifeScl, charge);
                            if (!this.continuous) {
                                this.shootSound.at(shootX, shootY, Mathf.random(this.soundPitchMin, this.soundPitchMax));
                            }

                        }
                    });
                    ++this.sequenceNum;
                });
            } else {
                Angles.shotgun(this.shots, this.spacing, rotation, (f) -> mount.bullet = this.bulletC(unit, shootX, shootY, f + Mathf.range(this.inaccuracy), lifeScl, charge));
            }

            boolean parentize = ammo.keepVelocity;
            if (delay) {
                Time.run(this.firstShotDelay, () -> {
                    if (unit.isAdded()) {
                        unit.vel.add(Tmp.v1.trns(rotation + 180.0F, ammo.recoil));
                        Effect.shake(this.shake, this.shake, shootX, shootY);
                        mount.heat = 1.0F;
                        if (!this.continuous) {
                            this.shootSound.at(shootX, shootY, Mathf.random(this.soundPitchMin, this.soundPitchMax));
                        }

                    }
                });
            } else {
                unit.vel.add(Tmp.v1.trns(rotation + 180.0F, ammo.recoil));
                Effect.shake(this.shake, this.shake, shootX, shootY);
                mount.heat = 1.0F;
            }

            this.ejectEffect.at(mountX, mountY, rotation * (float)side);
            ammo.shootEffect.at(shootX, shootY, rotation, parentize ? unit : null);
            ammo.smokeEffect.at(shootX, shootY, rotation, parentize ? unit : null);
            unit.apply(this.shootStatus, this.shootStatusDuration);
        }

    }

    Bullet bulletC(Unit unit, float shootX, float shootY, float angle, float lifescl, float charge) {
        float xr = Mathf.range(this.xRand);
        return this.bullet.create(unit, unit.team, shootX + Angles.trnsx(angle, 0.0F, xr), shootY + Angles.trnsy(angle, 0.0F, xr), angle, this.bullet.damage + charge, 1.0F - this.velocityRnd + Mathf.random(this.velocityRnd), lifescl, (Object)null);
    }

    public static class ChargeMount extends WeaponMount {
        public float timer = 0.0F;
        public float charge = 0.0F;

        public ChargeMount(Weapon weapon) {
            super(weapon);
        }
    }
}
