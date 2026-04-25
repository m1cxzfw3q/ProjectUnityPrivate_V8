package unity.entities.abilities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import unity.content.UnityBullets;
import unity.entities.AbilityTextures;
import unity.type.UnityUnitType;
import unity.util.Utils;

public class DirectionShieldAbility extends Ability {
    protected final float shieldWidth = 7.0F;
    protected final float blinkTime = 5.0F;
    public int shields;
    public float[] shieldAngles;
    public float[] healths;
    public float[] hitTimes;
    public boolean[] available;
    public float maxHealth;
    public float disableRegen;
    public float shieldRegen;
    public float distanceRadius;
    public float shieldSize;
    public float shieldSpeed;
    public Interval timer = new Interval();
    public float explosiveReflectDamageMultiplier = 0.7F;
    public float explosiveDamageThreshold = 90.0F;
    public float healthBarOffset = 4.0F;
    public Color healthBarColor;

    public DirectionShieldAbility(int shields, float speed, float size, float health, float regen, float disableRegen, float distance) {
        this.healthBarColor = Color.white;
        this.shieldSpeed = speed;
        this.shieldSize = size;
        this.maxHealth = health;
        this.shieldRegen = regen;
        this.distanceRadius = distance;
        this.shieldAngles = new float[shields];
        this.healths = new float[shields];
        this.hitTimes = new float[shields];
        this.available = new boolean[shields];
        this.disableRegen = disableRegen;
        this.shields = shields;

        for(int i = 0; i < shields; ++i) {
            this.shieldAngles[i] = 0.0F;
            this.hitTimes[i] = 0.0F;
            this.healths[i] = health;
            this.available[i] = true;
        }

    }

    public Ability copy() {
        DirectionShieldAbility instance = new DirectionShieldAbility(this.shields, this.shieldSpeed, this.shieldSize, this.maxHealth, this.shieldRegen, this.disableRegen, this.distanceRadius);
        instance.explosiveReflectDamageMultiplier = this.explosiveReflectDamageMultiplier;
        instance.explosiveDamageThreshold = this.explosiveDamageThreshold;
        instance.healthBarOffset = this.healthBarOffset;
        instance.healthBarColor = this.healthBarColor;
        return instance;
    }

    protected void updateShields(Unit unit) {
        Tmp.r1.setCentered(unit.x, unit.y, this.shieldSize);
        Seq<ShieldNode> nodes = new Seq();

        for(int i = 0; i < this.shields; ++i) {
            Tmp.v1.trns(this.shieldAngles[i], this.distanceRadius - 3.5F);
            Tmp.v1.add(unit);
            Tmp.v2.trns(this.shieldAngles[i] + 90.0F, this.shieldSize / 2.0F - 3.5F);
            ShieldNode ts = new ShieldNode();
            ts.id = i;

            for(int s : Mathf.signs) {
                ts.getNodes(s).set(Tmp.v1.x + Tmp.v2.x * (float)s, Tmp.v1.y + Tmp.v2.y * (float)s);
                Tmp.r2.setCentered(Tmp.v1.x + Tmp.v2.x * (float)s, Tmp.v1.y + Tmp.v2.y * (float)s, this.shieldSize / 2.0F);
                Tmp.r1.merge(Tmp.r2);
            }

            nodes.add(ts);
        }

        if (this.timer.get(1.5F)) {
            Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, (b) -> {
                if (b.team != unit.team && !(b.type instanceof ContinuousLaserBulletType) && !(b.type instanceof LaserBulletType) && !b.type.scaleVelocity && b.vel().len() > 0.1F) {
                    b.hitbox(Tmp.r2);
                    Tmp.r3.set(Tmp.r2).grow(7.0F).move(b.vel.x / 2.0F, b.vel.y / 2.0F);
                    Tmp.r2.grow(7.0F);
                    nodes.each((n) -> {
                        if (this.available[n.id]) {
                            if (Geometry.raycastRect(n.nodeA.x, n.nodeA.y, n.nodeB.x, n.nodeB.y, Tmp.r2) != null || Geometry.raycastRect(n.nodeA.x, n.nodeA.y, n.nodeB.x, n.nodeB.y, Tmp.r3) != null) {
                                float d = Utils.getBulletDamage(b.type) * (b.damage() / (b.type.damage * b.damageMultiplier()));
                                float[] var10000 = this.healths;
                                int var10001 = n.id;
                                var10000[var10001] -= d;
                                b.damage(b.damage() / 1.5F);
                                float angC = (this.shieldAngles[n.id] + 90.0F) * 2.0F - b.rotation() + Mathf.range(15.0F);
                                if (this.explosiveReflectDamageMultiplier > 0.0F && d >= this.explosiveDamageThreshold) {
                                    for(int i = 0; i < 3; ++i) {
                                        float off = (float)i * 20.0F - 20.0F;
                                        UnityBullets.scarShrapnel.create(unit, unit.team, b.x, b.y, angC + off, d * this.explosiveReflectDamageMultiplier, 1.0F, 1.0F, (Object)null);
                                    }
                                }

                                this.hitTimes[n.id] = 5.0F;
                                b.team(unit.team());
                                b.rotation(angC);
                                if (this.healths[n.id] < 0.0F) {
                                    this.available[n.id] = false;
                                }
                            }

                        }
                    });
                }

            });
        }

