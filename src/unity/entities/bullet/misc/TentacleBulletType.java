package unity.entities.bullet.misc;

import arc.func.Cons;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import unity.util.Utils;

public class TentacleBulletType extends BulletType {
    public float length = 100.0F;
    public float width = 2.0F;
    public int segments = 8;
    public float angleVelocity = 8.0F;
    public float angleDrag = 0.1F;
    public float angularVelocityInherit = 0.2F;
    public Color fromColor;
    public Color toColor;
    protected Cons<Building> hitBuilding;
    protected Cons<Unit> hitUnit;
    private boolean hit;

    public TentacleBulletType(float damage) {
        this.fromColor = Color.white;
        this.toColor = Color.white;
        this.damage = damage;
        this.speed = 0.001F;
        this.lifetime = 20.0F;
        this.pierce = true;
        this.despawnEffect = Fx.none;
        this.keepVelocity = false;
        this.hittable = false;
        this.absorbable = false;
    }

    public float range() {
        return this.length / 1.4F;
    }

    public float estimateDPS() {
        return this.damage * 100.0F / 5.0F * 3.0F;
    }

    public float continuousDamage() {
        return this.damage / 5.0F * 60.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        int sign = Mathf.signs[Mathf.randomSeed((long)b.id, 0, 1)];
        float ang = 360.0F / (float)this.segments;
        TentacleBulletData data = new TentacleBulletData();
        TentacleNode[] nodes = data.nodes = new TentacleNode[this.segments];
        if (b.owner instanceof Position) {
            data.offsetX = b.x - ((Position)b.owner).getX();
            data.offsetY = b.y - ((Position)b.owner).getY();
        }

        data.length = Damage.findLaserLength(b, this.length);
        Position last = b;

        for(int i = 0; i < nodes.length; ++i) {
            float av = (((float)i + 1.0F) / (float)nodes.length + (i == 0 ? 1.0F - 1.0F / (float)nodes.length : 0.0F)) * (i == 0 ? -1.0F : 1.0F);
            TentacleNode node = new TentacleNode();
            node.angle = ang * (float)i * (float)sign + b.rotation() + (float)sign * 90.0F + 180.0F;
            node.angularVelocity = this.angleVelocity * av * (float)(-sign);
            node.pos.set(last);
            last = node.pos;
            nodes[i] = node;
        }

        b.data = data;
    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof TentacleBulletData) {
            TentacleBulletData data = (TentacleBulletData)var3;
            TentacleNode[] nodes = data.nodes;
            float[] tmp = new float[8];
            float out = 1.0F - Mathf.clamp(b.fout() * 2.0F);
            Position last = b;
            float lastRot = nodes[0].angle;
            Draw.color(this.fromColor, this.toColor, out);
            int ix = 0;

            for(int sign : Mathf.signs) {
                Tmp.v1.trns(lastRot - 90.0F, this.width / 2.0F * Mathf.clamp(b.fout() * 2.0F) * (float)sign).add(last);
                tmp[ix++] = Tmp.v1.x;
                tmp[ix++] = Tmp.v1.y;
            }

            Tmp.v1.trns(lastRot + 180.0F, this.width).add(last);
            tmp[ix++] = Tmp.v1.x;
            tmp[ix] = Tmp.v1.y;
            Fill.tri(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5]);

            for(int i = 0; i < nodes.length; ++i) {
                int idx = 0;
                TentacleNode node = nodes[i];
                float scl = ((float)nodes.length - (float)i) / (float)nodes.length * (this.width / 2.0F) * Mathf.clamp(b.fout() * 2.0F);
                float sclB = ((float)nodes.length - ((float)i + 1.0F)) / (float)nodes.length * (this.width / 2.0F) * Mathf.clamp(b.fout() * 2.0F);

                for(int sign : Mathf.signs) {
                    Tmp.v1.trns(lastRot - 90.0F, scl * (float)sign).add(last);
                    tmp[idx++] = Tmp.v1.x;
                    tmp[idx++] = Tmp.v1.y;
                }

                for(int sign : Mathf.signs) {
                    Tmp.v1.trns(node.angle - 90.0F, sclB * (float)(-sign)).add(node.pos);
                    tmp[idx++] = Tmp.v1.x;
                    tmp[idx++] = Tmp.v1.y;
                }

                last = node.pos;
                lastRot = node.angle;
                Fill.quad(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5], tmp[6], tmp[7]);
            }

            Draw.reset();
        }

    }

    public void drawLight(Bullet b) {
    }

    public void update(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof TentacleBulletData) {
            TentacleBulletData data = (TentacleBulletData)var3;
            TentacleNode[] nodes = data.nodes;
            Position last = b;
            int next = 1;
            if (b.owner instanceof Position) {
                b.x = ((Position)b.owner).getX() + data.offsetX;
                b.y = ((Position)b.owner).getY() + data.offsetY;
            }

            for(TentacleNode node : nodes) {
                node.angle += node.angularVelocity * Time.delta;
                node.pos.trns(node.angle, this.length / (float)this.segments * b.fin()).add(last);
                if (next < nodes.length) {
                    nodes[next].angularVelocity += node.angularVelocity * this.angularVelocityInherit;
                }

                node.angularVelocity *= 1.0F - this.angleDrag * Time.delta;
                last = node.pos;
                ++next;
            }

            if (b.timer(1, 5.0F)) {
                last = b;
                this.hit = false;

                for(TentacleNode node : nodes) {
                    Utils.collideLineRaw(last.getX(), last.getY(), node.pos.x, node.pos.y, 3.0F, (bu) -> bu.team != b.team && !this.hit, (un) -> un.team != b.team && !this.hit, (build, direct) -> {
                        if (direct) {
                            if (this.hitBuilding != null) {
                                this.hitBuilding.get(build);
                            }

                            build.damage(this.damage * Mathf.clamp(b.fout() * 2.0F) * this.buildingDamageMultiplier);
                        }

                        if (build.block.absorbLasers) {
                            this.hit = true;
                        }

                        return build.block.absorbLasers;
                    }, (unit) -> {
                        if (this.hitUnit != null) {
                            this.hitUnit.get(unit);
                        }

                        unit.damage(this.damage * Mathf.clamp(b.fout() * 2.0F));
                        unit.apply(this.status, this.statusDuration);
                    }, (Floatf)null, (ex, ey) -> this.hitEffect.at(ex, ey, node.angle));
                    last = node.pos;
                }
            }
        }

    }

    static class TentacleNode {
        Vec2 pos = new Vec2();
        float angularVelocity = 0.0F;
        float angle = 0.0F;
    }

    static class TentacleBulletData {
        TentacleNode[] nodes;
        float offsetX;
        float offsetY;
        float length;
    }
}
