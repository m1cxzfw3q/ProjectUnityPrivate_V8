package unity.entities.bullet.physical;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;

public class GuidedMissileBulletType extends MissileBulletType {
    public float threshold = 1.0F;
    public float targetingInaccuracy = 17.0F;

    public GuidedMissileBulletType(float speed, float damage) {
        super(speed, damage);
    }

    public void update(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof WeaponMount) {
            WeaponMount mount = (WeaponMount)var3;
            if (this.homingPower > 0.0F) {
                if (this.targetingInaccuracy > 0.001F) {
                    Tmp.v1.trns(Mathf.randomSeed((long)b.id, 360.0F), Mathf.randomSeed(((long)b.id << 2) + 351L, this.targetingInaccuracy));
                } else {
                    Tmp.v1.setZero();
                }

                float ang = b.angleTo(mount.aimX + Tmp.v1.x, mount.aimY + Tmp.v1.y);
                b.rotation(Angles.moveToward(b.rotation(), ang, this.homingPower * Time.delta * 50.0F));
                if (Angles.within(b.rotation(), ang, this.threshold)) {
                    b.data = null;
                }
            }
        }

        super.update(b);
    }
}
