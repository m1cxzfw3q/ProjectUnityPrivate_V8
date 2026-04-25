package unity.entities.bullet.exp;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;

public class GeyserLaserBulletType extends ExpLaserBulletType {
    public float widthInc = 0.05F;
    public BulletType geyser;

    public GeyserLaserBulletType(float length, float damage) {
        super(length, damage);
        this.width = 3.0F;
        this.hitMissed = true;
    }

    public Liquid getLiquid(Bullet b) {
        Object var3 = b.data;
        Liquid var10000;
        if (var3 instanceof Liquid) {
            Liquid l = (Liquid)var3;
            var10000 = l;
        } else {
            var10000 = Liquids.water;
        }

        return var10000;
    }

    public void init(Bullet b) {
        Liquid l = this.getLiquid(b);
        super.init(b);
        Position dest = (Position)b.data;
        b.rotation(b.angleTo(dest));
        b.fdata = b.dst(dest);
        b.data = l;
        if (this.geyser != null) {
            this.geyser.create(b.owner, b.team, dest.getX(), dest.getY(), b.rotation(), -1.0F, 1.0F, 1.0F, l);
        }

    }

    public void draw(Bullet b) {
        Tmp.v1.trns(b.rotation(), b.fdata).add(b);
        float width = this.width + this.widthInc * (float)this.getLevel(b);
        Liquid l = this.getLiquid(b);
        Draw.color(l.color, 1.0F);
        Draw.alpha(0.4F);
        Lines.stroke(b.fout() * width * this.strokes[0]);
        Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
        Fill.circle(b.x, b.y, b.fout() * width * 0.9F * this.strokes[0]);
        Draw.alpha(1.0F);
        Lines.stroke(b.fout() * width * this.strokes[1]);
        Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
        Fill.circle(b.x, b.y, b.fout() * width * 0.9F * this.strokes[1]);
        Draw.color(l.color, Color.white, 0.6F);
        Lines.stroke(b.fout() * width * this.strokes[2]);
        Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
        Fill.circle(b.x, b.y, b.fout() * width * 0.9F * this.strokes[2]);
        Draw.reset();
        Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 10.0F * b.fout(), l.lightColor, l.lightColor.a);
    }
}