        for(int i = 0; i < this.shields; ++i) {
            if (this.available[i]) {
                this.healths[i] = Math.min(this.healths[i] + this.shieldRegen * Time.delta, this.maxHealth);
            } else {
                if (Mathf.chanceDelta(0.32 * (double)(1.0F - Mathf.clamp(this.healths[i] / this.maxHealth)))) {
                    Tmp.v1.trns(this.shieldAngles[i], this.distanceRadius);
                    Tmp.v1.add(unit);
                    Tmp.v2.trns(this.shieldAngles[i] + 90.0F, Mathf.range(this.shieldSize / 2.0F), Mathf.range(2.0F));
                    Tmp.v1.add(Tmp.v2);
                    Fx.smoke.at(Tmp.v1.x, Tmp.v1.y);
                }

                this.healths[i] = Math.min(this.healths[i] + this.disableRegen * Time.delta, this.maxHealth);
                if (this.healths[i] >= this.maxHealth) {
                    this.available[i] = true;
                    this.hitTimes[i] = 5.0F;
                }
            }
        }

    }

    public void update(Unit unit) {
        if (unit.isShooting()) {
            float size = this.shieldSize * ((float)Math.PI * 2F) / Mathf.sqrt(this.distanceRadius / 1.5F);

            for(int i = 0; i < this.shields; ++i) {
                float ang = Mathf.mod((float)i * size - ((float)this.shields - 1.0F) * size / 2.0F + unit.rotation, 360.0F);
                this.shieldAngles[i] = Mathf.slerpDelta(this.shieldAngles[i], ang, this.shieldSpeed);
                this.hitTimes[i] = Math.max(this.hitTimes[i] - Time.delta, 0.0F);
            }
        } else {
            float offset = 360.0F / (float)this.shields / 2.0F;

            for(int i = 0; i < this.shields; ++i) {
                float ang = Mathf.mod((float)i * 360.0F / (float)this.shields + offset + unit.rotation + 180.0F, 360.0F);
                this.shieldAngles[i] = Mathf.slerpDelta(this.shieldAngles[i], ang, this.shieldSpeed);
                this.hitTimes[i] = Math.max(this.hitTimes[i] - Time.delta, 0.0F);
            }
        }

        this.updateShields(unit);
    }

    public void draw(Unit unit) {
        float z = Draw.z();
        if (unit.type instanceof UnityUnitType) {
            UnityUnitType type = (UnityUnitType)unit.type;
            TextureRegion region = type.abilityRegions[AbilityTextures.shield.ordinal()];
            float size = (float)Math.max(region.width, region.height) * Draw.scl * 1.3F;
            Lines.stroke(1.5F);

            for(int i = 0; i < this.shields; ++i) {
                Draw.z(z - 0.0098F);
                Tmp.v3.trns(this.shieldAngles[i], this.distanceRadius);
                Tmp.v3.add(unit);
                float offset = this.available[i] ? 2.0F : 1.5F;
                Draw.mixcol(Color.white, this.hitTimes[i] / 5.0F);
                Draw.color(Color.white, Color.black, (1.0F - Mathf.clamp(this.healths[i] / this.maxHealth)) / offset);
                Draw.rect(region, Tmp.v3.x, Tmp.v3.y, this.shieldAngles[i]);
                if (this.available[i]) {
                    Tmp.v3.trns(this.shieldAngles[i], this.distanceRadius + this.healthBarOffset);
                    Tmp.v3.add(unit);
                    Draw.color(this.healthBarColor);
                    Lines.lineAngleCenter(Tmp.v3.x, Tmp.v3.y, this.shieldAngles[i] + 90.0F, Mathf.clamp(this.healths[i] / this.maxHealth) * this.shieldSize);
                }

                Draw.z(Math.min(80.0F, z - 1.0F));
                Draw.mixcol();
                Draw.color(Pal.shadow);
                Draw.rect(type.softShadowRegion, Tmp.v3.x, Tmp.v3.y, size, size);
                Draw.z(z - 0.0099F);
                float engScl = this.shieldSize / 6.0F;
                float liveScl = engScl - engScl / 4.0F + Mathf.absin(Time.time, 2.0F, engScl / 4.0F);
                Tmp.v3.trns(this.shieldAngles[i], this.distanceRadius - engScl);
                Tmp.v3.add(unit);
                Draw.color(unit.team.color);
                Fill.circle(Tmp.v3.x, Tmp.v3.y, liveScl);
                Draw.color(Color.white);
                Fill.circle(Tmp.v3.x, Tmp.v3.y, liveScl / 2.0F);
                Draw.z(z);
            }

            Draw.reset();
        }
    }

    public static class ShieldNode {
        public Vec2 nodeA = new Vec2();
        public Vec2 nodeB = new Vec2();
        public int id;

        public Vec2 getNodes(int sign) {
            return sign <= 0 ? this.nodeA : this.nodeB;
        }
    }
}
