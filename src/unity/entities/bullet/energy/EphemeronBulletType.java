package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import unity.content.UnityFx;

public class EphemeronBulletType extends BasicBulletType {
    public Color midColor;
    public float[] baseRadius;
    public float[] extraRadius;
    public float maxRadius;
    public int pairs;
    public BulletType positive;
    public BulletType negative;

    public EphemeronBulletType(float speed, float damage) {
        super(speed, damage);
        this.midColor = Pal.lancerLaser;
        this.baseRadius = new float[]{11.0F, 8.0F, 6.5F};
        this.extraRadius = new float[]{2.5F, 1.5F, 1.0F};
        this.maxRadius = 80.0F;
        this.pairs = 15;
        this.hittable = false;
        this.backColor = Color.valueOf("a9d8ff60");
        this.frontColor = Color.white;
    }

    public void draw(Bullet b) {
        Draw.color(this.backColor);
        Fill.circle(b.x, b.y, this.baseRadius[0] + b.fout() * this.extraRadius[0]);
        Draw.color(this.midColor);
        Fill.circle(b.x, b.y, this.baseRadius[1] + b.fout() * this.extraRadius[1]);
        Draw.color(this.frontColor);
        Fill.circle(b.x, b.y, this.baseRadius[2] + b.fout() * this.extraRadius[2]);
    }

    public void despawned(Bullet b) {
        super.despawned(b);

        for(int i = 0; i < this.pairs; ++i) {
            Tmp.v1.rnd(Mathf.range(this.maxRadius)).add(b);
            float randomSign = Mathf.random(180.0F);
            float randomB = Mathf.random(0.2F, 1.4F);
            float angleRandom = Mathf.range(360.0F);
            float rangeRandom = Mathf.range(40.0F, 70.0F);
            Tmp.v2.trns(angleRandom, rangeRandom);
            Bullet pos = this.positive.create(b, Tmp.v1.x + Tmp.v2.x, Tmp.v1.y + Tmp.v2.y, angleRandom + randomSign);
            Tmp.v2.rotate(180.0F);
            Bullet neg = this.negative.create(b, Tmp.v1.x + Tmp.v2.x, Tmp.v1.y + Tmp.v2.y, angleRandom + randomSign + 180.0F);
            pos.data = neg;
            neg.data = pos;
            Tmp.v2.trns(angleRandom + randomSign, randomB);
            pos.vel.add(Tmp.v2);
            neg.vel.add(Tmp.v2.rotate(180.0F));
            UnityFx.ephemeronLaser.at(b.x, b.y, 0.0F, ((BasicBulletType)pos.type).frontColor, new EphemeronEffectData(pos, b.x, b.y));
            UnityFx.ephemeronLaser.at(b.x, b.y, 0.0F, ((BasicBulletType)neg.type).frontColor, new EphemeronEffectData(neg, b.x, b.y));
        }

    }

    public class EphemeronEffectData {
        public Bullet b;
        public float x;
        public float y;

        public EphemeronEffectData(Bullet b, float x, float y) {
            this.b = b;
            this.x = x;
            this.y = y;
        }
    }
}
