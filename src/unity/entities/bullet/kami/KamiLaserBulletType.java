package unity.entities.bullet.kami;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Ellipse;
import arc.math.geom.Intersector;
import arc.math.geom.Position;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import unity.gen.Kami;

public class KamiLaserBulletType extends BulletType {
    private static final Vec2 tVec = new Vec2();
    private static final Vec2 tVecB = new Vec2();
    private static final Vec2 tVecC = new Vec2();
    private static final Vec2 tVecD = new Vec2();
    private static final Rect tRect1 = new Rect();
    private static final Rect tRect2 = new Rect();
    private static final Ellipse tElpse = new Ellipse();
    private static TextureRegion circleRegion;
    public float length = 280.0F;
    public float width = 45.0F;
    public float curveScl = 2.0F;
    public float fadeTime = 16.0F;
    public float fadeInTime = 16.0F;

    public KamiLaserBulletType(float damage) {
        super(0.001F, damage);
        this.collides = false;
        this.keepVelocity = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
        this.despawnEffect = Fx.none;
    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F)) {
            float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (b.lifetime - this.fadeTime)) / this.fadeTime : 1.0F) * Mathf.clamp(b.time / this.fadeInTime) * this.width;
            tVec.trns(b.rotation(), this.length + this.width * this.curveScl).add(b);
            tVecD.trns(b.rotation(), -this.width * this.curveScl).add(b);
            tRect1.setCentered(tVecD.x, tVecD.y, this.width);
            tRect2.setCentered(tVec.x, tVec.y, this.width);
            tRect1.merge(tRect2);
            Units.nearby(tRect1, (e) -> {
                tVecB.trns(b.rotation(), this.length).add(b);
                tVecD.trns(b.rotation(), this.width * this.curveScl).add(b);
                tVecC.set(e);
                if (b.team != e.team) {
                    Position a = e.dst(tVecB) < e.dst(tVecD) ? tVecB : tVecD;
                    float ba = e.dst(tVecB) < e.dst(tVecD) ? 0.0F : 180.0F;
                    float size = e.hitSize / 2.0F;
                    if (angDist(a.angleTo(e), b.rotation() + ba) < 90.0F) {
                        tVec.trns(-b.rotation(), e.x, e.y);
                        Tmp.v1.set(e).sub(a).rotate(-b.rotation());
                        tElpse.set(0.0F, 0.0F, (this.curveScl * fout + size) * 2.0F, (fout + size) * 2.0F);
                        if (tElpse.contains(Tmp.v1)) {
                            this.hitEffect.at(e.x, e.y);
                            b.collision(e, e.x, e.y);
                        }
                    } else if (Intersector.intersectSegmentCircle(tVecD, tVecB, tVecC, fout * fout + size * size)) {
                        this.hitEffect.at(e.x, e.y);
                        b.collision(e, e.x, e.y);
                    }
                }

            });
        }

    }

    private static float angDist(float a, float b) {
        float x = Math.abs(a - b) % 360.0F;
        return x > 180.0F ? 360.0F - x : x;
    }

    public void draw(Bullet b) {
        if (circleRegion == null) {
            circleRegion = Core.atlas.find("circle");
        }

        tVecD.trns(b.rotation(), this.width * this.curveScl).add(b);
        float widthAlt = this.width + 3.0F;
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (b.lifetime - this.fadeTime)) / this.fadeTime : 1.0F) * Mathf.clamp(b.time / this.fadeInTime);
        tVec.trns(b.rotation(), this.length).add(b);
        Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time * 3.0F));
        Draw.rect(circleRegion, tVecD, Draw.scl * widthAlt * this.curveScl * 8.0F, Draw.scl * widthAlt * fout * 8.0F, b.rotation());
        Draw.rect(circleRegion, tVec, Draw.scl * widthAlt * this.curveScl * 8.0F, Draw.scl * widthAlt * fout * 8.0F, b.rotation());
        Lines.stroke(widthAlt * 2.0F * fout);
        Lines.line(tVecD.x, tVecD.y, tVec.x, tVec.y, false);
        Draw.color(Color.white);
        Draw.rect(circleRegion, tVecD, Draw.scl * this.width * this.curveScl * 8.0F, Draw.scl * this.width * fout * 8.0F, b.rotation());
        Draw.rect(circleRegion, tVec, Draw.scl * this.width * this.curveScl * 8.0F, Draw.scl * this.width * fout * 8.0F, b.rotation());
        Lines.stroke(this.width * 2.0F * fout);
        Lines.line(tVecD.x, tVecD.y, tVec.x, tVec.y, false);
        Draw.reset();
    }

    public void init(Bullet b) {
        super.init(b);
        Entityc var3 = b.owner;
        if (var3 instanceof Kami) {
            Kami kami = (Kami)var3;
            kami.laser = b;
        }

    }

    public void despawned(Bullet b) {
        super.despawned(b);
        Entityc var3 = b.owner;
        if (var3 instanceof Kami) {
            Kami kami = (Kami)var3;
            if (kami.laser == b) {
                kami.laser = null;
            }
        }

    }
}
