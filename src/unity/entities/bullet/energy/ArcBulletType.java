package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import unity.content.UnityFx;

public class ArcBulletType extends BulletType {
    public Color fromColor = Color.valueOf("6c8fc7");
    public Color toColor = Color.valueOf("606571");
    public Color lightningC1;
    public Color lightningC2;
    public Color smokeColor;
    public int length1;
    public int length2;
    public int lengthRand1;
    public int lengthRand2;
    public float lightningDamage1;
    public float lightningDamage2;
    public float lightningInaccuracy1;
    public float lightningInaccuracy2;
    public float radius;
    public float smokeChance;
    public float lightningChance1;
    public float lightningChance2;
    public Effect arcSmokeEffect;
    public Effect arcSmokeEffect2;

    public ArcBulletType(float speed, float damage) {
        super(speed, damage);
        this.lightningC1 = Pal.lancerLaser;
        this.lightningC2 = Color.valueOf("8494b3");
        this.smokeColor = Pal.bulletYellowBack;
        this.length2 = 8;
        this.lengthRand2 = 4;
        this.lightningInaccuracy2 = 180.0F;
        this.radius = 12.0F;
        this.arcSmokeEffect = UnityFx.arcSmoke;
        this.arcSmokeEffect2 = UnityFx.arcSmoke2;
        this.despawnEffect = this.shootEffect = Fx.none;
        this.collidesTiles = this.hittable = false;
        this.pierce = true;
    }

    public void update(Bullet b) {
        super.update(b);
        if (Mathf.chanceDelta((double)this.smokeChance)) {
            this.arcSmokeEffect.at(b.x + Mathf.range(2.0F), b.y + Mathf.range(2.0F), b.rotation(), this.smokeColor);
        }

        if (Mathf.chanceDelta((double)this.lightningChance1)) {
            Tmp.v1.trns(b.rotation() + Mathf.range(2.0F), this.radius);
            Lightning.create(b, this.lightningC1, this.lightningDamage1, b.x + Tmp.v1.x + Mathf.range(this.radius), b.y + Tmp.v1.y + Mathf.range(this.radius), b.rotation() + Mathf.range(this.lightningInaccuracy1), this.length1 + Mathf.range(this.lengthRand1));
        }

        if (Mathf.chanceDelta((double)this.lightningChance2)) {
            Tmp.v1.trns(b.rotation() + Mathf.range(2.0F), this.radius);
            Lightning.create(b, this.lightningC1, this.lightningDamage2, b.x + Tmp.v1.x + Mathf.range(this.radius), b.y + Tmp.v1.y + Mathf.range(this.radius), b.rotation() + Mathf.range(this.lightningInaccuracy2), this.length2 + Mathf.range(this.lengthRand2));
        }

        if (Mathf.chanceDelta((double)1.0F)) {
            this.arcSmokeEffect2.at(b.x + Mathf.range(this.radius), b.y + Mathf.range(this.radius), b.rotation() + Mathf.range(2.0F), this.smokeColor);
        }

    }

    public void draw(Bullet b) {
        Draw.color(this.fromColor, this.toColor, b.fin());
        Fill.poly(b.x, b.y, 6, 6.0F + b.fout() * 6.1F, b.rotation());
        Draw.reset();
    }
}
