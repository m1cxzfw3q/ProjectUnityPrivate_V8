package unity.type.weapons;

import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import unity.util.Utils;

public class PointDefenceMultiBarrelWeapon extends MultiBarrelWeapon {
    static WeaponMount tmp;

    public PointDefenceMultiBarrelWeapon(String name) {
        super(name);
    }

    public void update(Unit unit, WeaponMount mount) {
        tmp = mount;
        super.update(unit, mount);
    }

    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        return Utils.nearestBullet(x, y, range, (b) -> b.team != unit.team && b.type.hittable && b.vel.len2() < 25.0F);
    }

    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
        boolean var10000;
        label29: {
            if (target instanceof Bullet) {
                Bullet b = (Bullet)target;
                if (b.hitSize <= 0.0F || b.type == null) {
                    var10000 = true;
                    break label29;
                }
            }

            var10000 = false;
        }

        boolean bullet = var10000;
        if (bullet) {
            tmp.retarget = 5.0F;
        }

        return super.checkTarget(unit, target, x, y, range) || bullet;
    }
}
