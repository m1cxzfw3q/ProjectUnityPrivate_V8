package unity.entities.bullet.kami;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import unity.gen.KamiLaser;

public class NewKamiLaserBulletType extends BulletType {
    static TextureRegion hcircle;

    public NewKamiLaserBulletType() {
        this.speed = 0.0F;
        this.damage = 9.0F;
        this.absorbable = false;
        this.hittable = false;
        this.collidesTiles = false;
        this.pierce = true;
        this.keepVelocity = false;
    }

    public void load() {
        hcircle = Core.atlas.find("hcircle");
    }

    public void draw(Bullet b) {
        KamiLaser lb = (KamiLaser)b;
        TextureRegion r = KamiBulletType.region;
        float time = b.time * 2.0F + Time.time / 2.0F;
        Tmp.c1.set(Color.red).shiftHue(time);
        if (lb.ellipseCollision) {
            Vec2 v = Tmp.v1.set(lb.x, lb.y).sub(lb.x2, lb.y2).setLength(3.0F);
            Lines.stroke((lb.width + 3.5F) * 2.0F);
            Draw.color(Tmp.c1);
            Lines.line(r, lb.x + v.x, lb.y + v.y, lb.x2 - v.x, lb.y2 - v.y, false);
            Draw.color();
            Lines.stroke(lb.width * 2.0F);
            Lines.line(r, lb.x, lb.y, lb.x2, lb.y2, false);
            Draw.reset();
        } else {
            float ang = lb.angleTo(lb.x2, lb.y2);
            Draw.blend(Blending.additive);
            Draw.color(Tmp.c1);
            Lines.stroke(lb.width * 2.0F);
            Lines.line(lb.x, lb.y, lb.x2, lb.y2, false);
            Draw.rect(hcircle, lb.x, lb.y, lb.width * 2.0F, lb.width * 2.0F, ang + 180.0F);
            Draw.rect(hcircle, lb.x2, lb.y2, lb.width * 2.0F, lb.width * 2.0F, ang);
            Draw.blend();
        }

    }

    public void drawLight(Bullet b) {
    }

    public KamiLaser createL(Entityc owner, Team team, float x, float y, float x2, float y2, Object data) {
        KamiLaser b = (KamiLaser)this.create(owner, team, x, y, 0.0F, -1.0F, 1.0F, 1.0F, data);
        b.x2 = x2;
        b.y2 = y2;
        return b;
    }

    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        KamiLaser bullet = KamiLaser.create();
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
        bullet.width = this.hitSize;
        bullet.damage = (damage < 0.0F ? this.damage : damage) * bullet.damageMultiplier();
        bullet.add();
        return bullet;
    }
}
