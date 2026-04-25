package unity.type.weapons.monolith;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import unity.gen.Trns;
import unity.util.Utils;

public class ChargeShotgunWeapon extends Weapon {
    private static final Vec2 tmp = new Vec2();
    public float addSequenceTime = 38.0F;
    public float weaveScale = 24.0F;
    public float weaveAmount = 30.0F;
    public float angleStrideScale = 10.0F;
    public Effect addEffect;
    public Effect addedEffect;
    public Effect releaseEffect;

    public ChargeShotgunWeapon(String name) {
        super(name);
        this.addEffect = Fx.lancerLaserCharge;
        this.addedEffect = Fx.lightningShoot;
        this.releaseEffect = Fx.none;
        this.mountType = ChargeShotgunMount::new;
    }

    protected Vec2 chargePos(Vec2 local, Unit unit, ChargeShotgunMount mount) {
        float weaponRotation = unit.rotation - 90.0F + (this.rotate ? mount.rotation : 0.0F);
        float mountX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
        float mountY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
        return tmp.trns(weaponRotation, local.x, local.y).add(mountX, mountY);
    }

    public void draw(Unit unit, WeaponMount mount) {
        super.draw(unit, mount);
        ChargeShotgunMount m = (ChargeShotgunMount)mount;
        float weaponRotation = unit.rotation - 90.0F + (this.rotate ? m.rotation : 0.0F);
        float mountX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
        float mountY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
        float bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
        float bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
        float shootAngle = this.rotate ? weaponRotation + 90.0F : Angles.angle(bulletX, bulletY, m.aimX, m.aimY) + (unit.rotation - unit.angleTo(m.aimX, m.aimY));

        for(int i = 0; i < m.added.size - 1; i += 2) {
            Vec2 current = (Vec2)m.added.get(i);
            if (!Float.isNaN(current.x) && !Float.isNaN(current.y)) {
                Vec2 pos = this.chargePos(current, unit, m);
                float rot = shootAngle + Utils.angleDistSigned(shootAngle, Angles.angle(mountX, mountY, pos.x, pos.y)) / this.angleStrideScale;
                this.drawCharge(pos.x, pos.y, weaponRotation, rot, unit, m);
            }
        }

    }

    public void drawCharge(float x, float y, float rotation, float shootAngle, Unit unit, ChargeShotgunMount mount) {
    }

