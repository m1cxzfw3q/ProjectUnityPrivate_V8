package unity.entities.bullet.energy;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import unity.content.effects.HitFx;

public class EphemeronPairBulletType extends BasicBulletType {
    public boolean positive;

    public EphemeronPairBulletType(float damage) {
        super(0.001F, damage);
        this.lifetime = 360.0F;
        this.hitEffect = Fx.hitLancer;
        this.despawnEffect = Fx.none;
        this.hitSound = Sounds.spark;
        this.hitSize = 8.0F;
        this.drag = 0.015F;
        this.pierce = true;
        this.hittable = this.absorbable = this.reflectable = this.collidesTiles = false;
    }

    public void draw(Bullet b) {
        Draw.color(this.frontColor);
        Fill.circle(b.x, b.y, 4.0F + b.fout() * 1.5F);
        Draw.color(this.backColor);
        Fill.circle(b.x, b.y, 2.5F + b.fout());
    }

    public void update(Bullet b) {
        super.update(b);
        Object var3 = b.data;
        if (var3 instanceof Bullet) {
            Bullet n = (Bullet)var3;
            if (n.added) {
                float dst = this.hitSize / Math.max(b.dst(n) / 2.0F, this.hitSize);
                Tmp.v1.set(n).sub(b).nor().scl(dst);
                b.vel.add(Tmp.v1);
                if (!this.positive) {
                    return;
                }

                b.hitbox(Tmp.r1);
                n.hitbox(Tmp.r2);
                if (Tmp.r1.overlaps(Tmp.r2)) {
                    b.remove();
                    n.remove();
                    Tmp.v1.set((b.x + n.x) / 2.0F, (b.y + n.y) / 2.0F);
                    HitFx.lightHitLarge.at(Tmp.v1);
                    Damage.damage(b.team, Tmp.v1.x, Tmp.v1.y, 40.0F, 80.0F);
                }

                return;
            }
        }

        b.absorb();
    }
}
