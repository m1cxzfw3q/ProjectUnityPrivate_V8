package unity.entities.bullet.physical;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class MortarBulletType extends BasicBulletType {
    public float heightScl = 1.5F;

    public MortarBulletType(float speed, float damage) {
        super(speed, damage, "shell");
        this.collides = false;
        this.collidesTiles = false;
        this.scaleVelocity = true;
        this.shrinkX = this.shrinkY = 0.0F;
        this.trailLength = 15;
        this.trailInterp = (a) -> Mathf.sin(a * (float)Math.PI);
    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        float f = Mathf.lerp(Math.max(b.fdata, 0.0F), 1.0F, 0.125F);
        float scl = 1.0F + this.heightScl * Interp.sineOut.apply(b.fslope()) * f;
        float height = this.height * (1.0F - this.shrinkY + this.shrinkY * b.fout()) * scl;
        float width = this.width * (1.0F - this.shrinkX + this.shrinkX * b.fout()) * scl;
        float offset = -90.0F + (this.spin != 0.0F ? Mathf.randomSeed((long)b.id, 360.0F) + b.time * this.spin : 0.0F);
        Color mix = Tmp.c1.set(this.mixColorFrom).lerp(this.mixColorTo, b.fin());
        Draw.mixcol(mix, mix.a);
        Draw.color(this.backColor);
        Draw.rect(this.backRegion, b.x, b.y, width, height, b.rotation() + offset);
        Draw.color(this.frontColor);
        Draw.rect(this.frontRegion, b.x, b.y, width, height, b.rotation() + offset);
        Draw.reset();
    }
}
