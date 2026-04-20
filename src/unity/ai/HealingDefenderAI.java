package unity.ai;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.ai.types.DefenderAI;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.units.UnitCommand;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.world.meta.BlockFlag;

public class HealingDefenderAI extends DefenderAI {
    public void updateTargeting() {
        if (this.unit.hasWeapons()) {
            this.updateWeapons();
        } else {
            super.updateTargeting();
        }

    }

    public void updateWeapons() {
        float rotation = this.unit.rotation - 90.0F;
        boolean ret = this.retarget();
        if (ret) {
            this.target = this.findTarget(this.unit.x, this.unit.y, this.unit.range(), true, true);
            if (this.invalid(this.target)) {
                this.target = null;
            }
        }

        this.unit.isShooting = false;

        for(WeaponMount mount : this.unit.mounts) {
            Weapon weapon = mount.weapon;
            if (weapon.controllable && !(weapon.bullet.healPercent <= 0.0F)) {
                float mountX = this.unit.x + Angles.trnsx(rotation, weapon.x, weapon.y);
                float mountY = this.unit.y + Angles.trnsy(rotation, weapon.x, weapon.y);
                if (this.unit.type.singleTarget) {
                    mount.target = this.target;
                } else if (ret) {
                    mount.target = this.findTargetAlt(mountX, mountY, weapon.bullet.range(), weapon.bullet.collidesAir, weapon.bullet.collidesGround);
                }

                if (this.checkTarget(mount.target, mountX, mountY, weapon.bullet.range())) {
                    mount.target = null;
                }

                boolean shoot = false;
                if (mount.target != null) {
                    Teamc var10000 = mount.target;
                    float var10003 = weapon.bullet.range();
                    Teamc var12 = mount.target;
                    float var10004;
                    if (var12 instanceof Sized) {
                        Sized s = (Sized)var12;
                        var10004 = s.hitSize() / 2.0F;
                    } else {
                        var10004 = 0.0F;
                    }

                    shoot = var10000.within(mountX, mountY, var10003 + var10004) && this.shouldShoot();
                    Vec2 to = Predict.intercept(this.unit, mount.target, weapon.bullet.speed);
                    mount.aimX = to.x;
                    mount.aimY = to.y;
                }

                Unit var14 = this.unit;
                var14.isShooting |= mount.shoot = mount.rotate = shoot;
                if (shoot) {
                    this.unit.aimX = mount.aimX;
                    this.unit.aimY = mount.aimY;
                }
            }
        }

    }

    public boolean checkTarget(Teamc target, float x, float y, float range) {
        boolean var10000;
        if (target != null && target.team() == this.unit.team) {
            label43: {
                if (target instanceof Healthc) {
                    Healthc h = (Healthc)target;
                    if (h.health() >= h.maxHealth() || h.dead()) {
                        break label43;
                    }
                }

                if (range != Float.MAX_VALUE) {
                    float var10004;
                    if (target instanceof Sized) {
                        Sized hb = (Sized)target;
                        var10004 = hb.hitSize() / 2.0F;
                    } else {
                        var10004 = 0.0F;
                    }

                    if (!target.within(x, y, range + var10004)) {
                        break label43;
                    }
                }

                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    public boolean invalid(Teamc target) {
        boolean var10000;
        if (target != null && target.team() == this.unit.team) {
            label30: {
                if (target instanceof Healthc) {
                    Healthc h = (Healthc)target;
                    if (h.dead()) {
                        break label30;
                    }
                }

                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    Teamc findTargetAlt(float x, float y, float range, boolean air, boolean ground) {
        Building blockResult = ground ? Units.findDamagedTile(this.unit.team, this.unit.x, this.unit.y) : null;
        Unit unitResult = Units.closest(this.unit.team, x, y, Math.max(range, 400.0F), (u) -> !u.dead() && u.damaged() && u.checkTarget(air, ground) && u.type != this.unit.type, (u, tx, ty) -> -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400.0F);
        Teamc trueResult;
        if (unitResult != null && (blockResult == null || !(unitResult.dst2(this.unit) / 6400.0F + unitResult.health > blockResult.dst2(this.unit) / 6400.0F + blockResult.health))) {
            trueResult = unitResult;
        } else {
            trueResult = blockResult;
        }

        return trueResult;
    }

    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        if (this.command() != UnitCommand.rally) {
            Building blockResult = Units.findDamagedTile(this.unit.team, this.unit.x, this.unit.y);
            Unit result = Units.closest(this.unit.team, x, y, Math.max(range, 400.0F), (u) -> !u.dead() && u.type != this.unit.type, (u, tx, ty) -> -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400.0F);
            Teamc trueResult;
            if (result != null && (blockResult == null || !(result.dst2(this.unit) / 6400.0F + result.health > blockResult.dst2(this.unit) / 6400.0F + blockResult.health))) {
                trueResult = result;
            } else {
                trueResult = blockResult;
            }

            if (trueResult != null) {
                return trueResult;
            }
        }

        Teamc block = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);
        return (Teamc)(block != null ? block : this.unit.closestCore());
    }
}
