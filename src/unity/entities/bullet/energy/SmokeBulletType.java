package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import unity.content.UnityFx;

public class SmokeBulletType extends BasicBulletType {
    public float baseSize;
    public float growAmount;
    public float trailRand;
    public float smokeRand;

    public SmokeBulletType(float speed, float damage) {
        super(speed, damage);
        this.baseSize = 3.0F;
        this.growAmount = 4.1F;
        this.trailRand = 0.6F;
        this.smokeRand = 1.7F;
    }

    public SmokeBulletType() {
        this(1.0F, 1.0F);
    }

    public void update(Bullet b) {
        super.update(b);
        if (b.timer.get(0, 1.0F)) {
            UnityFx.advanceFlameTrail.at(b.x + Mathf.range(this.trailRand), b.y + Mathf.range(this.trailRand), b.rotation());
        }

        if (Mathf.chanceDelta((double)0.7F)) {
            UnityFx.advanceFlameSmoke.at(b.x + Mathf.range(this.smokeRand), b.y + Mathf.range(this.smokeRand), b.rotation());
        }

    }

    public void draw(Bullet b) {
        Draw.color(Pal.lancerLaser, Color.valueOf("4f72e1"), b.fin());
        Fill.poly(b.x, b.y, 6, this.baseSize + b.fin() * this.growAmount, b.rotation() + b.fin() * 270.0F);
        Draw.reset();
    }
}
