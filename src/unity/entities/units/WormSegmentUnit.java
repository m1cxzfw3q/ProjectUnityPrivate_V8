package unity.entities.units;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.AIController;
import mindustry.entities.units.UnitController;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Hitboxc;
import mindustry.gen.Player;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import unity.ai.WormAI;
import unity.gen.UnityEntityMapping;
import unity.type.UnityUnitType;

public class WormSegmentUnit extends UnitEntity {
    public UnityUnitType wormType;
    protected float segmentHealth;
    protected WormDefaultUnit trueParentUnit;
    protected Unit parentUnit;
    protected boolean isBugged;
    protected int shootSequence;
    protected int segmentType;

    public int getSegmentLength() {
        return this.wormType.segmentLength;
    }

    public void type(UnitType type) {
        super.type(type);
        if (type instanceof UnityUnitType) {
            UnityUnitType w = (UnityUnitType)type;
            this.wormType = w;
        } else {
            throw new ClassCastException("you set this unit's type a in sneaky way");
        }
    }

    public boolean collides(Hitboxc other) {
        if (this.trueParentUnit == null) {
            return true;
        } else {
            WormSegmentUnit[] segs = this.trueParentUnit.segmentUnits;
            int i = 0;

            for(int len = this.getSegmentLength(); i < len; ++i) {
                if (segs[i] == other) {
                    return false;
                }
            }

            return true;
        }
    }

    public void add() {
        if (!this.added) {
            this.isBugged = true;
            Groups.all.add(this);
            Groups.unit.add(this);
            Groups.sync.add(this);
            this.added = true;
            this.updateLastPosition();
        }
    }

