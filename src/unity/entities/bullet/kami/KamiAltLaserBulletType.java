package unity.entities.bullet.kami;

import arc.Core;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Ellipse;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;

public class KamiAltLaserBulletType extends BulletType {
    public float fadeOut = 20.0F;
    public float fadeIn = 20.0F;
    private static final boolean test = false;
    private static final Ellipse tElpse = new Ellipse();
    private static TextureRegion circleRegion;

    public KamiAltLaserBulletType(float damage) {
        this.damage = damage;
        this.speed = 0.01F;
        this.hitEffect = Fx.none;
        this.despawnEffect = Fx.none;
        this.drawSize = 10000.0F;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public void init(Bullet b) {
        super.init(b);
        Object var3 = b.data;
        if (var3 instanceof KamiLaserData) {
            KamiLaserData data = (KamiLaserData)var3;
            data.init.get(data, b);
        }

    }

    public void update(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof KamiLaserData) {
            KamiLaserData data = (KamiLaserData)var3;
            data.update.get(data, b);
            Tmp.v1.set(data.x, data.y).add(data.x2, data.y2).scl(0.5F);
            b.x = Tmp.v1.x;
            b.y = Tmp.v1.y;
            if (b.timer(1, 5.0F)) {
                float fout = Mathf.clamp(b.time > b.lifetime - this.fadeOut ? 1.0F - (b.time - (b.lifetime - this.fadeOut)) / this.fadeOut : 1.0F) * Mathf.clamp(b.time / this.fadeIn) * data.width;
                float ang = Tmp.v2.set(data.x2, data.y2).sub(data.x, data.y).angle();
                float dst = Tmp.v2.len();
                Tmp.r1.setCentered(data.x, data.y, fout * 2.0F);
                Tmp.r2.setCentered(data.x2, data.y2, fout * 2.0F);
                Tmp.r1.merge(Tmp.r2);
                Units.nearby(Tmp.r1, (e) -> {
                    if (e.team != b.team) {
                        float size = e.hitSize / 2.0F;
                        Tmp.v2.set(e).sub(Tmp.v1).rotate(-ang);
                        tElpse.set(0.0F, 0.0F, dst * 2.0F + size, fout * 2.0F + size);
                        if (tElpse.contains(Tmp.v2)) {
                            b.collision(e, e.x, e.y);
                        }
                    }

                });
            }
        }

    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof KamiLaserData) {
            KamiLaserData data = (KamiLaserData)var3;
            if (circleRegion == null) {
                circleRegion = Core.atlas.find("circle");
            }

            float fout = Mathf.clamp(b.time > b.lifetime - this.fadeOut ? 1.0F - (b.time - (b.lifetime - this.fadeOut)) / this.fadeOut : 1.0F) * Mathf.clamp(b.time / this.fadeIn);
            Draw.color(Tmp.c1.set(Color.red).shiftHue(b.time * 3.0F));
            Lines.stroke((data.width + 3.0F) * fout * 2.0F);
            Tmp.v1.set(data.x, data.y).sub(data.x2, data.y2).setLength(3.0F);
            Tmp.v2.set(data.x2, data.y2).sub(data.x, data.y).setLength(3.0F);
            Lines.line(circleRegion, data.x + Tmp.v1.x, data.y + Tmp.v1.y, data.x2 + Tmp.v2.x, data.y2 + Tmp.v2.y, false);
            Draw.color();
            Lines.stroke(data.width * fout * 2.0F);
            Lines.line(circleRegion, data.x, data.y, data.x2, data.y2, false);
        }

    }

    public void drawLight(Bullet b) {
    }

    public Bullet create(Entityc owner, Team team, KamiLaserData data) {
        Tmp.v1.set(data.x, data.y).add(data.x2, data.y2).scl(0.5F);
        Tmp.v2.set(data.x2, data.y2).sub(data.x, data.y);
        return this.create(owner, team, Tmp.v1.x, Tmp.v1.y, Tmp.v2.angle(), -1.0F, 1.0F, 1.0F, data);
    }

    public static class KamiLaserData {
        public Cons2<KamiLaserData, Bullet> update = (data, b) -> {
        };
        public Cons2<KamiLaserData, Bullet> init = (data, bullet) -> {
        };
        public float x;
        public float y;
        public float x2;
        public float y2;
        public float width;
        public Object data;
    }
}
