package unity.type.weapons;

import arc.Core;
import arc.func.Intc;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.Weapon;

public class MultiBarrelWeapon extends Weapon {
    public int barrels = 2;
    public float barrelSpacing = 6.0F;
    public float barrelOffset = 0.0F;
    public float barrelRecoil = 0.0F;
    public boolean mirrorBarrels = false;
    public TextureRegion barrelRegion;
    public TextureRegion barrelOutlineRegion;
    private static final Vec2 tv = new Vec2();

    public MultiBarrelWeapon(String name) {
        super(name);
        this.mountType = MultiBarrelMount::new;
    }

    public void load() {
        super.load();
        this.barrelRegion = Core.atlas.find(this.name + "-barrel");
        this.barrelOutlineRegion = Core.atlas.find(this.name + "-barrel-outline");
    }

    public void update(Unit unit, WeaponMount mount) {
        super.update(unit, mount);
        MultiBarrelMount mMount = (MultiBarrelMount)mount;

        for(int i = 0; i < mMount.recoils.length; ++i) {
            mMount.recoils[i] = Math.max(0.0F, mMount.recoils[i] - this.barrelRecoil / this.reload / (float)this.barrels * Time.delta);
        }

    }

    public void drawOutline(Unit unit, WeaponMount mount) {
        if (this.barrelOutlineRegion.found()) {
            MultiBarrelMount mMount = (MultiBarrelMount)mount;
            float rotation = unit.rotation - 90.0F;
            float weaponRotation = rotation + (this.rotate ? mount.rotation : 0.0F);
            float recoil = -(mount.reload / this.reload * this.recoil);
            float wx = unit.x + Angles.trnsx(rotation, this.x, this.y) + Angles.trnsx(weaponRotation, 0.0F, recoil);
            float wy = unit.y + Angles.trnsy(rotation, this.x, this.y) + Angles.trnsy(weaponRotation, 0.0F, recoil);
            int barrels = mMount.recoils.length;
            Intc drawBarrel = (ix) -> {
                float offset = (float)ix * this.barrelSpacing - (float)(barrels - 1) * this.barrelSpacing / 2.0F;
                int s = Mathf.sign((!this.mirrorBarrels || offset < 0.0F) != this.flipSprite);
                tv.trns(weaponRotation - 90.0F, this.barrelOffset + -mMount.recoils[ix], offset).add(wx, wy);
                Draw.rect(this.barrelOutlineRegion, tv.x, tv.y, (float)this.barrelOutlineRegion.width * Draw.scl * (float)(-Mathf.sign(this.flipSprite)) * (float)s, (float)this.barrelOutlineRegion.height * Draw.scl, weaponRotation);
            };
            if (!this.flipSprite) {
                for(int i = 0; i < barrels; ++i) {
                    drawBarrel.get(i);
                }
            } else {
                for(int i = barrels - 1; i >= 0; --i) {
                    drawBarrel.get(i);
                }
            }
        }

        super.drawOutline(unit, mount);
    }

    public void draw(Unit unit, WeaponMount mount) {
        float z = Draw.z();
        Draw.z(z + this.layerOffset);
        MultiBarrelMount mMount = (MultiBarrelMount)mount;
        float rotation = unit.rotation - 90.0F;
        float weaponRotation = rotation + (this.rotate ? mount.rotation : 0.0F);
        float recoil = -(mount.reload / this.reload * this.recoil);
        float wx = unit.x + Angles.trnsx(rotation, this.x, this.y) + Angles.trnsx(weaponRotation, 0.0F, recoil);
        float wy = unit.y + Angles.trnsy(rotation, this.x, this.y) + Angles.trnsy(weaponRotation, 0.0F, recoil);
        int barrels = mMount.recoils.length;
        if (this.shadow > 0.0F) {
            Drawf.shadow(wx, wy, this.shadow);
        }

        if (this.outlineRegion.found() && this.top) {
            Draw.rect(this.outlineRegion, wx, wy, (float)this.outlineRegion.width * Draw.scl * (float)(-Mathf.sign(this.flipSprite)), (float)this.outlineRegion.height * Draw.scl, weaponRotation);
        }

        Intc drawBarrel = (ix) -> {
            float offset = (float)ix * this.barrelSpacing - (float)(barrels - 1) * this.barrelSpacing / 2.0F;
            int s = Mathf.sign((!this.mirrorBarrels || offset < 0.0F) != this.flipSprite);
            tv.trns(weaponRotation + 90.0F, this.barrelOffset + -mMount.recoils[ix], offset).add(wx, wy);
            if (this.top && this.barrelOutlineRegion.found()) {
                Draw.rect(this.barrelOutlineRegion, tv.x, tv.y, (float)this.barrelOutlineRegion.width * Draw.scl * (float)(-Mathf.sign(this.flipSprite)) * (float)s, (float)this.barrelOutlineRegion.height * Draw.scl, weaponRotation);
            } else {
                Draw.rect(this.barrelRegion, tv.x, tv.y, (float)this.barrelRegion.width * Draw.scl * (float)(-Mathf.sign(this.flipSprite)) * (float)s, (float)this.barrelRegion.height * Draw.scl, weaponRotation);
            }

            if (this.heatRegion.found() && mount.heat > 0.0F) {
                Draw.color(this.heatColor, mount.heat);
                Draw.blend(Blending.additive);
                Draw.rect(this.heatRegion, tv.x, tv.y, (float)this.heatRegion.width * Draw.scl * (float)(-Mathf.sign(this.flipSprite)) * (float)s, (float)this.heatRegion.height * Draw.scl, weaponRotation);
                Draw.blend();
                Draw.color();
            }

        };
        if (!this.flipSprite) {
            for(int i = 0; i < barrels; ++i) {
                drawBarrel.get(i);
            }
        } else {
            for(int i = barrels - 1; i >= 0; --i) {
                drawBarrel.get(i);
            }
        }

        Draw.rect(this.region, wx, wy, (float)this.region.width * Draw.scl * (float)(-Mathf.sign(this.flipSprite)), (float)this.region.height * Draw.scl, weaponRotation);
        Draw.z(z);
    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        MultiBarrelMount mMount = (MultiBarrelMount)mount;
        float offset = (float)Mathf.mod(mMount.inUse, mMount.recoils.length) * this.barrelSpacing - (float)(mMount.recoils.length - 1) * this.barrelSpacing / 2.0F;
        tv.trns(rotation, 0.0F, offset);
        shootX += tv.x;
        shootY += tv.y;
        super.shoot(unit, mount, shootX, shootY, aimX, aimY, mountX, mountY, rotation, side);
        mMount.recoils[Mathf.mod(mMount.inUse, mMount.recoils.length)] = this.barrelRecoil;
        mMount.inUse += Mathf.sign(this.flipSprite);
        mMount.inUse %= mMount.recoils.length;
    }

    public static class MultiBarrelMount extends WeaponMount {
        int inUse = 0;
        float[] recoils;

        public MultiBarrelMount(Weapon weapon) {
            super(weapon);
            this.recoils = new float[((MultiBarrelWeapon)weapon).barrels];
            if (weapon.flipSprite) {
                this.inUse = this.recoils.length - 1;
            }

        }
    }
}
