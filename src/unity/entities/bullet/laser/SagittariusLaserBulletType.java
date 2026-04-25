package unity.entities.bullet.laser;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Physicsc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.content.UnityStatusEffects;
import unity.content.effects.HitFx;
import unity.util.Utils;

public class SagittariusLaserBulletType extends BulletType {
    private static boolean hit;
    private static float hitX;
    private static float hitY;
    public Color[] colors;
    public float length;
    public float width;
    public int lasers;

    public SagittariusLaserBulletType(float damage) {
        super(0.0F, damage);
        this.colors = new Color[]{Pal.heal.cpy().a(0.2F), Pal.heal.cpy().a(0.5F), Pal.heal.cpy().mul(1.2F), Color.white};
        this.length = 550.0F;
        this.width = 45.0F;
        this.lasers = 8;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
        this.hitEffect = HitFx.coloredHitSmall;
        this.hitColor = Pal.heal;
    }

    public float range() {
        return this.length;
    }

    public void init() {
        super.init();
        this.despawnHit = false;
        this.drawSize = this.length * 2.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new SagittariusLaserData();
    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F) && b.data instanceof SagittariusLaserData) {
            SagittariusLaserData data = (SagittariusLaserData)b.data;
            float fin = b.time < 200.0F ? b.time / 200.0F : 1.0F;
            float sfn = b.time < 60.0F ? b.time / 60.0F : 1.0F;
            float fn = sfn + fin / 15.0F;
            float w = this.width * sfn / 3.0F;
            if (b.owner instanceof Physicsc) {
                Physicsc owner = (Physicsc)b.owner;
                Tmp.v1.trns(b.rotation() + 180.0F, 25.0F * fin);
                owner.impulse(Tmp.v1);
            }

            for(int i = 0; i < this.lasers; ++i) {
                float ang = (float)i * 360.0F / (float)this.lasers;
                float time = b.time * 2.0F;
                float sin = Mathf.sinDeg(time + ang);
                float cos = Mathf.cosDeg(time + ang);
                Vec2 p = Tmp.v1.trns(b.rotation() + 90.0F, sin * 9.0F * fin, cos * 4.0F * fin).add(b);
                Vec2 end = Tmp.v2.trns(b.rotation() + sin * 20.0F * fin, this.length).add(p);
                hit = false;
                Utils.collideLineRawEnemy(!this.collidesTeam ? b.team : null, p.x, p.y, end.x, end.y, w, (h) -> h != b.owner && (!(h instanceof Unit) || ((Unit)h).team != b.team), (building, direct) -> {
                    if (direct) {
                        if (building.team != b.team) {
                            building.damage(this.damage * fn * this.buildingDamageMultiplier);
                        } else {
                            building.heal(building.maxHealth * (this.healPercent / 100.0F));
                        }

                        hit = building.block.absorbLasers;
                        hitX = building.x;
                        hitY = building.y;
                    }

                    return building.block.absorbLasers;
                }, (unit) -> {
                    if (unit.team != b.team) {
                        unit.damage(this.damage * fn);
                        Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F * fn);
                        unit.impulse(Tmp.v3);
                        unit.apply(this.status, this.statusDuration);
                    }

                    return false;
                }, (ex, ey) -> this.hit(b, ex, ey), true);
                data.length[i] = this.length;
                if (hit) {
                    data.length[i] = b.dst(hitX, hitY);
                }
            }
        }

    }

    public void despawned(Bullet b) {
        super.despawned(b);
        if (b.owner instanceof Unit) {
            ((Unit)b.owner).apply(UnityStatusEffects.speedFatigue, 360.0F);
        }

    }

    public void draw(Bullet b) {
        if (b.data instanceof SagittariusLaserData) {
            SagittariusLaserData data = (SagittariusLaserData)b.data;
            float cw = this.width + Mathf.absin(0.8F, 1.5F);
            float fout = Mathf.clamp((b.lifetime - b.time) / 16.0F);
            float fin = b.time < 200.0F ? b.time / 200.0F : 1.0F;
            float sfn = b.time < 60.0F ? b.time / 60.0F : 1.0F;

            for(Color color : this.colors) {
                float w = cw * (sfn + fin / 15.0F) * fout;

                for(int i = 0; i < this.lasers; ++i) {
                    Draw.color(color);
                    float length = data.length[i];
                    float ang = (float)i * 360.0F / (float)this.lasers;
                    float time = b.time * 2.0F;
                    float sin = Mathf.sinDeg(time + ang);
                    float cos = Mathf.cosDeg(time + ang);
                    float rot = b.rotation() + sin * 20.0F * fin;
                    Vec2 p = Tmp.v1.trns(b.rotation() + 90.0F, sin * 9.0F * fin, cos * 4.0F * fin).add(b);
                    Vec2 end = Tmp.v2.trns(rot, length).add(p);
                    Lines.stroke(w);
                    Lines.line(p.x, p.y, end.x, end.y, false);
                    Drawf.tri(end.x, end.y, Lines.getStroke() * 1.22F, cw * 3.0F + this.width / 1.5F, rot);
                    Draw.color(Tmp.c1.set(color).a(Mathf.pow(color.a, (float)this.lasers / 3.0F)));
                    Fill.circle(p.x, p.y, w);
                    if (color == this.colors[0]) {
                        Drawf.light(b.team, p.x, p.y, end.x, end.y, w * 1.7F * b.fout(), this.colors[0], 0.6F);
                    }
                }

                cw *= 0.5F;
            }
        }

    }

    public void drawLight(Bullet b) {
    }

    class SagittariusLaserData {
        float[] length;

        SagittariusLaserData() {
            this.length = new float[SagittariusLaserBulletType.this.lasers];
        }
    }
}
