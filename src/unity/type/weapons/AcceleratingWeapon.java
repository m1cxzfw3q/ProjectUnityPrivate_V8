package unity.type.weapons;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class AcceleratingWeapon extends Weapon {
    public float accelCooldownTime = 120.0F;
    public float accelCooldownWaitTime = 60.0F;
    public float accelPerShot = 1.0F;
    public float minReload = 5.0F;

    public AcceleratingWeapon(String name) {
        super(name);
        this.mountType = AcceleratingMount::new;
    }

    public void update(Unit unit, WeaponMount mount) {
        AcceleratingMount aMount = (AcceleratingMount)mount;
        float r = aMount.accel / this.reload * unit.reloadMultiplier * Time.delta * (this.reload - this.minReload);
        if (this.alternate && this.otherSide != -1) {
            WeaponMount other = unit.mounts[this.otherSide];
            other.reload -= r / 2.0F;
            mount.reload -= r / 2.0F;
            if (other instanceof AcceleratingMount) {
                AcceleratingMount aM = (AcceleratingMount)other;
                float accel = unit.isShooting() && unit.canShoot() ? Math.max(aM.accel, aMount.accel) : Math.min(aM.accel, aMount.accel);
                float wTime = unit.isShooting() && unit.canShoot() ? Math.max(aM.waitTime, aMount.waitTime) : Math.min(aM.waitTime, aMount.waitTime);
                aM.accel = accel;
                aM.waitTime = wTime;
                aMount.accel = accel;
                aMount.waitTime = wTime;
            }
        } else {
            mount.reload -= r;
        }

        if (aMount.waitTime <= 0.0F) {
            aMount.accel = Math.max(0.0F, aMount.accel - this.minReload / this.accelCooldownTime * Time.delta);
        } else {
            aMount.waitTime -= Time.delta;
        }

        super.update(unit, mount);
    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
        AcceleratingMount aMount = (AcceleratingMount)mount;
        aMount.accel = Mathf.clamp(aMount.accel + this.accelPerShot, 0.0F, this.minReload);
        aMount.waitTime = this.accelCooldownWaitTime;
        super.shoot(unit, mount, shootX, shootY, aimX, aimY, mountX, mountY, rotation, side);
    }

    public static class AcceleratingMount extends WeaponMount {
        float accel = 0.0F;
        float waitTime = 0.0F;

        AcceleratingMount(Weapon weapon) {
            super(weapon);
        }
    }
}
