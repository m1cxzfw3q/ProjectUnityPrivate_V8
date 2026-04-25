package unity.entities.bullet.kami;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import java.util.Objects;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import unity.content.effects.SpecialFx;
import unity.gen.KamiBullet;

public class KamiBulletType extends BulletType {
    public static TextureRegion region;
    public float delay = -1.0F;

    public KamiBulletType() {
        this.speed = 1.0F;
        this.damage = 9.0F;
        this.absorbable = false;
        this.hittable = false;
        this.collidesTiles = false;
        this.pierce = true;
        this.keepVelocity = false;
        this.shootEffect = SpecialFx.kamiBulletSpawn;
    }

    public void load() {
        if (region == null) {
            region = Core.atlas.find("circle");
        }

    }

    public void draw(Bullet b) {
        KamiBullet kb = (KamiBullet)b;
        if (!kb.isTelegraph()) {
            float time = b.time * 2.0F + Time.time / 2.0F;
            float st = Mathf.clamp(Math.max(kb.width, kb.length) / 10.0F + 1.2F, 1.5F, 4.0F) * (1.0F + Mathf.absin(time, 10.0F, 0.33F));
            Tmp.c1.set(Color.red).shiftHue(time);
            this.drawTrail(b);
            Draw.color(Tmp.c1);
            Draw.rect(region, b.x, b.y, kb.width * 2.0F + st, kb.length * 2.0F + st, b.rotation());
            Draw.color(Color.white);
            Draw.rect(region, b.x, b.y, kb.width * 2.0F, kb.length * 2.0F, b.rotation());
        } else {
            Draw.blend(Blending.additive);
            FloatSeq seq = kb.lastPositions;
            float time = Time.time / 2.0F;
            float fout = 1.0F - Mathf.clamp(b.time - (b.lifetime - 40.0F)) / 40.0F;
            int max = seq.size / 2 - 1;

            for(int i = 0; i < seq.size - 2; i += 2) {
                int s = i / 2;
                float fin = ((float)s + 1.0F) / (float)max;
                float x1 = seq.get(i);
                float y1 = seq.get(i + 1);
                float x2 = seq.get(i + 2);
                float y2 = seq.get(i + 3);
                Tmp.c1.set(Color.red).shiftHue(time).a(fin);
                Draw.color(Tmp.c1);
                Lines.stroke(3.0F);
                Lines.line(x1, y1, x2, y2, false);
                time += b.time * 2.0F / 3.0F;
            }

            Draw.blend();
        }

    }

    public void drawTrail(Bullet b) {
        if (this.trailLength > 0 && b.trail != null) {
            KamiBullet kb = (KamiBullet)b;
            float z = Draw.z();
            Draw.z(z - 1.0E-4F);
            Draw.blend(Blending.additive);
            b.trail.draw(Tmp.c1, kb.width);
            Draw.blend();
            Draw.z(z);
        }

    }

    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        KamiBullet bullet = KamiBullet.create();
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
        bullet.length = this.hitSize;
        bullet.damage = (damage < 0.0F ? this.damage : damage) * bullet.damageMultiplier();
        if (this.delay > 0.0F) {
            this.shootEffect.at(x, y, angle, bullet);
            float var10000 = this.delay;
            Objects.requireNonNull(bullet);
            Time.run(var10000, bullet::add);
        } else {
            bullet.add();
        }

        return bullet;
    }
}
