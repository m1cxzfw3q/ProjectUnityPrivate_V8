package unity.entities.bullet.laser;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.util.Utils;

public class AnomalyLaserBulletType extends BulletType {
    float length = 250.0F;
    float lengthFalloff = 0.6F;
    float sideLength = 29.0F;
    float sideWidth = 0.7F;
    float sideAngle = 90.0F;
    Color[] colors;

    public AnomalyLaserBulletType(float damage) {
        super(0.0F, damage);
        this.colors = new Color[]{Pal.lancerLaser.cpy().mul(0.9F).a(0.3F), Pal.lancerLaser, Color.white};
        this.despawnEffect = Fx.none;
        this.hitEffect = Fx.lancerLaserShoot;
        this.keepVelocity = false;
        this.lifetime = 20.0F;
    }

    public float range() {
        return this.length;
    }

    public void init() {
        super.init();
        this.drawSize = (this.length + 375.0F) * 2.0F;
    }

    public void init(Bullet b) {
        float charge = Math.max(b.damage - this.damage, 0.0F);
        b.fdata = this.length + charge * 1.5F;
        float d = this.damage * (charge / 70.0F + 1.0F);
        float size = Mathf.sqrt(charge / 3.0F);
        int lighLength = Math.min(Mathf.round((charge - 30.0F) / 9.0F), 18);
        int lighAmount = Math.min(Mathf.ceil((charge - 30.0F) / 60.0F), 3);
        Tmp.v1.trns(b.rotation(), b.fdata).add(b);
        Utils.collideLineRawEnemyRatio(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, Math.max(size, 8.0F), (building, ratio, direct) -> {
            if (direct) {
                building.damage(d * ratio);
            }

            if (building.block.absorbLasers) {
                b.fdata = b.dst(building);
            }

            return building.block.absorbLasers;
        }, (unit, ratio) -> {
            Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F * ratio);
            if (this.impact) {
                Tmp.v3.setAngle(b.rotation() + (this.knockback < 0.0F ? 180.0F : 0.0F));
            }

            unit.impulse(Tmp.v3);
            unit.apply(this.status, this.statusDuration);
            unit.damage(d * ratio);
            return false;
        }, (ex, ey) -> {
            this.hit(b, ex, ey);
            if (lighLength >= 5 && lighAmount > 0) {
                for(int i = 0; i < lighAmount; ++i) {
                    int len = Mathf.random(lighLength / 2, lighLength);
                    Lightning.create(b.team, this.lightningColor, d / 5.0F, ex, ey, b.rotation() + Mathf.range(25.0F), len);
                }
            }

        });
    }

    public void draw(Bullet b) {
        float realLength = b.fdata;
        float f = Mathf.curve(b.fin(), 0.0F, 0.2F);
        float charge = Math.max(b.damage - this.damage, 0.0F);
        float width = (Mathf.sqrt(charge / 3.0F) + 3.0F) * 2.5F;
        float cw = width / this.lengthFalloff;
        float compound = 1.0F;
        float baseLen = realLength * f;
        float sLength = charge / 6.0F;
        sLength *= sLength / 2.0F;
        sLength /= 5.0F;
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);

        for(Color color : this.colors) {
            Draw.color(color);
            Lines.stroke((cw *= this.lengthFalloff) * b.fout());
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
            Tmp.v1.trns(b.rotation(), baseLen).add(b);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() * 1.22F, cw * 2.0F + width / 2.0F, b.rotation());
            Fill.circle(b.x, b.y, cw * b.fout());
            float offset = Math.min((charge - 40.0F) / 7.0F, 30.0F);

            for(int i : Mathf.signs) {
                if (offset <= 0.0F) {
                    Drawf.tri(b.x, b.y, this.sideWidth * b.fout() * cw, (this.sideLength + sLength) * compound, b.rotation() + this.sideAngle * (float)i);
                } else {
                    for(int s : Mathf.signs) {
                        Drawf.tri(b.x, b.y, this.sideWidth * b.fout() * cw, (this.sideLength + sLength) * compound, b.rotation() + this.sideAngle * (float)i + offset * (float)s);
                    }
                }
            }

            compound *= this.lengthFalloff;
        }

        Draw.reset();
        Tmp.v1.trns(b.rotation(), baseLen * 1.1F).add(b);
        Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 1.7F * b.fout(), this.colors[0], 0.6F);
    }

    public void drawLight(Bullet b) {
    }
}
