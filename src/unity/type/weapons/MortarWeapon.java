package unity.type.weapons;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class MortarWeapon extends Weapon {
    public float inclineOffset = 5.0F;
    public float maxIncline = 85.0F;
    public float barrelSpeed = 5.0F;
    public float barrelOffset = 0.0F;
    public TextureRegion barrelRegion;
    public TextureRegion barrelEndRegion;
    public TextureRegion barrelEndHeatRegion;

    public MortarWeapon(String name) {
        super(name);
        this.mountType = MortarMount::new;
    }

    public void load() {
        super.load();
        this.barrelRegion = Core.atlas.find(this.name + "-barrel");
        this.barrelEndRegion = Core.atlas.find(this.name + "-barrel-end");
        this.barrelEndHeatRegion = Core.atlas.find(this.name + "-barrel-end-heat");
    }

    public void draw(Unit unit, WeaponMount mount) {
        super.draw(unit, mount);
        float z = Draw.z();
        Draw.z(z + this.layerOffset);
        MortarMount mMount = (MortarMount)mount;
        float incline = -Mathf.sinDeg(Mathf.lerp(this.inclineOffset, this.maxIncline, mMount.incline)) * (float)this.barrelRegion.width * Draw.scl;
        float endIncline = -Mathf.cosDeg(Mathf.lerp(this.inclineOffset, this.maxIncline, mMount.incline));
        float rotation = unit.rotation - 90.0F;
        float weaponRotation = rotation + (this.rotate ? mount.rotation : 0.0F);
        float recoil = -(mount.reload / this.reload * this.recoil);
        float wx = unit.x + Angles.trnsx(rotation, this.x, this.y) + Angles.trnsx(weaponRotation, 0.0F, recoil);
        float wy = unit.y + Angles.trnsy(rotation, this.x, this.y) + Angles.trnsy(weaponRotation, 0.0F, recoil);
        Tmp.v1.trns(weaponRotation - 90.0F, incline + this.barrelOffset).add(wx, wy);
        Tmp.v2.trns(weaponRotation - 90.0F, this.barrelOffset).add(wx, wy);
        Lines.stroke((float)this.barrelRegion.width * Draw.scl * 0.5F);
        Lines.line(this.barrelRegion, Tmp.v2.x, Tmp.v2.y, Tmp.v1.x, Tmp.v1.y, false);
        if (this.heatRegion.found() && mount.heat > 0.0F) {
            Draw.color(this.heatColor, mount.heat);
            Draw.blend(Blending.additive);
            Lines.stroke((float)this.heatRegion.width * Draw.scl * 0.5F);
            Lines.line(this.heatRegion, Tmp.v2.x, Tmp.v2.y, Tmp.v1.x, Tmp.v1.y, false);
            Draw.blend();
            Draw.color();
        }

        Draw.rect(this.barrelEndRegion, Tmp.v1, (float)this.barrelEndRegion.width * Draw.scl, (float)this.barrelEndRegion.height * endIncline * Draw.scl, weaponRotation);
        if (this.barrelEndHeatRegion.found() && mount.heat > 0.0F) {
            Draw.color(this.heatColor, mount.heat);
            Draw.blend(Blending.additive);
            Draw.rect(this.barrelEndHeatRegion, Tmp.v1, (float)this.barrelEndHeatRegion.width * Draw.scl, (float)this.barrelEndHeatRegion.height * endIncline * Draw.scl, weaponRotation);
            Draw.blend();
            Draw.color();
        }

        Draw.z(z);
    }

    public void update(Unit unit, WeaponMount mount) {
        MortarMount mMount = (MortarMount)mount;
        float r = this.bullet.range();
        mMount.incline = Mathf.approachDelta(mMount.incline, Mathf.clamp(unit.dst(mount.aimX, mount.aimY) / r), this.barrelSpeed / r);
        super.update(unit, mount);
    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        MortarMount mMount = (MortarMount)mount;
        float incline = Mathf.sinDeg(Mathf.lerp(this.inclineOffset, this.maxIncline, mMount.incline)) * this.shootY;
        float weaponRotation = unit.rotation - 90.0F + (this.rotate ? mount.rotation : 0.0F);
        float mX = unit.x + Angles.trnsx(unit.rotation - 90.0F, this.x, this.y);
        float mY = unit.y + Angles.trnsy(unit.rotation - 90.0F, this.x, this.y);
        Tmp.v1.trns(weaponRotation - 90.0F, -incline + this.barrelOffset);
        shootX = mX + Tmp.v1.x;
        shootY = mY + Tmp.v1.y;
        super.shoot(unit, mount, shootX, shootY, aimX, aimY, mountX, mountY, rotation, side);
    }

    protected Bullet bullet(Unit unit, float shootX, float shootY, float angle, float lifescl) {
        float xr = Mathf.range(this.xRand);
        Bullet b = this.bullet.create(unit, unit.team, shootX + Angles.trnsx(angle, 0.0F, xr), shootY + Angles.trnsy(angle, 0.0F, xr), angle, lifescl - this.velocityRnd + Mathf.random(this.velocityRnd), 1.0F);
        b.fdata = 1.0F - lifescl;
        return b;
    }

    static class MortarMount extends WeaponMount {
        float incline = 0.0F;

        MortarMount(Weapon weapon) {
            super(weapon);
        }
    }
}
