package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.util.Tmp;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class DesolationBulletType extends AntiCheatBulletTypeBase {
    protected float health = 1500000.0F;
    protected float maxDamage = 5000.0F;
    protected float bulletDamage = 43.0F;
    protected float length = 70.0F;
    protected float widthFrom = 180.0F;
    protected float widthTo = 230.0F;
    protected float offset = 1.75F;
    protected float startingScl = 1.4F;
    protected float scaleReduction = 0.8F;
    protected float fadeOutTime = 20.0F;
    protected float fadeInTime = 40.0F;
    public Color[] colors;

    public DesolationBulletType(float speed, float damage) {
        super(speed, damage);
        this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
        this.hittable = false;
        this.absorbable = false;
        this.collides = false;
        this.pierce = true;
        this.pierceShields = true;
        this.impact = true;
        this.keepVelocity = false;
        this.knockback = 5.0F;
    }

    public float continuousDamage() {
        return this.damage / 5.0F * 60.0F;
    }

    public float estimateDPS() {
        return this.damage * 100.0F / 5.0F * 3.0F;
    }

    public void init() {
        super.init();
        this.despawnHit = false;
        this.drawSize = Math.max(Math.max(this.widthTo, this.widthFrom), this.length) * 2.0F * this.startingScl;
    }

    public void init(Bullet b) {
        super.init(b);
        DesolationBulletData data = new DesolationBulletData();
        data.health = this.health;
        b.data = data;
    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F) && b.data instanceof DesolationBulletData) {
            DesolationBulletData d = (DesolationBulletData)b.data;
            d.collided.clear();
            float width = Mathf.lerp(this.widthFrom, this.widthTo, Mathf.clamp(b.time / this.fadeInTime));
            float in = d.health / this.health;
            float out = Mathf.clamp(b.time > b.lifetime - this.fadeOutTime ? 1.0F - (b.time - (this.lifetime - this.fadeOutTime)) / this.fadeOutTime : 1.0F);
            Vec2 v1 = Tmp.v1.trns(b.rotation(), this.length / 2.0F);

            for(int s : Mathf.signs) {
                Vec2 v2 = Tmp.v2.trns(b.rotation() - 90.0F, width * in * (float)s * out, -(this.length / 2.0F) * this.offset).add(b);
                float x1 = b.x + v1.x;
                float x2 = b.x - v1.x;
                float y1 = b.y + v1.y;
                float y2 = b.y - v1.y;
                Utils.inTriangle(Groups.unit, x1, y1, x2, y2, v2.x, v2.y, (u) -> u.team != b.team && !u.dead && d.collided.add(u.id), (u) -> {
                    d.health -= Math.min(u.health, this.maxDamage);
                    this.hitUnitAntiCheat(b, u);
                    Vec2 p = Intersector.nearestSegmentPoint(x2, y2, v2.x, v2.y, u.x, u.y, Tmp.v3);
                    p.sub(u).limit(u.hitSize / 2.0F).add(u);
                    this.hit(b, p.x, p.y);
                });
                Utils.inTriangle(Groups.bullet, x1, y1, x2, y2, v2.x, v2.y, (bb) -> bb.team != b.team && bb.type.hittable && d.collided.add(bb.id), (bb) -> {
                    bb.time = Mathf.clamp(bb.time + this.bulletDamage / bb.type.damage, 0.0F, bb.lifetime);
                    Tmp.v3.trns(Mathf.slerp(b.rotation(), b.angleTo(bb), 0.5F), this.knockback / (bb.hitSize / 2.0F)).add(bb.vel).scl(Mathf.clamp(1.0F / (this.bulletDamage / bb.type.damage), 0.0F, 0.9F)).limit(bb.type.speed);
                    bb.vel.set(Tmp.v3);
                });
                Utils.inTriangleBuilding(b.team, true, x1, y1, x2, y2, v2.x, v2.y, (build) -> d.collided.add(build.id), (build) -> {
                    d.health -= Math.min(build.health, this.maxDamage);
                    this.hitBuildingAntiCheat(b, build);
                    Vec2 p = Intersector.nearestSegmentPoint(x2, y2, v2.x, v2.y, build.x, build.y, Tmp.v3);
                    float sz = (float)(build.block.size * 8) / 2.0F;
                    p.x = Mathf.clamp(p.x, build.x - sz, build.x + sz);
                    p.y = Mathf.clamp(p.y, build.y - sz, build.y + sz);
                    this.hit(b, p.x, p.y);
                });
            }

            if (d.health <= 0.0F) {
                b.remove();
            }
        }

    }

    public void draw(Bullet b) {
        if (b.data instanceof DesolationBulletData) {
            DesolationBulletData d = (DesolationBulletData)b.data;
            float width = Mathf.lerp(this.widthFrom, this.widthTo, Mathf.clamp(b.time / this.fadeInTime));
            float in = d.health / this.health;
            Vec2 v1 = Tmp.v1.trns(b.rotation(), this.length / 2.0F);
            float scl = this.startingScl;
            float out = Mathf.clamp(b.time > b.lifetime - this.fadeOutTime ? 1.0F - (b.time - (this.lifetime - this.fadeOutTime)) / this.fadeOutTime : 1.0F);

            for(Color c : this.colors) {
                Draw.color(c);
                float rx = Mathf.range(2.0F);
                float ry = Mathf.range(2.0F);

                for(int s : Mathf.signs) {
                    Vec2 v2 = Tmp.v2.trns(b.rotation() - 90.0F, width * in * (float)s * out, -(this.length / 2.0F) * this.offset);
                    float x1 = b.x + v1.x * scl;
                    float x2 = b.x - v1.x;
                    float x3 = b.x + v2.x * scl;
                    float y1 = b.y + v1.y * scl;
                    float y2 = b.y - v1.y;
                    float y3 = b.y + v2.y * scl;
                    Fill.tri(x1 + rx, y1 + ry, x2 + rx, y2 + ry, x3 + rx, y3 + ry);
                }

                scl *= this.scaleReduction;
            }

            Draw.reset();
        }

    }

    public void drawLight(Bullet b) {
    }

    static class DesolationBulletData {
        IntSet collided = new IntSet(103);
        float health = 0.0F;
    }
}
