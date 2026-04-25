package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import java.util.Arrays;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class FlameBulletType extends BulletType {
    public Color[] colors;
    public Color[] smokeColors;
    public float particleSpread;
    public float particleSizeScl;
    public int particleAmount;
    private final Color tc;
    private final Color tc2;
    private Color[] hitColors;

    public FlameBulletType(float speed, float damage) {
        super(speed, damage);
        this.colors = new Color[]{Pal.lightFlame, Pal.darkFlame, Color.gray};
        this.smokeColors = new Color[0];
        this.particleSpread = 10.0F;
        this.particleSizeScl = 1.5F;
        this.particleAmount = 8;
        this.tc = new Color();
        this.tc2 = new Color();
        this.pierce = true;
        this.lifetime = 12.0F;
        this.despawnEffect = Fx.none;
        this.status = StatusEffects.burning;
        this.statusDuration = 240.0F;
        this.hitSize = 7.0F;
        this.collidesAir = false;
        this.keepVelocity = false;
        this.hittable = false;
        this.layer = 110.001F;
    }

    public void init() {
        super.init();
        this.hitColors = (Color[])Arrays.copyOf(this.colors, Math.max(1, this.colors.length - 1));
        this.shootEffect = (new Effect(this.lifetime + 15.0F, this.range() * 2.0F, (e) -> {
            Draw.color(this.tc.lerp(this.colors, e.fin()));
            this.tc2.set(this.tc).shiftSaturation(0.77F);
            Angles.randLenVectors((long)e.id, this.particleAmount, e.finpow() * (this.range() + 15.0F), e.rotation, this.particleSpread, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.65F + e.fout() * this.particleSizeScl);
                Drawf.light((Team)null, e.x + x, e.y + y, (0.65F + e.fout(Interp.pow4Out) * this.particleSizeScl) * 4.0F, this.tc2, 0.5F * e.fout(Interp.pow2Out));
            });
        })).layer(this.layer);
        if (this.smokeColors != null && this.smokeColors.length > 0) {
            this.smokeEffect = (new Effect(this.lifetime * 3.0F, this.range() * 2.25F, (e) -> {
                Draw.color(this.tc.lerp(this.smokeColors, e.fin()));
                float slope = (0.5F - Math.abs(e.fin(Interp.pow2InInverse) - 0.5F)) * 2.0F;
                Angles.randLenVectors((long)e.id, this.particleAmount, e.fin(Interp.pow5Out) * (this.range() * 1.125F + 15.0F), e.rotation, this.particleSpread, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.65F + slope * this.particleSizeScl);
                    Fill.circle(e.x + x / 2.0F, e.y + y / 2.0F, 0.5F + slope * (this.particleSizeScl / 2.0F));
                });
            })).followParent(false).layer(this.layer - 0.001F);
        }

        this.hitEffect = new Effect(14.0F, (e) -> {
            Draw.color(this.tc.lerp(this.hitColors, e.fin()));
            Lines.stroke(0.5F + e.fout());
            Angles.randLenVectors((long)e.id, this.particleAmount / 3, e.fin() * 15.0F, e.rotation, 50.0F, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3.0F + 1.0F);
            });
        });
    }

    public void drawLight(Bullet b) {
    }
}
