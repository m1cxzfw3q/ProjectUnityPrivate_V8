package unity.type.weapons;

import arc.func.Boolf;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Posc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import unity.util.Utils;

public class LimitedAngleWeapon extends Weapon {
    public float angleCone = 45.0F;
    public float angleOffset = 0.0F;
    public float defaultAngle = 0.0F;

    public LimitedAngleWeapon(String name) {
        super(name);
        this.mountType = (weapon) -> {
            WeaponMount mount = new WeaponMount(weapon);
            mount.rotation = this.defaultAngle * (float)Mathf.sign(this.flipSprite);
            return mount;
        };
    }

    public void update(Unit unit, WeaponMount mount) {
        boolean can = unit.canShoot();
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0.0F);
        mount.recoil = Mathf.approachDelta(mount.recoil, 0.0F, Math.abs(this.recoil) * unit.reloadMultiplier / this.recoilTime);
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

                    mount.sound.update(this.x, this.y, true);
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
            mount.rotation = Utils.clampedAngle(mount.rotation, this.angleOffset * (float)Mathf.sign(this.flipSprite), this.angleCone);
        } else if (!this.rotate) {
            mount.rotation = 0.0F;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }

        if (mount.shoot && can && (!this.useAmmo || unit.ammo > 0.0F || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && (!this.alternate || mount.side == this.flipSprite) && (unit.vel.len() >= mount.weapon.minShootVelocity || Vars.net.active() && !unit.isLocal()) && mount.reload <= 1.0E-4F && Angles.within(this.rotate ? mount.rotation : unit.rotation, mount.targetRotation, mount.weapon.shootCone)) {
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

    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        Boolf<Posc> angBool = (e) -> Utils.angleDist(unit.rotation + this.angleOffset * (float)Mathf.sign(this.flipSprite), unit.angleTo(e)) <= this.angleCone;
        return Units.closestTarget(unit.team, x, y, range + Math.abs(this.shootY), (u) -> u.checkTarget(air, ground) && angBool.get(u), (t) -> ground && angBool.get(t));
    }

    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
        return super.checkTarget(unit, target, x, y, range) || Utils.angleDist(unit.rotation + this.angleOffset * (float)Mathf.sign(this.flipSprite), unit.angleTo(target)) > this.angleCone;
    }
}
