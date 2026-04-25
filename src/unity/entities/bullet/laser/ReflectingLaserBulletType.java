package unity.entities.bullet.laser;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import unity.util.Utils;

public class ReflectingLaserBulletType extends BulletType {
    private static int p = 0;
    private static final Vec2 vec = new Vec2();
    public Color[] colors = new Color[0];
    public float length = 500.0F;
    public float reflectLength = 200.0F;
    public float width = 65.0F;
    public float lengthFalloff = 0.5F;
    public float reflectRange = 80.0F;
    public float reflectLoss = 0.75F;
    public float minimumTargetLength = 70.0F;
    public int reflections = 5;
    public int reflectLightning = 10;

    public ReflectingLaserBulletType(float damage) {
        super(0.0F, damage);
        this.lifetime = 16.0F;
        this.impact = true;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public void init() {
        super.init();
        this.drawSize = this.length * 2.0F;
        this.despawnHit = false;
    }

    public void init(Bullet b) {
        super.init(b);
        b.fdata = b.data == null ? this.length : this.reflectLength;
        if (b.data == null) {
            ReflectLaserData data = new ReflectLaserData();
            data.reflected = new IntSet();
            b.data = data;
        }

        if (b.data instanceof ReflectLaserData) {
            ReflectLaserData data = (ReflectLaserData)b.data;
            float length = b.fdata;
            Vec2 pos = vec.trns(b.rotation(), length).add(b);
            p = 0;
            Utils.collideLineRawEnemy(this.collidesTeam ? null : b.team, b.x, b.y, pos.x, pos.y, this.width / 3.0F, this.collidesTiles, true, true, (x, y, h, direct) -> {
                boolean hit = p > this.pierceCap;
                if (direct && h instanceof Teamc) {
                    Teamc t = (Teamc)h;
                    if (t.team() != b.team) {
                        data.hitX = x;
                        data.hitY = y;
                        data.hit = true;
                        if (h instanceof Hitboxc) {
                            this.hitEntity(b, (Hitboxc)h, h.health());
                        }

                        this.hit(b, x, y);
                        if (!b.within(h, this.minimumTargetLength)) {
                            ++p;
                        }
                    } else {
                        h.heal(h.maxHealth() * (this.healPercent / 100.0F));
                    }
                }

                if (h instanceof Building && ((Building)h).team != b.team) {
                    Building block = (Building)h;
                    hit |= block.block.absorbLasers;
                }

                return hit;
            });
            if (data.hit) {
                Vec2 hit = Intersector.nearestSegmentPoint(b.x, b.y, pos.x, pos.y, data.hitX, data.hitY, Tmp.v2);
                float hx = hit.x;
                float hy = hit.y;
                b.fdata = b.dst(hx, hy);
                if (data.reflect < this.reflections) {
                    float delay = this.lifetime * 0.2F;
                    Posc n = Units.closestTarget(b.team, hx, hy, this.reflectLength, (unit) -> unit.isValid() && this.valid(hx, hy, b.rotation(), data.lastRot, unit, data.reflected), (building) -> this.valid(hx, hy, b.rotation(), data.lastRot, building, data.reflected));
                    float nextAngle = n == null ? b.rotation() + 180.0F + Mathf.range(this.reflectRange / 2.0F, this.reflectRange) : n.angleTo(hx, hy) + 180.0F;
                    ReflectLaserData d = new ReflectLaserData();
                    d.reflect = data.reflect + 1;
                    d.lastRot = (b.rotation() + 360.0F) % 360.0F;
                    d.reflected = data.reflected;
                    Time.run(delay, () -> {
                        if (b.isAdded() && b.type == this) {
                            this.hitReflect(b, hx, hy);
                            this.createAlt(b, hx, hy, nextAngle, this.reflectLength, d);
                        }

                    });
                }
            }
        }

    }

    boolean valid(float x, float y, float angle, float angle2, Posc pos, IntSet collided) {
        float angleTo = pos.angleTo(x, y);
        return !pos.within(x, y, this.minimumTargetLength) && Angles.within(angleTo, angle, this.reflectRange) && (angle2 <= -1.0F || !Angles.within(angleTo + 180.0F, angle2, this.reflectRange / 2.0F)) && collided.add(pos.id());
    }

    void hitReflect(Bullet b, float x, float y) {
        for(int i = 0; i < this.reflectLightning; ++i) {
            Lightning.create(b, this.lightningColor, this.lightningDamage < 0.0F ? this.damage : this.lightningDamage, x, y, b.rotation() + Mathf.range(this.lightningCone / 2.0F) + this.lightningAngle, this.lightningLength + Mathf.random(this.lightningLengthRand));
        }

    }

    public void draw(Bullet b) {
        if (b.data instanceof ReflectLaserData) {
            ReflectLaserData data = (ReflectLaserData)b.data;
            boolean hit = data.hit && data.reflect < this.reflections;
            float len = b.fdata;
            float f = Mathf.curve(b.fin(), 0.0F, 0.2F);
            float cl = len * f;
            float cw = this.width;
            Vec2 p = Tmp.v1.trns(b.rotation(), cl).add(b);
            Lines.line(b.x, b.y, p.x, p.y);

            for(Color color : this.colors) {
                Draw.color(color);
                Lines.stroke(cw * b.fout());
                Lines.line(b.x, b.y, p.x, p.y, false);
                if (!hit) {
                    Drawf.tri(p.x, p.y, Lines.getStroke() * 1.22F, cw * 2.0F + this.width / 2.0F, b.rotation());
                } else {
                    Fill.circle(p.x, p.y, cw * b.fout() / 2.0F);
                }

                Fill.circle(b.x, b.y, cw * b.fout());
                cw *= this.lengthFalloff;
            }

            Tmp.v2.set(p).sub(b).scl(1.1F).add(b);
            Drawf.light(b.team, b.x, b.y, Tmp.v2.x, Tmp.v2.y, this.width * 1.4F * b.fout(), this.colors[0], 0.6F);
        }

    }

    public void drawLight(Bullet b) {
    }

    void createAlt(Bullet s, float x, float y, float rotation, float length, ReflectLaserData data) {
        Bullet b = Bullet.create();
        b.x = x;
        b.y = y;
        b.type = this;
        b.owner = s.owner;
        b.team = s.team;
        b.time = 0.0F;
        b.lifetime = this.lifetime;
        b.initVel(rotation, 0.0F);
        b.fdata = length;
        b.data = data;
        b.drag = 0.0F;
        b.hitSize = this.hitSize;
        b.damage = s.damage * this.reflectLoss;
        b.add();
    }

    static class ReflectLaserData {
        int reflect = 0;
        boolean hit = false;
        float hitX;
        float hitY;
        float lastRot = -1.0F;
        IntSet reflected;
    }
}
