package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntIntMap;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import unity.content.effects.SpecialFx;
import unity.graphics.UnityBlending;
import unity.graphics.UnityPal;
import unity.util.BasicPool;
import unity.util.Utils;

public class VoidPortalBulletType extends AntiCheatBulletTypeBase {
    public float length = 800.0F;
    public float width = 95.0F;
    public float fadeInTime = 180.0F;
    public float fadeOutTime = 20.0F;
    public float tentacleRange = 500.0F;
    public float tentacleWidth = 7.0F;
    public float tentaclePull = 3.0F;
    public float tentacleOutOfRangeStrength = 0.0125F;
    public float tentacleRangeReduction = 4.0F;
    public float tentacleDamage = 50.0F;
    private static final IntSet collided = new IntSet(102);
    private static final BasicPool<VoidTentacle> tentaclePool = new BasicPool(8, 250, VoidTentacle::new);

    public VoidPortalBulletType(float damage) {
        super(0.0F, damage);
        this.lifetime = 240.0F;
        this.collides = this.hittable = this.absorbable = this.keepVelocity = false;
        this.pierce = this.pierceShields = true;
        this.despawnEffect = Fx.none;
    }

    public void init() {
        super.init();
        this.drawSize = this.length * 2.0F;
    }

    public float range() {
        return this.length;
    }

    public float estimateDPS() {
        return this.damage * (this.lifetime / 2.0F) / 5.0F * 3.0F;
    }

    public float continuousDamage() {
        return this.damage / 5.0F * 60.0F;
    }

    public void update(Bullet b) {
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeOutTime ? 1.0F - (b.time - (this.lifetime - this.fadeOutTime)) / this.fadeOutTime : 1.0F);
        float fin = b.time < this.fadeInTime ? Mathf.clamp(b.time / this.fadeInTime) : 1.0F;
        float fin2 = Mathf.curve(b.fin(), 0.0F, 15.0F / this.lifetime);
        Vec2 end = Tmp.v1.trns(b.rotation(), this.length * fin2).add(b);
        Vec2 mid = Tmp.v2.set(end).sub(b).scl(0.5F).add(b);
        Vec2 s = Tmp.v3.trns(b.rotation() - 90.0F, this.width * fin * fout);
        Effect.shake(5.0F * fin, 5.0F * fin, mid);
        if (b.timer(0, 5.0F)) {
            float ex = end.x;
            float ey = end.y;
            float mx = mid.x;
            float my = mid.y;
            float sx = s.x;
            float sy = s.y;
            collided.clear();
            Utils.inTriangleBuilding(b.team, true, b.x, b.y, mx + sx, my + sy, mx - sx, my - sy, (building) -> collided.add(building.id), (building) -> {
                this.hit(b, building.x, building.y);
                this.hitBuildingAntiCheat(b, building);
            });
            Utils.inTriangleBuilding(b.team, true, mx + sx, my + sy, mx - sx, my - sy, ex, ey, (building) -> collided.add(building.id), (building) -> {
                this.hit(b, building.x, building.y);
                this.hitBuildingAntiCheat(b, building);
            });
            Utils.inTriangle(Groups.unit, b.x, b.y, mx + sx, my + sy, mx - sx, my - sy, (u) -> u.team != b.team && collided.add(u.id), (u) -> {
                this.hit(b, u.x, u.y);
                this.hitUnitAntiCheat(b, u);
            });
            Utils.inTriangle(Groups.unit, mx + sx, my + sy, mx - sx, my - sy, ex, ey, (u) -> u.team != b.team && collided.add(u.id), (u) -> {
                this.hit(b, u.x, u.y);
                this.hitUnitAntiCheat(b, u);
            });
        }

