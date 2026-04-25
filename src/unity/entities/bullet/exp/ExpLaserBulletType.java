package unity.entities.bullet.exp;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.graphics.Drawf;
import unity.util.Utils;

public abstract class ExpLaserBulletType extends ExpBulletType {
    public float width;
    public float length;
    public float lengthInc;
    public float[] strokes;
    public int buildingExpGain;
    public boolean hitMissed;
    public boolean blip;

    public ExpLaserBulletType(float length, float damage) {
        super(0.01F, damage);
        this.width = 1.0F;
        this.strokes = new float[]{2.9F, 1.8F, 1.0F};
        this.hitMissed = false;
        this.blip = false;
        this.length = length;
        this.ammoMultiplier = 1.0F;
        this.drawSize = length * 2.0F;
        this.hitSize = 0.0F;
        this.hitEffect = Fx.hitLiquid;
        this.shootEffect = Fx.hitLiquid;
        this.lifetime = 18.0F;
        this.despawnEffect = Fx.none;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
        this.expOnHit = false;
    }

    public ExpLaserBulletType() {
        this(120.0F, 1.0F);
    }

    public float getLength(Bullet b) {
        return this.length + this.lengthInc * (float)this.getLevel(b);
    }

    public float range() {
        return Math.max(this.length, this.maxRange);
    }

    public void init(Bullet b) {
        super.init(b);
        this.despawnHit = false;
        this.setDamage(b);
        Healthc target = Utils.linecast(b, b.x, b.y, b.rotation(), this.getLength(b));
        b.data = target;
        if (target instanceof Hitboxc) {
            Hitboxc hit = (Hitboxc)target;
            hit.collision(b, hit.x(), hit.y());
            b.collision(hit, hit.x(), hit.y());
            this.handleExp(b, hit.x(), hit.y(), this.expGain);
        } else {
            if (target instanceof Building) {
                Building tile = (Building)target;
                if (tile.collide(b)) {
                    tile.collision(b);
                    this.hit(b, tile.x, tile.y);
                    this.handleExp(b, tile.x, tile.y, this.expGain);
                    return;
                }
            }

            Vec2 v = (new Vec2()).trns(b.rotation(), this.getLength(b)).add(b.x, b.y);
            b.data = v;
            if (this.hitMissed) {
                this.hit(b, v.x, v.y);
            }
        }

    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof Position) {
            Position point = (Position)var3;
            Tmp.v1.set(point);
            Draw.color(this.getColor(b));
            Draw.alpha(0.4F);
            Lines.stroke(b.fout() * this.width * this.strokes[0]);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
            Draw.alpha(1.0F);
            Lines.stroke(b.fout() * this.width * this.strokes[1]);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
            Draw.color(Color.white);
            Lines.stroke(b.fout() * this.width * this.strokes[2]);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
            if (this.blip) {
                Draw.color(Color.white, Tmp.c2, b.fin());
                Lines.circle(Tmp.v1.x, Tmp.v1.y, b.finpow() * this.width * 5.0F);
            }

            Draw.reset();
            Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * 10.0F * b.fout(), Color.white, 0.6F);
        }

    }

    public void drawLight(Bullet b) {
    }
}
