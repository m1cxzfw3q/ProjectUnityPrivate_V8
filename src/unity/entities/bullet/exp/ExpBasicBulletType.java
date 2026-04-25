package unity.entities.bullet.exp;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

public class ExpBasicBulletType extends ExpBulletType {
    public Color backColor;
    public Color frontColor;
    public Color mixColorFrom;
    public Color mixColorTo;
    public float width;
    public float height;
    public float shrinkX;
    public float shrinkY;
    public float spin;
    public String sprite;
    public TextureRegion backRegion;
    public TextureRegion frontRegion;

    public ExpBasicBulletType(float speed, float damage, String bulletSprite) {
        super(speed, damage);
        this.backColor = Pal.bulletYellowBack;
        this.frontColor = Pal.bulletYellow;
        this.mixColorFrom = new Color(1.0F, 1.0F, 1.0F, 0.0F);
        this.mixColorTo = new Color(1.0F, 1.0F, 1.0F, 0.0F);
        this.width = 5.0F;
        this.height = 7.0F;
        this.shrinkX = 0.0F;
        this.shrinkY = 0.5F;
        this.spin = 0.0F;
        this.sprite = bulletSprite;
        this.expOnHit = true;
    }

    public ExpBasicBulletType(float speed, float damage) {
        this(speed, damage, "bullet");
    }

    public ExpBasicBulletType() {
        this(1.0F, 1.0F, "bullet");
    }

    public void init() {
        super.init();
        this.despawnHit = !this.expOnHit;
    }

    public void load() {
        this.backRegion = Core.atlas.find(this.sprite + "-back");
        this.frontRegion = Core.atlas.find(this.sprite);
    }

    public void draw(Bullet b) {
        super.draw(b);
        float height = this.height * (1.0F - this.shrinkY + this.shrinkY * b.fout());
        float width = this.width * (1.0F - this.shrinkX + this.shrinkX * b.fout());
        float offset = -90.0F + (this.spin != 0.0F ? Mathf.randomSeed((long)b.id, 360.0F) + b.time * this.spin : 0.0F);
        Color mix = Tmp.c1.set(this.mixColorFrom).lerp(this.mixColorTo, b.fin());
        Draw.mixcol(mix, mix.a);
        Draw.color(this.getColor(b));
        Draw.rect(this.backRegion, b.x, b.y, width, height, b.rotation() + offset);
        Draw.color(this.frontColor);
        Draw.rect(this.frontRegion, b.x, b.y, width, height, b.rotation() + offset);
        Draw.reset();
    }
}
