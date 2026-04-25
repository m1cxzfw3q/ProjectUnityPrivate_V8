package unity.entities.bullet.energy;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class ArrowBulletType extends BasicBulletType {
    public ArrowBulletType(float speed, float damage) {
        super(speed, damage);
        this.trailLength = 35;
    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        Tmp.v1.trns(b.rotation(), this.height / 2.0F);

        for(int s : Mathf.signs) {
            Tmp.v2.trns(b.rotation() - 90.0F, this.width * (float)s, -this.height);
            Draw.color(this.backColor);
            Fill.tri(Tmp.v1.x + b.x, Tmp.v1.y + b.y, -Tmp.v1.x + b.x, -Tmp.v1.y + b.y, Tmp.v2.x + b.x, Tmp.v2.y + b.y);
            Draw.color(this.frontColor);
            Fill.tri(Tmp.v1.x / 2.0F + b.x, Tmp.v1.y / 2.0F + b.y, -Tmp.v1.x / 2.0F + b.x, -Tmp.v1.y / 2.0F + b.y, Tmp.v2.x / 2.0F + b.x, Tmp.v2.y / 2.0F + b.y);
        }

    }
}
