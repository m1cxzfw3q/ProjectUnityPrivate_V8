package unity.entities.bullet.kami;

import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Velc;
import unity.entities.CircleCollisionBullet;

public class CircleBulletType extends BulletType {
    public Func<Bullet, Color> color = (b) -> Color.red;

    public CircleBulletType(float speed, float damage) {
        super(speed, damage);
        this.collidesTiles = false;
    }

    public void update(Bullet b) {
        super.update(b);
    }

    public void draw(Bullet b) {
        float z = Draw.z();
        Draw.z(b.hitSize > 50.0F ? z - 0.001F : z);
        Draw.color((Color)this.color.get(b));
        Fill.circle(b.x, b.y, b.hitSize + 1.5F);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, b.hitSize);
        Draw.z(z);
    }

    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        CircleCollisionBullet bullet = CircleCollisionBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0.0F;
        bullet.vel.trns(angle, this.speed * velocityScl);
        if (this.backMove) {
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        } else {
            bullet.set(x, y);
        }

        bullet.lifetime = this.lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = this.drag;
        bullet.hitSize = this.hitSize;
        bullet.damage = (damage < 0.0F ? this.damage : damage) * bullet.damageMultiplier();
        bullet.add();
        if (this.keepVelocity && owner instanceof Velc) {
            Velc v = (Velc)owner;
            bullet.vel.add(v.vel());
        }

        return bullet;
    }
}
