package unity.type.weapons;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class SweepWeapon extends Weapon {
    public float sweepTime = 120.0F;
    public float sweepAngle = 60.0F;

    public SweepWeapon(String name) {
        super(name);
        this.rotate = true;
        this.continuous = true;
        this.mountType = SweepWeaponMount::new;
    }

    public void update(Unit unit, WeaponMount mount) {
        SweepWeaponMount m = (SweepWeaponMount)mount;
        boolean can = unit.canShoot();
        boolean sweep = m.sweep2 != m.sweep;
        float lastReload = mount.reload;
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0.0F);
        mount.recoil = Mathf.approachDelta(mount.recoil, 0.0F, Math.abs(this.recoil) * unit.reloadMultiplier / this.recoilTime);
        if (sweep) {
            float direction = this.sweepAngle * (float)Mathf.sign(m.sweep);
            m.angle = Mathf.approachDelta(m.angle, direction, this.sweepAngle * 2.0F / this.sweepTime);
            if (Mathf.equal(m.angle, direction)) {
                m.sweep2 = m.sweep;
            }
        }

        if (this.rotate && (mount.rotate || mount.shoot) && can) {
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
            float axisY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation + m.angle, this.rotateSpeed * Time.delta);
        } else if (!this.rotate) {
            mount.rotation = 0.0F;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }

        if (sweep && !mount.rotate && !mount.shoot) {
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation + m.angle, this.rotateSpeed * Time.delta);
        }

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
                Teamc var15 = mount.target;
                float var10004;
                if (var15 instanceof Sized) {
                    Sized s = (Sized)var15;
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
                mount.recoil = this.recoil;
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
            mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / this.cooldownTime, 0.0F);
            if (mount.sound != null) {
                mount.sound.update(bulletX, bulletY, false);
            }
        }

        boolean wasFlipped = mount.side;
        if (this.otherSide != -1 && this.alternate && mount.side == this.flipSprite && mount.reload <= this.reload / 2.0F && lastReload > this.reload / 2.0F) {
            unit.mounts[this.otherSide].side = !unit.mounts[this.otherSide].side;
            mount.side = !mount.side;
        }

        if (mount.shoot && can && (!this.useAmmo || unit.ammo > 0.0F || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && (!this.alternate || wasFlipped == this.flipSprite) && unit.vel.len() >= this.minShootVelocity && mount.reload <= 1.0E-4F && Angles.within(this.rotate ? mount.rotation - m.angle : unit.rotation, mount.targetRotation, this.shootCone)) {
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

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        SweepWeaponMount m = (SweepWeaponMount)mount;
        m.sweep = !m.sweep;
        super.shoot(unit, mount, shootX, shootY, aimX, aimY, mountX, mountY, rotation, side);
    }

    static class SweepWeaponMount extends WeaponMount {
        float angle;
        boolean sweep;
        boolean sweep2;

        SweepWeaponMount(Weapon weapon) {
            super(weapon);
            SweepWeapon w = (SweepWeapon)weapon;
            this.angle = w.sweepAngle * (float)Mathf.sign(weapon.flipSprite);
            this.sweep = this.sweep2 = weapon.flipSprite;
        }
    }
}
