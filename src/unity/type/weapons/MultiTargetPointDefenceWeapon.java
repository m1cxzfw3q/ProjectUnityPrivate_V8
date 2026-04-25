package unity.type.weapons;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import unity.util.Utils;

public class MultiTargetPointDefenceWeapon extends Weapon {
    private static float f;
    private static final Seq<Bullet> tmp = new Seq(Bullet.class);
    public static boolean testing = false;
    public Effect beamEffect;
    public Effect absorbEffect;
    public Color color;
    public boolean absorb;
    public float splitCone;
    public float shootConeAlt;
    public float decideTime;

    public MultiTargetPointDefenceWeapon(String name) {
        super(name);
        this.beamEffect = Fx.pointBeam;
        this.absorbEffect = Fx.absorb;
        this.color = Color.white;
        this.absorb = true;
        this.splitCone = 35.0F;
        this.shootConeAlt = 5.0F;
        this.decideTime = 20.0F;
        this.mountType = MultiTargetPointDefenceMount::new;
        this.predictTarget = false;
        this.controllable = false;
        this.autoTarget = true;
        this.rotate = true;
        this.useAmmo = false;
        this.shots = 5;
    }

    public void draw(Unit unit, WeaponMount mount) {
        super.draw(unit, mount);
        if (testing) {
            MultiTargetPointDefenceMount cm = (MultiTargetPointDefenceMount)mount;
            float mountX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
            float mountY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);

            for(int i = 0; i < cm.available.size; i += 2) {
                float h = (float)i / 2.0F * (360.0F / ((float)cm.available.size / 2.0F));
                float len = 30.0F + cm.available.items[i];
                float ang = cm.available.items[i + 1];
                Draw.color(Tmp.c1.set(Color.green).shiftHue(h));
                Lines.stroke(2.0F);
                Lines.lineAngle(mountX, mountY, ang, len, false);
                Draw.alpha(0.3F);
                Lines.lineAngle(mountX, mountY, ang - this.splitCone, this.bullet.range(), false);
                Lines.lineAngle(mountX, mountY, ang + this.splitCone, this.bullet.range(), false);
            }

            Draw.reset();
        }

    }

    public void update(Unit unit, WeaponMount mount) {
        MultiTargetPointDefenceMount cm = (MultiTargetPointDefenceMount)mount;
        boolean can = unit.canShoot();
        boolean ret = (mount.retarget += Time.delta) >= 5.0F;
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0.0F);
        float weaponRotation = unit.rotation - 90.0F + (this.rotate ? mount.rotation : 0.0F);
        float mountX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
        float mountY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
        float bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
        float bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
        float bulletRotation = weaponRotation + 90.0F;
        if (this.otherSide != -1 && this.alternate && mount.side == this.flipSprite && mount.reload + Time.delta * unit.reloadMultiplier > this.reload / 2.0F && mount.reload <= this.reload / 2.0F) {
            unit.mounts[this.otherSide].side = !unit.mounts[this.otherSide].side;
            mount.side = !mount.side;
        }

        if (ret) {
            mount.retarget = 0.0F;
            cm.targets.clear();
            Rect r = Tmp.r1.setCentered(mountX, mountY, this.bullet.range() * 2.0F);
            Groups.bullet.intersect(r.x, r.y, r.x + r.width, r.y + r.height, (b) -> {
                if (b.team != unit.team && b.type.hittable && b.within(mountX, mountY, this.bullet.range() + b.hitSize / 2.0F)) {
                    cm.targets.add(b);
                }

            });
            cm.targets.sort((b) -> Utils.angleDistSigned(bulletRotation, b.angleTo(mountX, mountY) + 180.0F));
        }

        cm.available.clear();
        if (!cm.targets.isEmpty()) {
            tmp.clear();
            f = -360.0F;
            cm.targets.removeAll((b) -> {
                boolean invalid = !b.isAdded() || Units.invalidateTarget(b, unit.team, mountX, mountY, this.bullet.range());
                if (!invalid) {
                    tmp.add(b);
                    float ang = Angles.angle(mountX, mountY, b.x, b.y);
                    if (f != -360.0F && Utils.angleDist(f, ang) > this.splitCone) {
                        this.updateScore(cm, bulletRotation, mountX, mountY);
                    }

                    f = ang;
                }

                return invalid;
            });
            this.updateScore(cm, bulletRotation, mountX, mountY);
            boolean changed = cm.targetIdx + 1 >= cm.available.size;
            if (cm.decideTime <= 0.0F || changed) {
                float ls = -1.0F;

                for(int i = 0; i < cm.available.size; i += 2) {
                    float s = cm.available.items[i];
                    if (s > ls) {
                        cm.targetIdx = i;
                        ls = s;
                    }
                }

                if (cm.decideTime <= 0.0F) {
                    cm.decideTime = this.decideTime;
                }
            }
        }

        cm.decideTime = Math.max(cm.decideTime - Time.delta, 0.0F);
        if ((mount.rotate = mount.shoot = !cm.targets.isEmpty()) && can) {
            mount.targetRotation = cm.available.get(cm.targetIdx + 1) - unit.rotation;
            mount.aimX = Angles.trnsx(mount.targetRotation + unit.rotation, this.bullet.range());
            mount.aimY = Angles.trnsy(mount.targetRotation + unit.rotation, this.bullet.range());
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, this.rotateSpeed * Time.delta);
        }

        if (mount.shoot && can && (!this.useAmmo || unit.ammo > 0.0F || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && (!this.alternate || mount.side == this.flipSprite) && mount.reload <= 1.0E-4F && Angles.within(mount.rotation, mount.targetRotation, this.shootConeAlt)) {
            this.shoot(unit, mount, bulletX, bulletY, mount.aimX, mount.aimY, mountX, mountY, bulletRotation, Mathf.sign(this.x));
            mount.reload = this.reload;
            if (this.useAmmo) {
                --unit.ammo;
                if (unit.ammo < 0.0F) {
                    unit.ammo = 0.0F;
                }
            }
        }

    }

    void updateScore(MultiTargetPointDefenceMount mount, float rotation, float mX, float mY) {
        float x = 0.0F;
        float y = 0.0F;

        for(Bullet b : tmp) {
            x += (b.x - mX) / (float)tmp.size;
            y += (b.y - mY) / (float)tmp.size;
        }

        float angle = Angles.angle(x, y);
        float score = tmp.sumf((bx) -> bx.damage) * Mathf.clamp(1.0F - Utils.angleDist(rotation, angle) / 180.0F) * Mathf.clamp(1.0F - Mathf.dst(x, y) / this.bullet.range());
        tmp.clear();
        mount.available.add(score, angle);
    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        MultiTargetPointDefenceMount cm = (MultiTargetPointDefenceMount)mount;
        cm.decideTime = 0.0F;
        float range = this.bullet.range();
        Rect r = Tmp.r1.setCentered(mountX, mountY, 0.0F);
        Utils.shotgunRange(3, this.shootCone, rotation, (a) -> r.merge(Angles.trnsx(a, range) + mountX, Angles.trnsy(a, range) + mountY));
        tmp.clear();
        Groups.bullet.intersect(r.x, r.y, r.width + r.x, r.height + r.y, (bx) -> {
            if (bx.team != unit.team && bx.type.hittable && bx.within(mountX, mountY, range + bx.hitSize / 2.0F) && Angles.within(Angles.angle(mountX, mountY, bx.x, bx.y), rotation, this.shootCone)) {
                tmp.add(bx);
            }

        });
        if (!tmp.isEmpty()) {
            tmp.sort((bx) -> bx.dst2(mountX, mountY));

            for(int i = 0; i < Math.min(tmp.size, this.shots); ++i) {
                Bullet b = (Bullet)tmp.get(i);
                if (b != null) {
                    this.beamEffect.at(shootX, shootY, rotation, this.color, (new Vec2()).set(b));
                    if (b.damage > this.bullet.damage) {
                        this.bullet.hitEffect.at(b.x, b.y);
                        b.damage -= this.bullet.damage;
                    } else {
                        if (this.absorb) {
                            b.absorbed = true;
                            this.absorbEffect.at(b.x, b.y, this.color);
                        } else {
                            this.bullet.hitEffect.at(b.x, b.y);
                        }

                        b.remove();
                    }
                }
            }

            this.bullet.shootEffect.at(shootX, shootY, rotation);
            this.shootSound.at(shootX, shootY, Mathf.random(0.9F, 1.1F));
        }

    }

    public static class MultiTargetPointDefenceMount extends WeaponMount {
        FloatSeq available = new FloatSeq();
        Seq<Bullet> targets = new Seq();
        float decideTime = 0.0F;
        int targetIdx = 0;

        MultiTargetPointDefenceMount(Weapon weapon) {
            super(weapon);
        }
    }
}
