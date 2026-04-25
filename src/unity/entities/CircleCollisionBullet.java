package unity.entities;

import arc.math.geom.Rect;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;

public class CircleCollisionBullet extends Bullet {
    float resetTime = 0.0F;

    public void update() {
        super.update();
        if (!this.collided.isEmpty()) {
            this.resetTime += Time.delta;
            if (this.resetTime >= 60.0F) {
                this.collided.clear();
                this.resetTime = 0.0F;
            }
        }

    }

    public boolean collides(Hitboxc other) {
        return super.collides(other) && this.within(other, other.hitSize() / 2.0F + this.hitSize());
    }

    public void hitbox(Rect rect) {
        rect.setCentered(this.x, this.y, this.hitSize * 2.0F);
    }

    public float clipSize() {
        return this.hitSize() * 2.0F;
    }

    public static CircleCollisionBullet create() {
        return (CircleCollisionBullet)Pools.obtain(CircleCollisionBullet.class, CircleCollisionBullet::new);
    }

    public void reset() {
        super.reset();
        this.resetTime = 0.0F;
    }
}
