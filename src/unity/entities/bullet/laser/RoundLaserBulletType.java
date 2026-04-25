package unity.entities.bullet.laser;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class RoundLaserBulletType extends LaserBulletType {
    public float lightStroke = 40.0F;
    public float spaceMag = 45.0F;
    public float[] tscales = new float[]{1.0F, 0.7F, 0.5F, 0.24F};
    public float[] strokes = new float[]{2.8F, 2.4F, 1.9F, 1.3F};
    public float[] lenscales = new float[]{1.0F, 1.13F, 1.16F, 1.17F};

    public RoundLaserBulletType(float damage) {
        super(damage);
        this.lifetime = 14.0F;
        this.colors = new Color[]{Color.valueOf("4787ff55"), Color.valueOf("4787ffaa"), Pal.lancerLaser, Color.white};
    }

    public void draw(Bullet b) {
        float realLength = b.fdata;
        float baseLen = realLength * b.fout();
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);

        for(int s = 0; s < this.colors.length; ++s) {
            Draw.color(Tmp.c1.set(this.colors[s]));

            for(int i = 0; i < this.tscales.length; ++i) {
                Tmp.v1.trns(b.rotation() + 180.0F, (this.lenscales[i] - 1.0F) * this.spaceMag);
                Lines.stroke(this.width * b.fout() * this.strokes[s] * this.tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * this.lenscales[i], false);
            }
        }

        Tmp.v1.trns(b.rotation(), baseLen * 1.1F);
        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.lightStroke, this.lightColor, 0.7F);
        Draw.reset();
    }
}
