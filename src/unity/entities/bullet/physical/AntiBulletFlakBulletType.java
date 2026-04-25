package unity.entities.bullet.physical;

import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.util.Tmp;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;

public class AntiBulletFlakBulletType extends FlakBulletType {
    public float bulletDamage = 5.0F;
    public float bulletSlowDownScl = 0.5F;
    public float bulletRadius = 40.0F;
    public Interp interp;

    public AntiBulletFlakBulletType(float speed, float damage) {
        super(speed, damage);
        this.interp = Interp.pow3;
        this.collidesGround = true;
        this.despawnHit = true;
        this.shrinkY = 0.2F;
    }

    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        Rect r1 = Tmp.r1.setSize(this.bulletRadius * 2.0F).setCenter(b.x, b.y);
        Groups.bullet.intersect(r1.x, r1.y, r1.width, r1.height, (bl) -> {
            if (b.team != bl.team && bl.type.hittable && b.within(bl, this.bulletRadius)) {
                float in = this.interp.apply(Mathf.clamp((this.bulletRadius - b.dst(bl)) / this.bulletRadius));
                bl.vel.scl(Mathf.lerp(1.0F, this.bulletSlowDownScl, in));
                bl.damage -= this.bulletDamage * in;
                if (bl.damage <= 0.0F) {
                    bl.remove();
                }
            }

        });
    }
}