    public void update(Unit unit, WeaponMount mount) {
        ChargeShotgunMount m = (ChargeShotgunMount)mount;
        m.transform.parent = unit;
        m.transform.offsetRot = this.rotate ? m.rotation : 0.0F;
        m.transform.update();
        float weaponRotation = unit.rotation - 90.0F + (this.rotate ? m.rotation : 0.0F);
        float mountX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
        float mountY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
        float bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
        float bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
        float shootAngle = this.rotate ? weaponRotation + 90.0F : Angles.angle(bulletX, bulletY, m.aimX, m.aimY) + (unit.rotation - unit.angleTo(m.aimX, m.aimY));
        float addTime = Math.max(this.reload - this.addSequenceTime, 0.0F);
        if (!m.releasing) {
            if (!m.adding) {
                if (m.loaded() < this.shoot.shots && (m.add += Time.delta * unit.reloadMultiplier) >= addTime) {
                    m.adding = true;
                    m.added.add(new Vec2(Float.NaN, Float.NaN), new Vec2(this.shootX, this.shootY));
                    m.addSequence = m.add % addTime;
                    m.add = 0.0F;
                    this.addEffect.at(bulletX, bulletY, weaponRotation, m.transform);
                }
            } else if ((m.addSequence += Time.delta) >= this.addSequenceTime && m.added.any()) {
                m.adding = false;
                ((Vec2)m.added.get(m.added.size - 2)).set(this.shootX, this.shootY);
                m.add = m.addSequence % this.addSequenceTime;
                m.addSequence = 0.0F;
                this.addedEffect.at(bulletX, bulletY, weaponRotation, m.transform);
            }
        } else {
            m.adding = false;
            m.add = m.addSequence = 0.0F;
        }

        for(int i = 0; i < m.added.size - 1; i += 2) {
            Vec2 current = (Vec2)m.added.get(i);
            Vec2 target = (Vec2)m.added.get(i + 1);
            if (!m.releasing) {
                target.setAngle(Mathf.sin(Time.time + Mathf.randomSeed((long)unit.id, ((float)Math.PI * 2F) * this.weaveScale) + ((float)Math.PI * 2F) * this.weaveScale * ((float)i / (float)m.added.size), this.weaveScale, this.weaveAmount / 2.0F * (float)m.loaded()) + 90.0F);
            }

            if (!Float.isNaN(current.x) && !Float.isNaN(current.y)) {
                current.setAngle(Mathf.slerpDelta(current.angle(), target.angle(), 0.08F));
            }
        }

        boolean can = unit.canShoot();
        m.recoil = Mathf.approachDelta(m.recoil, 0.0F, Math.abs(this.recoil) * unit.reloadMultiplier / this.recoilTime);
        if (this.rotate && (m.rotate || m.shoot) && can) {
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
            float axisY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
            m.targetRotation = Angles.angle(axisX, axisY, m.aimX, m.aimY) - unit.rotation;
            m.rotation = Angles.moveToward(m.rotation, m.targetRotation, this.rotateSpeed * Time.delta);
        } else if (!this.rotate) {
            m.rotation = 0.0F;
            m.targetRotation = unit.angleTo(m.aimX, m.aimY);
        }

        if (!this.controllable && this.autoTarget) {
            if ((m.retarget -= Time.delta) <= 0.0F) {
                m.target = this.findTarget(unit, mountX, mountY, this.bullet.range, this.bullet.collidesAir, this.bullet.collidesGround);
                m.retarget = m.target == null ? this.targetInterval : this.targetSwitchInterval;
            }

            if (m.target != null && this.checkTarget(unit, m.target, mountX, mountY, this.bullet.range)) {
                m.target = null;
            }

            boolean shoot = false;
            if (m.target != null) {
                Teamc var10000 = m.target;
                float var10003 = this.bullet.range + Math.abs(this.shootY);
                Teamc var14 = m.target;
                float var10004 = var14 instanceof Sized s ? s.hitSize() / 2f : 0f;

                shoot = var10000.within(mountX, mountY, var10003 + var10004) && can;
                if (this.predictTarget) {
                    Vec2 to = Predict.intercept(unit, m.target, this.bullet.speed);
                    m.aimX = to.x;
                    m.aimY = to.y;
                } else {
                    m.aimX = m.target.x();
                    m.aimY = m.target.y();
                }
            }

            m.shoot = m.rotate = shoot;
        }

        if (this.continuous && m.bullet != null) {
            if (m.bullet.isAdded() && !(m.bullet.time >= m.bullet.lifetime) && m.bullet.type == this.bullet) {
                m.bullet.rotation(weaponRotation + 90.0F);
                m.bullet.set(bulletX, bulletY);
                m.recoil = this.recoil;
                unit.vel.add(Tmp.v1.trns(unit.rotation + 180.0F, m.bullet.type.recoil));
                if (this.shootSound != Sounds.none && !Vars.headless) {
                    if (m.sound == null) {
                        m.sound = new SoundLoop(this.shootSound, 1.0F);
                    }

                    m.sound.update(bulletX, bulletY, true);
                }
            } else {
                m.bullet = null;
            }
        } else {
            m.heat = Math.max(m.heat - Time.delta * unit.reloadMultiplier / this.cooldownTime, 0.0F);
            if (m.sound != null) {
                m.sound.update(bulletX, bulletY, false);
            }
        }

        if (m.shoot && can && (!this.useAmmo || unit.ammo > 0.0F || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && (!this.alternate || m.side == this.flipSprite) && unit.vel.len() >= this.minShootVelocity && m.loaded() > 0 && !m.releasing && Angles.within(this.rotate ? m.rotation : unit.rotation, m.targetRotation, this.shootCone)) {
            this.shoot(unit, m, bulletX, bulletY, m.aimX, m.aimY, mountX, mountY, shootAngle, Mathf.sign(this.x));
            if (this.otherSide != -1 && this.alternate && m.side == this.flipSprite) {
                unit.mounts[this.otherSide].side = !unit.mounts[this.otherSide].side;
                m.side = !m.side;
            }

            if (this.useAmmo) {
                --unit.ammo;
                if (unit.ammo < 0.0F) {
                    unit.ammo = 0.0F;
                }
            }
        }

    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        ChargeShotgunMount m = (ChargeShotgunMount)mount;
        float baseX = unit.x;
        float baseY = unit.y;
        boolean delay = this.shoot.firstShotDelay + this.shoot.shotDelay > 0.0F;
        BulletType ammo = this.bullet;
        boolean parentize = ammo.keepVelocity || this.parentizeEffects;
        if (delay) {
            m.releasing = true;
            if (m.adding) {
                m.added.removeRange(m.added.size - 2, m.added.size - 1);
                m.adding = false;
            }

            for(int i = 0; i < m.added.size - 1; i += 2) {
                Vec2 current = (Vec2)m.added.get(i);
                Time.run((float)i / 2.0F * this.shoot.shotDelay + this.shoot.firstShotDelay, () -> {
                    if (unit.isAdded()) {
                        Vec2 pos = this.chargePos(current, unit, m);
                        float rot = rotation + Utils.angleDistSigned(rotation, Angles.angle(mountX, mountY, pos.x, pos.y)) / this.angleStrideScale;
                        this.bullet(unit, mount, pos.x, pos.y, Mathf.range(inaccuracy), b -> {});
                        this.shootSound.at(pos.x, pos.y, Mathf.random(this.soundPitchMin, this.soundPitchMax));
                        unit.vel.add(Tmp.v1.trns(rotation + 180.0F, ammo.recoil));
                        Effect.shake(this.shake, this.shake, pos.x, pos.y);
                        ammo.shootEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                        ammo.smokeEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                        mount.recoil = this.recoil;
                        mount.heat = 1.0F;
                        current.x = current.y = Float.NaN;
                    }
                });
                Vec2 pos = this.chargePos(current, unit, m);
                float rot = rotation + Utils.angleDistSigned(rotation, Angles.angle(mountX, mountY, pos.x, pos.y)) / this.angleStrideScale;
                this.releaseEffect.at(pos.x, pos.y, rot, parentize ? m.transform : null);
            }

            Time.run((float)(m.loaded() - 1) * this.shoot.shotDelay + this.shoot.firstShotDelay, () -> {
                m.releasing = false;
                m.added.clear();
            });
            Time.run(this.shoot.firstShotDelay, () -> {
                if (unit.isAdded()) {
                    ammo.chargeEffect.at(shootX + unit.x - baseX, shootY + unit.y - baseY, rotation, parentize ? unit : null);
                }
            });
        } else {
            for(int i = 0; i < m.added.size - 1; i += 2) {
                Vec2 current = (Vec2)m.added.get(i);
                if (!Float.isNaN(current.x) && !Float.isNaN(current.y)) {
                    Vec2 pos = this.chargePos(current, unit, m);
                    float rot = rotation + Utils.angleDistSigned(rotation, Angles.angle(mountX, mountY, pos.x, pos.y)) / this.angleStrideScale;
                    this.bullet(unit, mount, pos.x, pos.y, rot + Mathf.range(this.inaccuracy), b -> {});
                    this.shootSound.at(pos.x, pos.y, Mathf.random(this.soundPitchMin, this.soundPitchMax));
                    Effect.shake(this.shake, this.shake, pos.x, pos.y);
                    ammo.shootEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                    ammo.smokeEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                }
            }

            unit.vel.add(Tmp.v1.trns(rotation + 180.0F, ammo.recoil));
            mount.recoil = this.recoil;
            mount.heat = 1.0F;
            m.added.clear();
        }

        this.ejectEffect.at(mountX, mountY, rotation * (float)side);
        unit.apply(this.shootStatus, this.shootStatusDuration);
    }

    public static class ChargeShotgunMount extends WeaponMount {
        public Trns transform = Trns.create();
        public boolean adding;
        public Seq<Vec2> added = new Seq<>();
        public float add;
        public float addSequence;
        public boolean releasing;

        public ChargeShotgunMount(Weapon weapon) {
            super(weapon);
            this.transform.offsetX = weapon.x;
            this.transform.offsetY = weapon.y;
        }

        public int loaded() {
            return this.added.size / 2 - (this.adding ? 1 : 0);
        }
    }
}