    public void setType(UnitType type) {
        this.type = type;
        this.maxHealth = this.segmentHealth = type.health;
        this.drag = type.drag;
        this.armor = type.armor;
        this.hitSize = type.hitSize;
        this.hovering = type.hovering;
        if (this.controller == null) {
            this.controller(type.createController());
        }

        if (this.mounts().length != type.weapons.size) {
            this.setupWeapons(type);
        }

        if (type instanceof UnityUnitType) {
            UnityUnitType w = (UnityUnitType)type;
            this.wormType = w;
        } else {
            throw new ClassCastException("you set this unit's type in sneaky way");
        }
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.unit.remove(this);
            Groups.sync.remove(this);
            this.added = false;
            this.controller.removed(this);
            if (Vars.net.client()) {
                Vars.netClient.addRemovedEntity(this.id);
            }

        }
    }

    public void damage(float amount) {
        if (this.wormType.splittable) {
            this.segmentHealth -= amount * this.wormType.segmentDamageScl;
        }

        this.trueParentUnit.damage(amount);
        if (this.trueParentUnit.controller instanceof WormAI) {
            ((WormAI)this.trueParentUnit.controller).setTarget(this.x, this.y, amount);
        }

    }

    public void segmentDamage(float amount) {
        this.segmentHealth -= amount;
    }

    public void controller(UnitController next) {
        if (!(next instanceof Player)) {
            this.controller = next;
            if (this.controller.unit() != this) {
                this.controller.unit(this);
            }
        } else if (this.trueParentUnit != null) {
            this.trueParentUnit.controller = next;
            if (this.trueParentUnit.controller.unit() != this.trueParentUnit) {
                this.trueParentUnit.controller.unit(this.trueParentUnit);
            }
        }

    }

    public boolean isPlayer() {
        return this.trueParentUnit == null ? false : this.trueParentUnit.controller instanceof Player;
    }

    public boolean isAI() {
        return this.trueParentUnit == null ? true : this.trueParentUnit.controller instanceof AIController;
    }

    public boolean isCounted() {
        return false;
    }

    public Player getPlayer() {
        if (this.trueParentUnit == null) {
            return null;
        } else {
            return this.isPlayer() ? (Player)this.trueParentUnit.controller : null;
        }
    }

    public int classId() {
        return UnityEntityMapping.classId(WormSegmentUnit.class);
    }

    public void heal(float amount) {
        if (this.trueParentUnit != null) {
            this.trueParentUnit.heal(amount);
        }

        this.health += amount;
        this.segmentHealth = Mathf.clamp(this.segmentHealth + amount, 0.0F, this.maxHealth);
        this.clampHealth();
    }

    public void kill() {
        if (!this.dead && !Vars.net.client()) {
            if (this.trueParentUnit != null) {
                Call.unitDeath(this.trueParentUnit.id);
            }

            Call.unitDeath(this.id);
        }
    }

    public void setSegmentType(int val) {
        this.segmentType = val;
    }

    public void setupWeapons(UnitType def) {
        if (def instanceof UnityUnitType) {
            UnityUnitType w = (UnityUnitType)def;
            Seq tmpSeq = new Seq();
            Seq originSeq = w.segWeapSeq;

            for(int i = 0; i < originSeq.size; ++i) {
                tmpSeq.add(new WeaponMount((Weapon)originSeq.get(i)));
            }

            this.mounts = (WeaponMount[])tmpSeq.toArray(WeaponMount.class);
        } else {
            super.setupWeapons(def);
        }

    }

    public boolean serialize() {
        return false;
    }

    public void update() {
        if (this.parentUnit == null || this.parentUnit.dead || !this.parentUnit.isAdded()) {
            this.dead = true;
            this.remove();
        }

        if (this.trueParentUnit != null && this.isBugged) {
            if (!Structs.contains(this.trueParentUnit.segmentUnits, (s) -> s == this)) {
                this.remove();
            } else {
                this.isBugged = false;
            }
        }

    }

    public void wormSegmentUpdate() {
        if (this.trueParentUnit != null) {
            if (this.wormType.splittable && this.wormType.healthDistribution <= 0.0F) {
                this.maxHealth = this.trueParentUnit.maxHealth;
            }

            if (!this.wormType.splittable) {
                this.health = this.trueParentUnit.health;
            } else {
                if (this.segmentHealth > this.maxHealth) {
                    this.segmentHealth = this.maxHealth;
                }

                this.health = this.segmentHealth;
            }

            this.hitTime = this.trueParentUnit.hitTime;
            this.ammo = this.trueParentUnit.ammo;
            if (this.wormType.splittable && this.segmentHealth <= 0.0F) {
                this.split();
            }

            if (this.team != this.trueParentUnit.team) {
                this.team = this.trueParentUnit.team;
            }

            if (!Vars.net.client() && !this.dead && this.controller != null) {
                this.controller.updateUnit();
            }

            if (this.controller == null || !this.controller.isValidController()) {
                this.resetController();
            }

            this.updateWeapon();
            this.updateStatus();
        }
    }

    protected void split() {
        int index = 0;
        WormDefaultUnit hd = this.trueParentUnit;
        hd.maxHealth /= 2.0F;
        hd.health = Math.min(hd.health, hd.maxHealth);

        for(int i = 0; i < hd.segmentUnits.length; ++i) {
            if (hd.segmentUnits[i] == this) {
                index = i;
                break;
            }
        }

        if (index >= hd.segmentUnits.length - 1) {
            this.trueParentUnit.removeTail();
        }

        if (index > 0 && index < hd.segmentUnits.length - 1) {
            hd.segmentUnits[index - 1].segmentType = 1;
            WormDefaultUnit newHead = (WormDefaultUnit)this.type.create(this.team);
            hd.segmentUnits[index + 1].parentUnit = newHead;
            newHead.addSegments = false;
            newHead.attachTime = 0.0F;
            newHead.set(this);
            newHead.vel.set(this.vel);
            newHead.maxHealth /= 2.0F;
            newHead.health /= 2.0F;
            newHead.rotation = this.rotation;
            SegmentData oldSeg = new SegmentData(hd.segmentUnits.length);
            SegmentData newSeg = new SegmentData(hd.segmentUnits.length);

            for(int i = 0; i < hd.segmentUnits.length; ++i) {
                WormSegmentUnit var10000 = hd.segmentUnits[i];
                var10000.maxHealth /= 2.0F;
                hd.segmentUnits[i].clampHealth();
                if (i < index) {
                    oldSeg.add(hd, i);
                }

                if (i > index) {
                    newSeg.add(hd, i);
                }
            }

            oldSeg.set(hd);
            newSeg.set(newHead);
            newHead.add();
            this.wormType.splitSound.at(this.x, this.y, Mathf.random(0.9F, 1.1F));
            this.remove();
        }
    }

    protected void updateStatus() {
        if (this.trueParentUnit != null && !this.trueParentUnit.dead) {
            if (!this.statuses.isEmpty()) {
                this.statuses.each((s) -> this.trueParentUnit.apply(s.effect, s.time));
            }

            this.statuses.clear();
        }
    }

    protected void updateWeapon() {
        boolean can = this.canShoot();

        for(WeaponMount mount : this.mounts) {
            Weapon weapon = mount.weapon;
            mount.reload = Math.max(mount.reload - Time.delta * this.reloadMultiplier, 0.0F);
            float weaponRotation = this.rotation - 90.0F + (weapon.rotate ? mount.rotation : 0.0F);
            float mountX = this.x + Angles.trnsx(this.rotation - 90.0F, weapon.x, weapon.y);
            float mountY = this.y + Angles.trnsy(this.rotation - 90.0F, weapon.x, weapon.y);
            float shootX = mountX + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY);
            float shootY = mountY + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
            float shootAngle = weapon.rotate ? weaponRotation + 90.0F : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (this.rotation - this.angleTo(mount.aimX, mount.aimY));
            if (weapon.continuous && mount.bullet != null) {
                if (mount.bullet.isAdded() && !(mount.bullet.time >= mount.bullet.lifetime) && mount.bullet.type == weapon.bullet) {
                    mount.bullet.rotation(weaponRotation + 90.0F);
                    mount.bullet.set(shootX, shootY);
                    mount.reload = weapon.reload;
                    this.vel.add(Tmp.v1.trns(this.rotation + 180.0F, mount.bullet.type.recoil));
                    if (weapon.shootSound != Sounds.none && !Vars.headless) {
                        if (mount.sound == null) {
                            mount.sound = new SoundLoop(weapon.shootSound, 1.0F);
                        }

                        mount.sound.update(this.x, this.y, true);
                    }
                } else {
                    mount.bullet = null;
                }
            } else {
                mount.heat = Math.max(mount.heat - Time.delta * this.reloadMultiplier / mount.weapon.cooldownTime, 0.0F);
                if (mount.sound != null) {
                    mount.sound.update(this.x, this.y, false);
                }
            }

            if (weapon.otherSide != -1 && weapon.alternate && mount.side == weapon.flipSprite && mount.reload + Time.delta > weapon.reload / 2.0F && mount.reload <= weapon.reload / 2.0F) {
                this.mounts[weapon.otherSide].side = !this.mounts[weapon.otherSide].side;
                mount.side = !mount.side;
            }

            if (weapon.rotate && (mount.rotate || mount.shoot) && can) {
                float axisX = this.x + Angles.trnsx(this.rotation - 90.0F, weapon.x, weapon.y);
                float axisY = this.y + Angles.trnsy(this.rotation - 90.0F, weapon.x, weapon.y);
                mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - this.rotation;
                mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, weapon.rotateSpeed * Time.delta);
            } else if (!weapon.rotate) {
                mount.rotation = 0.0F;
                mount.targetRotation = this.angleTo(mount.aimX, mount.aimY);
            }

            if (mount.shoot && can && (this.ammo > 0.0F || !Vars.state.rules.unitAmmo || this.team().rules().infiniteAmmo) && (!weapon.alternate || mount.side == weapon.flipSprite) && (this.vel.len() >= mount.weapon.minShootVelocity || Vars.net.active() && !this.isLocal()) && mount.reload <= 1.0E-4F && Angles.within(weapon.rotate ? mount.rotation : this.rotation, mount.targetRotation, mount.weapon.shootCone)) {
                this.shoot(mount, shootX, shootY, mount.aimX, mount.aimY, mountX, mountY, shootAngle, Mathf.sign(weapon.x));
                mount.reload = weapon.reload;
                --this.ammo;
                if (this.ammo < 0.0F) {
                    this.ammo = 0.0F;
                }
            }
        }

    }

    protected void shoot(WeaponMount mount, float x, float y, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        Weapon weapon = mount.weapon;
        float baseX = this.x;
        float baseY = this.y;
        boolean delay = weapon.firstShotDelay + weapon.shotDelay > 0.0F;
        (delay ? weapon.chargeSound : (weapon.continuous ? Sounds.none : weapon.shootSound)).at(x, y, Mathf.random(weapon.soundPitchMin, weapon.soundPitchMax));
        BulletType ammo = weapon.bullet;
        float lifeScl = ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(x, y, aimX, aimY) / ammo.range()) : 1.0F;
        if (delay) {
            Angles.shotgun(weapon.shots, weapon.spacing, rotation, (f) -> Time.run(weapon.shotDelay + weapon.firstShotDelay, () -> {
                if (this.isAdded()) {
                    mount.bullet = this.bullet(weapon, x + this.x - baseX, y + this.y - baseY, f + Mathf.range(weapon.inaccuracy), lifeScl);
                }
            }));
        } else {
            Angles.shotgun(weapon.shots, weapon.spacing, rotation, (f) -> mount.bullet = this.bullet(weapon, x, y, f + Mathf.range(weapon.inaccuracy), lifeScl));
        }

        boolean parentize = ammo.keepVelocity;
        if (delay) {
            Time.run(weapon.firstShotDelay, () -> {
                if (this.isAdded()) {
                    this.vel.add(Tmp.v1.trns(rotation + 180.0F, ammo.recoil));
                    Effect.shake(weapon.shake, weapon.shake, x, y);
                    mount.heat = 1.0F;
                    if (!weapon.continuous) {
                        weapon.shootSound.at(x, y, Mathf.random(weapon.soundPitchMin, weapon.soundPitchMax));
                    }

                }
            });
        } else {
            this.vel.add(Tmp.v1.trns(rotation + 180.0F, ammo.recoil));
            Effect.shake(weapon.shake, weapon.shake, x, y);
            mount.heat = 1.0F;
        }

        weapon.ejectEffect.at(mountX, mountY, rotation * (float)side);
        ammo.shootEffect.at(x, y, rotation, parentize ? this : null);
        ammo.smokeEffect.at(x, y, rotation, parentize ? this : null);
        this.apply(weapon.shootStatus, weapon.shootStatusDuration);
    }

    protected Bullet bullet(Weapon weapon, float x, float y, float angle, float lifescl) {
        return weapon.bullet.create(this, this.team, x, y, angle, 1.0F - weapon.velocityRnd + Mathf.random(weapon.velocityRnd), lifescl);
    }

    public void drawBody() {
        float z = Draw.z();
        this.type.applyColor(this);
        TextureRegion region = this.segmentType == 0 ? this.wormType.segmentRegion : this.wormType.tailRegion;
        Draw.rect(region, this, this.rotation - 90.0F);
        TextureRegion segCellReg = this.wormType.segmentCellRegion;
        if (this.segmentType == 0 && segCellReg != Core.atlas.find("error")) {
            this.drawCell(segCellReg);
        }

        TextureRegion outline = this.wormType.segmentOutline != null && this.wormType.tailOutline != null ? (this.segmentType == 0 ? this.wormType.segmentOutline : this.wormType.tailOutline) : null;
        if (outline != null) {
            Draw.color(Color.white);
            Draw.z(Draw.z());
            Draw.rect(outline, this, this.rotation - 90.0F);
            Draw.z(z);
        }

        Draw.reset();
    }

    public void drawCell(TextureRegion cellRegion) {
        Draw.color(this.type.cellColor(this));
        Draw.rect(cellRegion, this.x, this.y, this.rotation - 90.0F);
    }

    public void drawShadow() {
        TextureRegion region = this.segmentType == 0 ? this.wormType.segmentRegion : this.wormType.tailRegion;
        Draw.color(Pal.shadow);
        float e = Math.max(this.elevation, this.type.visualElevation);
        Draw.rect(region, this.x + -12.0F * e, this.y + -13.0F * e, this.rotation - 90.0F);
        Draw.color();
    }

    public void draw() {
    }

    public void collision(Hitboxc other, float x, float y) {
        super.collision(other, x, y);
        if (this.trueParentUnit != null) {
            this.trueParentUnit.handleCollision(this, other, x, y);
        }

    }

    protected void setTrueParent(WormDefaultUnit parent) {
        this.shootSequence = 0;
        this.trueParentUnit = parent;
    }

    public void setParent(Unit parent) {
        this.parentUnit = parent;
    }

    protected static class SegmentData {
        WormSegmentUnit[] units;
        Vec2[] pos;
        Vec2[] vel;
        int size = 0;

        SegmentData(int size) {
            this.units = new WormSegmentUnit[size];
            this.pos = new Vec2[size];
            this.vel = new Vec2[size];
        }

        void add(WormSegmentUnit unit, Vec2 vel) {
            this.units[this.size] = unit;
            this.pos[this.size] = new Vec2(unit.getX(), unit.getY());
            this.vel[this.size++] = vel;
        }

        void add(WormDefaultUnit unit, int index) {
            this.units[this.size] = unit.segmentUnits[index];
            this.pos[this.size] = unit.segments[index];
            this.vel[this.size++] = unit.segmentVelocities[index];
        }

        void set(WormDefaultUnit unit) {
            for(WormSegmentUnit seg : this.units) {
                if (seg == null) {
                    break;
                }

                seg.trueParentUnit = unit;
            }

            unit.segmentUnits = new WormSegmentUnit[this.size];
            unit.segments = new Vec2[this.size];
            unit.segmentVelocities = new Vec2[this.size];
            System.arraycopy(this.units, 0, unit.segmentUnits, 0, this.size);
            System.arraycopy(this.pos, 0, unit.segments, 0, this.size);
            System.arraycopy(this.vel, 0, unit.segmentVelocities, 0, this.size);
        }
    }
}