        if (b.data instanceof VoidPortalData) {
            VoidPortalData data = (VoidPortalData)b.data;
            if (Mathf.chanceDelta((double)0.2F)) {
                Tmp.v1.set(b).sub(mid);
                float l = Mathf.range(1.0F);
                float o = Mathf.range(1.0F) * (1.0F - Math.abs(l));
                float x = Tmp.v1.x * l + mid.x + s.x * o;
                float y = Tmp.v1.y * l + mid.y + s.y * o;
                Unit unit = Units.bestEnemy(b.team, x, y, this.tentacleRange, Healthc::isValid, (u, sxx, syx) -> (float)data.map.get(u.id, 0) + b.dst(u) / (this.tentacleRange * 2.0F));
                if (unit != null) {
                    VoidTentacle t = (VoidTentacle)tentaclePool.obtain();
                    t.set(unit, x, y, this.tentacleRange);
                    data.tentacles.add(t);
                    data.map.put(unit.id, data.map.get(unit.id, 0) + 1);
                }
            }

            data.tentacles.removeAll((tx) -> tx.update(b, this, mid.x, mid.y));
        }

    }

    public void draw(Bullet b) {
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeOutTime ? 1.0F - (b.time - (b.lifetime - this.fadeOutTime)) / this.fadeOutTime : 1.0F);
        float fin = b.time < this.fadeInTime ? Mathf.clamp(b.time / this.fadeInTime) : 1.0F;
        float fin2 = Mathf.curve(b.fin(), 0.0F, 15.0F / this.lifetime);
        Vec2 end = Tmp.v1.trns(b.rotation(), this.length * fin2).add(b);
        Vec2 mid = Tmp.v2.set(end).sub(b).scl(0.5F).add(b);
        Vec2 s = Tmp.v3.trns(b.rotation() - 90.0F, this.width * fin * fout);
        float z = Draw.z();
        Draw.z(89.9999F);
        Draw.color(UnityPal.scarColor);
        Draw.blend(UnityBlending.shadowRealm);
        Fill.tri(b.x, b.y, mid.x + s.x, mid.y + s.y, mid.x - s.x, mid.y - s.y);
        Fill.tri(end.x, end.y, mid.x + s.x, mid.y + s.y, mid.x - s.x, mid.y - s.y);
        Draw.blend();
        Draw.z(115.001F);
        if (b.data instanceof VoidPortalData) {
            Draw.color(Color.black);

            for(VoidTentacle t : ((VoidPortalData)b.data).tentacles) {
                t.draw(this.tentacleWidth, fout);
            }
        }

        Draw.z(z);
        Draw.color();
    }

    public void drawLight(Bullet b) {
    }

    public void removed(Bullet b) {
        super.removed(b);
        if (b.data instanceof VoidPortalData) {
            VoidPortalData data = (VoidPortalData)b.data;

            for(VoidTentacle tentacle : data.tentacles) {
                tentaclePool.free(tentacle);
            }
        }

    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new VoidPortalData();
    }

    static class VoidPortalData {
        Seq<VoidTentacle> tentacles = new Seq();
        IntIntMap map = new IntIntMap(102);
    }

    static class VoidTentacle implements Pool.Poolable {
        float x;
        float y;
        float randLen;
        float randAng;
        float length;
        float time;
        float timer;
        Unit unit;
        boolean side = Mathf.randomBoolean();

        void set(Unit unit, float x, float y, float length) {
            this.unit = unit;
            this.x = x;
            this.y = y;
            this.length = length;
            this.randAng = Mathf.range(360.0F);
            this.randLen = Mathf.random(unit.hitSize / 3.0F);
            this.side = Mathf.randomBoolean();
        }

        public void reset() {
            this.unit = null;
            this.x = this.y = this.randLen = this.randAng = this.length = this.time = this.timer = 0.0F;
        }

        boolean update(Bullet b, VoidPortalBulletType type, float cx, float cy) {
            if (this.unit.isValid()) {
                this.time = Math.min(20.0F, this.time + Time.delta);
                this.timer += Time.delta;
                float tx = this.unit.x + Angles.trnsx(this.unit.rotation + this.randAng, this.randLen);
                float ty = this.unit.y + Angles.trnsy(this.unit.rotation + this.randAng, this.randLen);
                Tmp.v1.set(this.x, this.y).sub(tx, ty).nor();
                float mx = Tmp.v1.x;
                float my = Tmp.v1.y;
                float scl = type.tentaclePull;
                float dst = Mathf.dst(this.x, this.y, tx, ty);
                if (dst > this.length) {
                    float s = (dst - this.length) * type.tentacleOutOfRangeStrength;
                    if (this.timer >= 5.0F) {
                        boolean wasDead = this.unit.dead;
                        type.hitUnitAntiCheat(b, this.unit, -b.damage + type.tentacleDamage * s);
                        if (this.unit.dead && !wasDead) {
                            if (this.unit.isAdded()) {
                                this.unit.destroy();
                            }

                            if (Vars.renderer.animateShields) {
                                SpecialFx.fragmentationFast.at(this.unit.x, this.unit.y, this.unit.angleTo(cx, cy) + 180.0F, this.unit);
                            }
                        }

                        this.timer = 0.0F;
                    }

                    scl += s;
                } else {
                    this.length = Math.max(dst, this.length - (this.length - dst) * type.tentacleOutOfRangeStrength * Time.delta);
                }

                this.length = Math.max(0.0F, this.length - type.tentacleRangeReduction * Time.delta);
                scl *= 20.0F;
                this.unit.impulse(mx * scl * Time.delta, my * scl * Time.delta);
            } else {
                this.time -= Time.delta;
            }

            return this.time < 0.0F && !this.unit.isValid();
        }

        void draw(float width, float fout) {
            float fin = this.time / 20.0F * fout;
            float tx = this.unit.x + Angles.trnsx(this.unit.rotation + this.randAng, this.randLen);
            float ty = this.unit.y + Angles.trnsy(this.unit.rotation + this.randAng, this.randLen);
            int res = 16;
            float dst = Mathf.dst(this.x, this.y, tx, ty) / (float)res * Mathf.clamp(this.time / 13.0F);
            float angle = Angles.angle(this.x, this.y, tx, ty);
            Tmp.v1.set(this.x, this.y);
            float w = width * fin;

            for(int i = 0; i < res; ++i) {
                float bend = (1.0F - fin) * (360.0F / (float)res) * (float)Mathf.sign(this.side) * (float)i;
                float lx = Tmp.v1.x;
                float ly = Tmp.v1.y;
                float w2 = w - w * ((float)i / (float)res) / 1.5F;
                Vec2 v = Tmp.v1.add(Tmp.v2.trns(bend + angle, dst));
                if (i == 0) {
                    Fill.circle(lx, ly, w2 / 2.0F);
                }

                Lines.stroke(w2);
                Lines.line(lx, ly, v.x, v.y, false);
                Fill.circle(v.x, v.y, w2 / 2.0F);
            }

        }
    }
}
