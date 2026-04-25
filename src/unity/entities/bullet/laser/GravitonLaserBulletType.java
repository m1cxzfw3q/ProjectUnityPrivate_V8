package unity.entities.bullet.laser;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import unity.util.Utils;

public class GravitonLaserBulletType extends ContinuousLaserBulletType {
    public int max = 6;
    private int len;

    public GravitonLaserBulletType(float damage) {
        super(damage);
    }

    public void init(Bullet b) {
        super.init(b);
        b.fdata = this.length;
    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F)) {
            this.len = 0;
            b.fdata = this.length;
            Tmp.v1.trns(b.rotation(), this.length).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, (build, direct) -> {
                if (direct) {
                    build.damage(this.damage);
                    ++this.len;
                }

                if (build.block.absorbLasers || direct && this.len >= this.max) {
                    b.fdata = b.dst(build);
                }

                return build.block.absorbLasers || direct && this.len >= this.max;
            }, (unit) -> {
                unit.damage(this.damage);
                if (this.knockback != 0.0F) {
                    unit.impulse(Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F));
                }

                if (this.statusDuration > 0.0F) {
                    unit.apply(this.status, this.statusDuration);
                }

                if (this.len >= this.max) {
                    b.fdata = b.dst(unit);
                }

                return this.len >= this.max;
            }, (ex, ey) -> this.hit(b, ex, ey), true);
        }

    }

    public void draw(Bullet b) {
        float realLength = b.fdata;
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);
        float baseLen = realLength * fout;
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);

        for(int s = 0; s < this.colors.length; ++s) {
            Draw.color(Tmp.c1.set(this.colors[s]).mul(1.0F + Mathf.absin(Time.time, 1.0F, 0.1F)));

            for(int i = 0; i < this.tscales.length; ++i) {
                Tmp.v1.trns(b.rotation() + 180.0F, (this.lenscales[i] - 1.0F) * this.spaceMag);
                Lines.stroke((this.width + Mathf.absin(Time.time, this.oscScl, this.oscMag)) * fout * this.strokes[s] * this.tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * this.lenscales[i], false);
            }
        }

        Tmp.v1.trns(b.rotation(), baseLen * 1.1F);
        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.lightStroke, this.lightColor, 0.7F);
        Draw.reset();
    }
}
