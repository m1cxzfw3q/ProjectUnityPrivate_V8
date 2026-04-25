package unity.entities.bullet.monolith.energy;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import unity.gen.Float2;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class JoiningBulletType extends BulletType {
    public float sensitivity = 0.2F;
    public float joinDelay = 0.0F;
    public float attractMaxSpeed = -1.0F;
    public float maxDamage = -1.0F;
    public float yieldScl = 1.25F;
    public float joinCone = 60.0F;
    public float joinRange = 16.0F;
    public float minDot = 0.2F;
    public float radius = 10.0F;
    public Color[] colors;
    public Color edgeColor;
    public Color centerColor;
    public Effect joinEffect;
    private static int lastID;
    private final int identifier;
    private static Bullet lastBullet;
    private static float lastScore;

    public JoiningBulletType(float speed, float damage) {
        super(speed, damage);
        this.colors = new Color[]{UnityPal.monolithGreenLight, UnityPal.monolithGreen, UnityPal.monolithGreenDark};
        this.edgeColor = UnityPal.monolithGreenLight.cpy().a(0.8F);
        this.centerColor = UnityPal.monolithGreenDark.cpy().a(0.0F);
        this.joinEffect = Fx.none;
        this.identifier = lastID++;
    }

    public float bulletRadius(Bullet b) {
        return 0.84F + 0.16F * (b.damage / this.damage);
    }

    public void init() {
        if (this.attractMaxSpeed == -1.0F) {
            this.attractMaxSpeed = this.speed * 2.5F;
        }

        if (this.maxDamage == -1.0F) {
            this.maxDamage = this.damage * 4.0F;
        }

        super.init();
    }

    public void init(Bullet b) {
        super.init(b);
        JoinData data = new JoinData();
        data.bullet = b;
        b.data = data;
    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        float r = this.radius * this.bulletRadius(b);
        float start = this.radius * 0.8F;
        float stroke = 2.0F;
        float z = 89.99F;
        Lines.stroke(stroke);
        TextureRegion reg = Core.atlas.white();
        TextureRegion light = Core.atlas.find("unity-line-shade");
        Fill.light(b.x, b.y, Lines.circleVertices(r), r, this.centerColor, this.edgeColor);
        int startAmount = Math.max(Mathf.round((r - start) / stroke), 0);
        int amount = Math.max(Mathf.round(r / stroke), 1);

        for(int i = startAmount; i < amount; ++i) {
            Draw.color(this.colors[Mathf.randomSeed((long)(b.id - i), 0, this.colors.length - 1)]);
            float sr = stroke + (float)i * stroke;
            Mathf.rand.setSeed((long)(b.id + i));
            Quat var10000 = Utils.q1;
            Vec3 var10001 = Tmp.v31;
            Vec3 var10002;
            switch (Mathf.randomSeed((long)b.id * 2L, 0, 2)) {
                case 0:
                    var10002 = Vec3.X;
                    break;
                case 1:
                    var10002 = Vec3.Y;
                    break;
                default:
                    var10002 = Vec3.Z;
            }

            var10000.set(var10001.set(var10002).setToRandomDirection(), Time.time * 6.0F + Mathf.randomSeed((long)(b.id + i) * 4L, 0.0F, 1000.0F));
            UnityDrawf.panningCircle(reg, b.x, b.y, 1.0F, 1.0F, sr, 360.0F, 0.0F, Utils.q1, true, z, z);
            Draw.color(Draw.getColor(), Color.black, 0.33F);
            Draw.blend(Blending.additive);
            UnityDrawf.panningCircle(light, b.x, b.y, 5.0F, 5.0F, sr, 360.0F, 0.0F, Utils.q1, true, z, z);
            Draw.blend();
        }

        Draw.reset();
    }

    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        this.hitEffect.at(x, y, b.rotation(), this.hitColor, Float2.construct(this.radius, this.bulletRadius(b)));
    }

    public void despawned(Bullet b) {
        super.despawned(b);
        this.despawnEffect.at(b.x, b.y, b.rotation(), this.hitColor, Float2.construct(this.radius, this.bulletRadius(b)));
    }

    public void removed(Bullet b) {
        super.removed(b);
        b.trail = null;
    }

    public void update(Bullet b) {
        if (b.isAdded()) {
            lastBullet = null;
            lastScore = 0.0F;
            Groups.bullet.intersect(b.x - this.joinRange, b.y - this.joinRange, 2.0F * this.joinRange, 2.0F * this.joinRange, (e) -> {
                if (e.isAdded() && e != b) {
                    float dot = 0.0F;
                    if (e.damage < this.maxDamage && e.team == b.team) {
                        if (lastBullet != null) {
                            BulletType type$temp = e.type;
                            if (!(type$temp instanceof JoiningBulletType)) {
                                return;
                            }

                            JoiningBulletType type = (JoiningBulletType)type$temp;
                            if (type.identifier != this.identifier || !Angles.within(b.rotation(), e.rotation(), this.joinCone) || !((dot = b.vel.dot(e.vel)) >= this.minDot) || !(lastScore < dot)) {
                                return;
                            }
                        }

                        lastBullet = e;
                        lastScore = dot;
                    }

                }
            });
            JoinData data = (JoinData)b.data;
            if (lastBullet == null) {
                data.target = null;
            } else if (data.target == null) {
                data.target = lastBullet;
                data.rotation = b.rotation();
            } else {
                data.target = lastBullet;
            }

            Bullet t = data.target;
            if (t != null) {
                b.hitbox(Tmp.r1);
                t.hitbox(Tmp.r2);
                if (Tmp.r1.overlaps(Tmp.r2)) {
                    Effect bd = this.despawnEffect;
                    Effect td = t.type.despawnEffect;
                    this.despawnEffect = this.joinEffect;
                    t.type.despawnEffect = this.joinEffect;
                    b.remove();
                    t.remove();
                    this.despawnEffect = bd;
                    t.type.despawnEffect = td;
                    Object var8 = t.data;
                    JoinData var10000;
                    if (var8 instanceof JoinData) {
                        JoinData d = (JoinData)var8;
                        var10000 = d;
                    } else {
                        var10000 = null;
                    }

                    JoinData other = var10000;
                    float bt = b.fout();
                    float tt = t.fout();
                    Bullet n = this.create(b.owner == null ? t.owner : b.owner, b.team, (b.x + t.x) / 2.0F, (b.y + t.y) / 2.0F, Mathf.slerp(data.rotation, other != null ? other.rotation : t.rotation(), Mathf.clamp(t.vel.len() / b.vel.len() / 2.0F)), Math.max(b.damage, t.damage) + Math.min(b.damage, t.damage) * this.yieldScl, 1.0F, Math.max(bt, tt) + Math.min(bt, tt) / 2.0F, (Object)null);
                    n.hitSize *= this.bulletRadius(n);
                }

                if (b.time >= this.joinDelay) {
                    float len = b.vel.len();
                    b.vel.add(Tmp.v1.set(t).sub(b).setLength(this.sensitivity * Time.delta * 0.3F));
                    b.vel.limit(Math.max(len, this.speed * 2.5F));
                }
            } else if (this.homingPower > 1.0E-4F && b.time >= this.homingDelay) {
                Teamc target;
                if (this.healPercent > 0.0F) {
                    target = Units.closestTarget((Team)null, b.x, b.y, this.homingRange, (e) -> e.checkTarget(this.collidesAir, this.collidesGround) && e.team != b.team && !b.hasCollided(e.id), (e) -> this.collidesGround && (e.team != b.team || e.damaged()) && !b.hasCollided(e.id));
                } else {
                    target = Units.closestTarget(b.team, b.x, b.y, this.homingRange, (e) -> e.checkTarget(this.collidesAir, this.collidesGround) && !b.hasCollided(e.id), (e) -> this.collidesGround && !b.hasCollided(e.id));
                }

                if (target != null) {
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), this.homingPower * Time.delta * 50.0F));
                }
            }

            this.updateTrail(b);
            if (this.weaveMag > 0.0F) {
                b.vel.rotate(Mathf.sin(b.time + (float)Math.PI * this.weaveScale / 2.0F, this.weaveScale, this.weaveMag * (float)(Mathf.randomSeed((long)b.id, 0, 1) == 1 ? -1 : 1)) * Time.delta);
            }

            if (this.trailChance > 0.0F && Mathf.chanceDelta((double)this.trailChance)) {
                this.trailEffect.at(b.x, b.y, this.trailRotation ? b.rotation() : this.trailParam * this.bulletRadius(b), this.trailColor);
            }

            if (this.trailInterval > 0.0F && b.timer(0, this.trailInterval)) {
                this.trailEffect.at(b.x, b.y, this.trailRotation ? b.rotation() : this.trailParam * this.bulletRadius(b), this.trailColor);
            }

        }
    }

    protected static class JoinData {
        protected Bullet bullet;
        protected Bullet target;
        protected float rotation;
    }
}
