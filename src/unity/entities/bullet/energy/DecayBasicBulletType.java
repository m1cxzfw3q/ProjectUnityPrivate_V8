package unity.entities.bullet.energy;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class DecayBasicBulletType extends BasicBulletType {
    public float backMinRadius = 3.0F;
    public float frontMinRadius = 1.75F;
    public float backRadius = 6.0F;
    public float frontRadius = 5.75F;
    public float minInterval = 0.75F;
    public float maxInterval = 1.75F;
    public float decayMinVel = 0.9F;
    public float decayMaxVel = 1.1F;
    public float decayMinLife = 0.3F;
    public float decayMaxLife = 1.3F;
    public Effect decayEffect;
    public BulletType decayBullet;

    public DecayBasicBulletType(float speed, float damage) {
        super(speed, damage);
        this.decayEffect = Fx.none;
    }

    public float estimateDPS() {
        float total = this.decayBullet.estimateDPS() * (this.lifetime / Math.max(1.0F, (this.minInterval + this.maxInterval) / 2.0F));
        return super.estimateDPS() + total / 3.0F;
    }

    public void draw(Bullet b) {
        Draw.color(this.backColor);
        Fill.circle(b.x, b.y, this.backMinRadius + b.fout() * this.backRadius);
        Draw.color(this.frontColor);
        Fill.circle(b.x, b.y, this.frontMinRadius + b.fout() * this.frontRadius);
    }

    public void update(Bullet b) {
        super.update(b);
        if (b.timer(1, Mathf.lerp(this.maxInterval, this.minInterval, b.fin()))) {
            this.decayEffect.at(b.x, b.y, b.rotation() + 180.0F, this.trailColor);
            this.decayBullet.create(b, b.team, b.x, b.y, b.rotation() + Mathf.range(180.0F), Mathf.random(this.decayMinVel, this.decayMaxVel), Mathf.lerp(this.decayMaxLife, this.decayMinLife, b.fin()));
        }

    }
}
