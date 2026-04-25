package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Bullet;
import unity.content.effects.HitFx;
import unity.util.Utils;

public class VoidPelletBulletType extends AntiCheatBulletTypeBase {
    public VoidPelletBulletType(float speed, float damage) {
        super(speed, damage);
        this.lifetime = 90.0F;
        this.trailColor = Color.black;
        this.trailLength = 16;
        this.trailWidth = 2.0F;
        this.despawnEffect = this.hitEffect = HitFx.voidHit;
        this.homingPower = 0.01F;
        this.homingRange = 50.0F;
        this.homingDelay = 20.0F;
        this.hitSize = 3.0F;
        this.keepVelocity = false;
    }

    public void init(Bullet b) {
        super.init(b);
        b.fdata = b.rotation();
        b.rotation(b.rotation() + Mathf.range(120.0F));
    }

    public void update(Bullet b) {
        super.update(b);
        if (b.fdata != -361.0F) {
            float ang = Utils.angleDistSigned(b.rotation(), b.fdata);
            b.vel.rotate(-ang * Mathf.clamp(0.2F * Time.delta));
            if (Math.abs(ang) <= 0.06F) {
                b.fdata = -361.0F;
            }
        }

    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        Draw.color(Color.black);
        Fill.square(b.x, b.y, 2.0F, b.rotation() + 45.0F);
    }

    public void drawLight(Bullet b) {
    }
}
