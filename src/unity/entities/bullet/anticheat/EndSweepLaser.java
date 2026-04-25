package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class EndSweepLaser extends AntiCheatBulletTypeBase {
    public float length = 300.0F;
    public float collisionWidth = 3.0F;
    public float widthLoss = 0.7F;
    public float width = 9.0F;
    public float oscScl = 0.8F;
    public float oscMag = 1.5F;
    public float distance = 150.0F;
    public Color[] colors;
    public BulletType hitBullet;
    private static float len;
    private static int pierceIdx;

    public EndSweepLaser(float damage) {
        super(0.0F, damage);
        this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
        this.despawnEffect = Fx.none;
        this.hittable = this.collides = this.absorbable = this.keepVelocity = false;
        this.impact = true;
        this.pierceShields = true;
    }

    public float estimateDPS() {
        return this.damage * (this.lifetime / 2.0F) / 5.0F * 3.0F;
    }

    public float continuousDamage() {
        return this.damage / 5.0F * 60.0F;
    }

    public float range() {
        return this.length;
    }

    public void init() {
        super.init();
        this.despawnHit = false;
        this.drawSize = this.length * 2.0F + 20.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new Vec2();
    }

    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        if (b.data instanceof Vec2) {
            Vec2 v = (Vec2)b.data;
            if (v.dst(x, y) > this.distance) {
                v.set(x, y);
                this.hitBullet.create(b.owner, b.team, x, y, b.rotation());
            }
        }

    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F)) {
            len = this.length;
            pierceIdx = this.pierce ? this.pierceCap : 0;
            Vec2 v = Tmp.v1.trns(b.rotation(), len).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, v.x, v.y, this.collisionWidth, (building, direct) -> {
                if (direct) {
                    --pierceIdx;
                    if (pierceIdx <= 0) {
                        len = b.dst(building);
                    }

                    this.hitBuildingAntiCheat(b, building);
                }

                return pierceIdx <= 0;
            }, (unit) -> {
                --pierceIdx;
                if (pierceIdx <= 0) {
                    len = b.dst(unit);
                }

                this.hitUnitAntiCheat(b, unit);
                return pierceIdx <= 0;
            }, (ex, ey) -> this.hit(b, ex, ey), true);
            b.fdata = len;
        }

    }

    public void drawLight(Bullet b) {
    }

    public void draw(Bullet b) {
        Vec2 v = Tmp.v1.trns(b.rotation(), b.fdata).add(b);
        float fin = Mathf.clamp(b.time / 16.0F) * Mathf.clamp(b.time > b.lifetime - 16.0F ? 1.0F - (b.time - (b.lifetime - 16.0F)) / 16.0F : 1.0F);
        float w = (this.width + Mathf.absin(this.oscScl, this.oscMag)) * fin;

        for(Color c : this.colors) {
            Draw.color(c);
            Lines.stroke(w);
            Lines.line(b.x, b.y, v.x, v.y, false);
            Drawf.tri(b.x, b.y, w * 1.22F, this.width * 2.0F, b.rotation() + 180.0F);
            Drawf.tri(v.x, v.y, w * 1.22F, this.width * 3.0F + w * 2.0F, b.rotation());
            w *= this.widthLoss;
        }

    }
}
