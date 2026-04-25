package unity.entities.bullet.anticheat;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Velc;
import mindustry.graphics.Drawf;
import unity.content.effects.HitFx;
import unity.entities.bullet.anticheat.modules.AntiCheatBulletModule;
import unity.entities.bullet.anticheat.modules.ArmorDamageModule;
import unity.gen.TimeStopBullet;
import unity.graphics.UnityPal;
import unity.mod.TimeStop;

public class TimeStopBulletType extends AntiCheatBulletTypeBase {
    public float duration = 45.0F;

    public TimeStopBulletType(float speed, float damage) {
        super(speed, damage);
        this.despawnEffect = this.hitEffect = HitFx.endHitRedSmall;
        this.trailColor = UnityPal.scarColor;
        this.trailLength = 10;
        this.trailWidth = 4.0F;
        this.pierce = true;
        this.pierceCap = 3;
        this.lifetime = 110.0F;
        this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.01F, 2.0F, 8.0F, 3.0F)};
    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        Draw.color(this.trailColor);
        Drawf.tri(b.x, b.y, this.trailWidth * 2.0F * 1.22F, 14.0F, b.rotation());
        Drawf.tri(b.x, b.y, this.trailWidth * 2.0F * 1.22F, 7.0F, b.rotation() + 180.0F);
        Draw.color();
    }

    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        Bullet b = TimeStopBullet.create();
        b.type = this;
        b.owner = owner;
        b.team = team;
        b.time = 0.0F;
        b.initVel(angle, this.speed * velocityScl);
        if (this.backMove) {
            b.set(x - b.vel.x * Time.delta, y - b.vel.y * Time.delta);
        } else {
            b.set(x, y);
        }

        b.lifetime = this.lifetime * lifetimeScl;
        b.data = data;
        b.drag = this.drag;
        b.hitSize = this.hitSize;
        b.damage = (damage < 0.0F ? this.damage : damage) * b.damageMultiplier();
        if (b.trail != null) {
            b.trail.clear();
        }

        b.add();
        if (this.keepVelocity && owner instanceof Velc) {
            b.vel.add(((Velc)owner).vel());
        }

        if (TimeStop.inTimeStop() && owner != null) {
            float duration = Math.min(this.duration, TimeStop.getTime(owner));
            if (duration > 0.0F) {
                TimeStop.addEntity(b, duration);
            }
        }

        return b;
    }
}
