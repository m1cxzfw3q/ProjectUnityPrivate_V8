package unity.entities.units;

import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import java.util.Arrays;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import unity.gen.UnityEntityMapping;
import unity.type.UnityUnitType;
import unity.util.Utils;

public class WormDefaultUnit extends UnitEntity {
    public UnityUnitType wormType;
    public WormSegmentUnit[] segmentUnits;
    public float repairTime = 0.0F;
    protected float attachTime = 240.0F;
    protected float healthDistributionEfficiency = 1.0F;
    protected Vec2[] segments;
    protected Vec2[] segmentVelocities;
    protected boolean addSegments = true;
    protected boolean found;
    protected final Interval scanTimer = new Interval();
    protected final Vec2 lastVelocityC = new Vec2();
    protected final Vec2 lastVelocityD = new Vec2();

    public int getSegmentLength() {
        return this.wormType.segmentLength;
    }

    public void type(UnitType type) {
        super.type(type);
        if (type instanceof UnityUnitType) {
            UnityUnitType w = (UnityUnitType)type;
            this.wormType = w;
        } else {
            throw new ClassCastException("you set this unit's type in a sneaky way");
        }
    }

    public void setType(UnitType type) {
        super.setType(type);
        if (type instanceof UnityUnitType) {
            UnityUnitType w = (UnityUnitType)type;
            this.wormType = w;
        } else {
            throw new ClassCastException("you set this unit's type in a sneaky way");
        }
    }

    protected void setEffects() {
        this.segmentUnits = new WormSegmentUnit[this.wormType.segmentLength];
        this.segments = new Vec2[this.wormType.segmentLength];
        this.segmentVelocities = new Vec2[this.wormType.segmentLength];

        for(int i = 0; i < this.getSegmentLength(); ++i) {
            this.segments[i] = new Vec2(this.x, this.y);
            this.segmentVelocities[i] = new Vec2();
        }

    }

    public void damage(float amount) {
        super.damage(amount);
        this.healthDistributionEfficiency = Mathf.clamp(this.healthDistributionEfficiency - amount / 15.0F);
    }

    public void update() {
        this.lastVelocityD.set(this.lastVelocityC);
        this.lastVelocityC.set(this.vel);
        super.update();
        this.healthDistributionEfficiency = Mathf.clamp(this.healthDistributionEfficiency + Time.delta / 160.0F);
        this.updateSegmentVLocal(this.lastVelocityC);
        this.updateSegmentsLocal();
        if (this.wormType.chainable && this.segmentUnits.length < this.wormType.maxSegments && this.scanTimer.get(15.0F) && this.attachTime >= 240.0F) {
            this.scanTailSegment();
        }

        this.attachTime += Time.delta;
        if (this.regenAvailable()) {
            if (this.repairTime >= this.wormType.regenTime) {
                float damage = this.health / (float)this.segmentUnits.length / 2.0F;
                this.damage(damage);

                for(WormSegmentUnit seg : this.segmentUnits) {
                    float sDamage = seg.segmentHealth / (float)this.segmentUnits.length / 2.0F;
                    seg.segmentDamage(sDamage);
                }

                this.addSegment();
                this.repairTime = 0.0F;
            } else {
                this.repairTime += Time.delta;
            }
        }

    }

    public boolean regenAvailable() {
        return this.wormType.splittable && (this.segmentUnits.length < this.wormType.segmentLength || this.segmentUnits.length < this.wormType.maxSegments) && this.wormType.regenTime > 0.0F;
    }

    protected void updateSegmentVLocal(Vec2 vec) {
        int len = this.segmentUnits.length;

        for(int i = 0; i < len; ++i) {
            Vec2 seg = this.segments[i];
            Vec2 segV = this.segmentVelocities[i];
            segV.limit(this.type.speed);
            float angleB = i != 0 ? Angles.angle(seg.x, seg.y, this.segments[i - 1].x, this.segments[i - 1].y) : Angles.angle(seg.x, seg.y, this.x, this.y);
            float velocity = i != 0 ? this.segmentVelocities[i - 1].len() : vec.len();
            Tmp.v1.set(this.vel);
            Tmp.v1.add(vec);
            Tmp.v1.add(this.lastVelocityD);
            Tmp.v1.scl(0.33333334F);
            float trueVel = Math.max(Math.max(velocity, segV.len()), Tmp.v1.len());
            Tmp.v1.trns(angleB, trueVel);
            segV.add(Tmp.v1);
            segV.setLength(trueVel);
            if (this.wormType.counterDrag) {
                segV.scl(1.0F - this.drag);
            }

            this.segmentUnits[i].vel.set(segV);
        }

    }

    protected void updateSegmentsLocal() {
        float segmentOffset = this.wormType.segmentOffset / 2.0F;
        this.segments[0].add(this.segmentVelocities[0]);
        this.rotation -= Utils.angleDistSigned(this.rotation, this.segmentUnits[0].rotation, this.wormType.angleLimit) / 1.25F;
        Tmp.v1.trns(this.rotation + 180.0F, segmentOffset + this.wormType.headOffset).add(this);
        this.segmentUnits[0].rotation = Utils.clampedAngle(this.segments[0].angleTo(Tmp.v1), this.rotation, this.wormType.angleLimit);
        Tmp.v2.trns(this.segmentUnits[0].rotation, segmentOffset).add(this.segments[0]).sub(Tmp.v1);
        this.segments[0].sub(Tmp.v2);
        this.segmentVelocities[0].scl(Mathf.clamp(1.0F - this.drag * Time.delta));
        this.segmentUnits[0].set(this.segments[0].x, this.segments[0].y);
        this.segmentUnits[0].wormSegmentUpdate();
        if (this.wormType.healthDistribution > 0.0F) {
            this.distributeHealth(0);
        }

        int len = this.segmentUnits.length;

        for(int i = 1; i < len; ++i) {
            Vec2 seg = this.segments[i];
            Vec2 segLast = this.segments[i - 1];
            WormSegmentUnit segU = this.segmentUnits[i];
            WormSegmentUnit segULast = this.segmentUnits[i - 1];
            seg.add(this.segmentVelocities[i]);
            segULast.rotation -= Utils.angleDistSigned(segULast.rotation, segU.rotation, this.wormType.angleLimit) / 1.25F;
            Tmp.v1.trns(segULast.rotation + 180.0F, segmentOffset).add(segLast);
            segU.rotation = Utils.clampedAngle(segU.angleTo(Tmp.v1), segULast.rotation, this.wormType.angleLimit);
            Tmp.v2.trns(segU.rotation, segmentOffset).add(seg).sub(Tmp.v1);
            seg.sub(Tmp.v2);
            this.segmentVelocities[i].scl(Mathf.clamp(1.0F - this.drag * Time.delta));
            segU.set(seg);
            segU.wormSegmentUpdate();
            if (this.wormType.healthDistribution > 0.0F) {
                this.distributeHealth(i);
            }
        }

    }

    protected void distributeHealth(int index) {
        int idx = 0;
        float mHealth = 0.0F;
        float mMaxHealth = 0.0F;

        for(int i = -1; i < 2; ++i) {
            Unit seg = this.getSegment(i + index);
            if (seg == null) {
                break;
            }

            mHealth += seg.health;
            mMaxHealth += seg.maxHealth;
            ++idx;
        }

        mMaxHealth /= (float)idx;
        mHealth /= (float)idx;

        for(int i = -1; i < 2; ++i) {
            Unit seg = this.getSegment(i + index);
            if (seg == null) {
                break;
            }

            if (seg instanceof WormSegmentUnit) {
                WormSegmentUnit ws = (WormSegmentUnit)seg;
                if (!Mathf.equal(ws.segmentHealth, mHealth, 0.001F)) {
                    ws.segmentHealth = Mathf.lerpDelta(ws.segmentHealth, mHealth, this.wormType.healthDistribution * this.healthDistributionEfficiency);
                }
            } else if (!Mathf.equal(seg.health, mHealth, 0.001F)) {
                seg.health = Mathf.lerpDelta(seg.health, mHealth, this.wormType.healthDistribution * this.healthDistributionEfficiency);
            }

            if (!Mathf.equal(seg.maxHealth, mMaxHealth, 0.001F)) {
                seg.maxHealth = Mathf.lerpDelta(seg.maxHealth, mMaxHealth, this.wormType.healthDistribution * this.healthDistributionEfficiency);
            }
        }

    }

    protected Unit getSegment(int index) {
        if (index < 0) {
            return this;
        } else {
            return index >= this.segmentUnits.length ? null : this.segmentUnits[index];
        }
    }

    public int classId() {
        return UnityEntityMapping.classId(WormDefaultUnit.class);
    }

    public float clipSize() {
        return (float)this.segmentUnits.length * this.wormType.segmentOffset * 2.0F;
    }

    public void drawShadow() {
        float originZ = Draw.z();
        int i = 0;

        for(int len = this.segmentUnits.length; i < len; ++i) {
            Draw.z(originZ - (float)(i + 1) / 10000.0F);
            this.segmentUnits[i].drawShadow();
        }

        Draw.z(originZ);
    }

    public WormSegmentUnit newSegment() {
        return new WormSegmentUnit();
    }

    public void destroy() {
        if (this.added) {
            super.destroy();

            for(WormSegmentUnit seg : this.segmentUnits) {
                float explosiveness = 2.0F + seg.item().explosiveness * (float)this.stack().amount * 1.53F;
                float flammability = seg.item().flammability * (float)seg.stack().amount / 1.9F;
                float power = seg.item().charge * (float)seg.stack().amount * 150.0F;
                if (!this.spawnedByCore) {
                    Damage.dynamicExplosion(seg.x, seg.y, flammability, explosiveness, power, this.bounds() / 2.0F, Vars.state.rules.damageExplosions, this.item().flammability > 1.0F, this.team);
                }

                float shake = this.hitSize / 3.0F;
                Effect.scorch(seg.x, seg.y, (int)(this.hitSize / 5.0F));
                Fx.explosion.at(seg);
                Effect.shake(shake, shake, seg);
                this.type.deathSound.at(seg);
                if (this.type.flying && !this.spawnedByCore) {
                    Damage.damage(this.team, seg.x, seg.y, Mathf.pow(seg.hitSize, 0.94F) * 1.25F, Mathf.pow(seg.hitSize, 0.75F) * this.type.crashDamageMultiplier * 5.0F, true, false, true);
                }

                if (!Vars.headless) {
                    for(int i = 0; i < this.type.wreckRegions.length; ++i) {
                        if (this.type.wreckRegions[i].found()) {
                            float range = this.type.hitSize / 4.0F;
                            Tmp.v1.rnd(range);
                            Effect.decal(this.type.wreckRegions[i], seg.x + Tmp.v1.x, seg.y + Tmp.v1.y, seg.rotation - 90.0F);
                        }
                    }
                }
            }

        }
    }

    public void remove() {
        if (this.added) {
            super.remove();

            for(WormSegmentUnit segmentUnit : this.segmentUnits) {
                segmentUnit.remove();
            }

        }
    }

    protected void superRemove() {
        super.remove();
    }

    public int count() {
        return Math.max(super.count() / Math.max(this.wormType.segmentLength, this.wormType.maxSegments), 1);
    }

    protected void scanTailSegment() {
        Tmp.v1.trns(this.rotation, this.wormType.segmentOffset).add(this);
        float size = this.wormType.hitSize / 2.0F;
        this.found = false;
        Units.nearby(this.team, Tmp.v1.x - size, Tmp.v1.y - size, size * 2.0F, size * 2.0F, (e) -> {
            if (!this.found) {
                if (e instanceof WormSegmentUnit) {
                    WormSegmentUnit ws = (WormSegmentUnit)e;
                    if (ws.segmentType == 1 && ws.wormType == this.wormType && ws.trueParentUnit != this && this.within(ws, this.wormType.segmentOffset + 5.0F) && Angles.within(this.angleTo(e), e.rotation, this.wormType.angleLimit + 2.0F)) {
                        if (ws.trueParentUnit == null || ws.trueParentUnit.segmentUnits.length > this.wormType.maxSegments) {
                            return;
                        }

                        this.wormType.chainSound.at(this, Mathf.random(0.9F, 1.1F));
                        WormSegmentUnit head = this.newSegment();
                        head.setType(this.wormType);
                        head.set(this);
                        head.rotation = this.rotation;
                        head.vel.set(this.vel);
                        head.team = this.team;
                        head.maxHealth = this.maxHealth;
                        head.health = head.segmentHealth = this.health;
                        head.segmentType = 0;
                        this.segmentUnits[0].parentUnit = head;
                        head.add();
                        this.superRemove();
                        WormSegmentUnit.SegmentData data = new WormSegmentUnit.SegmentData(this.segmentUnits.length + 1);
                        data.add(head, head.vel);

                        for(int i = 0; i < this.segmentUnits.length; ++i) {
                            data.add(this, i);
                        }

                        for(int i = 0; i < data.size; ++i) {
                            ws.trueParentUnit.addSegment(data.units[i], data.pos[i], data.vel[i]);
                        }

                        this.found = true;
                    }
                }

            }
        });
    }

    protected void removeTail() {
        int index = this.segments.length - 1;
        if (index > 0) {
            this.segmentUnits[index].remove();
            this.segmentUnits[index] = null;
            this.segmentUnits[index - 1].segmentType = 1;
            this.segmentUnits = (WormSegmentUnit[])Arrays.copyOf(this.segmentUnits, this.segmentUnits.length - 1);
            this.segments = (Vec2[])Arrays.copyOf(this.segments, this.segments.length - 1);
            this.segmentVelocities = (Vec2[])Arrays.copyOf(this.segmentVelocities, this.segmentVelocities.length - 1);
        }
    }

    public void addSegment(WormSegmentUnit unit, Vec2 pos, Vec2 vel) {
        int index = this.segments.length;
        Unit parent = this.segmentUnits[index - 1];
        this.segmentUnits[index - 1].segmentType = 0;
        this.segmentUnits = (WormSegmentUnit[])Arrays.copyOf(this.segmentUnits, this.segmentUnits.length + 1);
        this.segments = (Vec2[])Arrays.copyOf(this.segments, this.segments.length + 1);
        this.segmentVelocities = (Vec2[])Arrays.copyOf(this.segmentVelocities, this.segmentVelocities.length + 1);
        unit.elevation = this.elevation;
        unit.segmentType = 1;
        unit.parentUnit = parent;
        unit.trueParentUnit = this;
        this.segmentUnits[this.segmentUnits.length - 1] = unit;
        this.segments[this.segments.length - 1] = pos;
        this.segmentVelocities[this.segmentVelocities.length - 1] = vel;
    }

    public void addSegment() {
        int index = this.segments.length;
        Unit parent = this.segmentUnits[index - 1];
        Tmp.v1.trns(this.segmentUnits[index - 1].rotation + 180.0F, this.wormType.segmentOffset).add(this.segmentUnits[index - 1]);
        this.segmentUnits[index - 1].segmentType = 0;
        this.segmentUnits = (WormSegmentUnit[])Arrays.copyOf(this.segmentUnits, this.segmentUnits.length + 1);
        this.segments = (Vec2[])Arrays.copyOf(this.segments, this.segments.length + 1);
        this.segmentVelocities = (Vec2[])Arrays.copyOf(this.segmentVelocities, this.segmentVelocities.length + 1);
        WormSegmentUnit segment = this.newSegment();
        segment.elevation = this.elevation;
        segment.segmentType = 1;
        segment.setType(this.type);
        segment.parentUnit = parent;
        segment.trueParentUnit = this;
        segment.set(Tmp.v1);
        segment.team = this.team;
        segment.health = this.health;
        segment.maxHealth = this.maxHealth;
        segment.segmentHealth = this.health;
        segment.dead = false;
        segment.add();
        this.segmentUnits[this.segmentUnits.length - 1] = segment;
        this.segments[this.segments.length - 1] = new Vec2(Tmp.v1);
        this.segmentVelocities[this.segmentVelocities.length - 1] = new Vec2(this.segmentVelocities[this.segmentVelocities.length - 2]);
    }

    public void add() {
        if (!this.added) {
            super.add();
            if (!this.addSegments) {
                this.postAdd();
            } else {
                this.setEffects();
                Unit parent = this;
                int i = 0;

                for(int len = this.getSegmentLength(); i < len; ++i) {
                    int typeS = i == len - 1 ? 1 : 0;
                    this.segments[i].set(this.x, this.y);
                    WormSegmentUnit temp = this.newSegment();
                    temp.elevation = this.elevation;
                    temp.setSegmentType(typeS);
                    temp.type(this.type);
                    temp.resetController();
                    temp.team = this.team;
                    temp.setTrueParent(this);
                    temp.setParent(parent);
                    temp.add();
                    temp.afterSync();
                    temp.heal();
                    parent = temp;
                    this.segmentUnits[i] = temp;
                }

            }
        }
    }

    void postAdd() {
        for(WormSegmentUnit ws : this.segmentUnits) {
            ws.add();
        }

    }

    public void read(Reads read) {
        super.read(read);
        this.addSegments = false;
        int length = read.s();
        boolean splittable = read.bool();
        this.repairTime = read.f();
        this.segmentUnits = new WormSegmentUnit[length];
        this.segments = new Vec2[length];
        this.segmentVelocities = new Vec2[length];
        Unit parent = this;

        for(int i = 0; i < length; ++i) {
            this.segments[i] = new Vec2();
            this.segmentVelocities[i] = new Vec2();
            WormSegmentUnit temp = this.newSegment();
            temp.elevation = this.elevation;
            temp.type(this.type);
            temp.team = this.team;
            temp.drag = this.type.drag;
            temp.armor = this.type.armor;
            temp.hitSize = this.type.hitSize;
            temp.hovering = this.type.hovering;
            temp.setupWeapons(this.type);
            temp.resetController();
            temp.abilities = this.type.abilities.map(Ability::copy);
            temp.setTrueParent(this);
            temp.setParent(parent);
            temp.x = this.segments[i].x = read.f();
            temp.y = this.segments[i].y = read.f();
            temp.rotation = read.f();
            temp.segmentType = read.b();
            if (splittable) {
                temp.segmentHealth = temp.health = read.f();
                temp.maxHealth = read.f();
            }

            parent = temp;
            this.segmentUnits[i] = temp;
        }

    }

    public void write(Writes write) {
        super.write(write);
        write.s(this.segmentUnits.length);
        write.bool(this.wormType.splittable);
        write.f(this.repairTime);

        for(int i = 0; i < this.segmentUnits.length; ++i) {
            write.f(this.segments[i].x);
            write.f(this.segments[i].y);
            write.f(this.segmentUnits[i].rotation);
            write.b(this.segmentUnits[i].segmentType);
            if (this.wormType.splittable) {
                write.f(this.segmentUnits[i].segmentHealth);
                write.f(this.segmentUnits[i].maxHealth);
            }
        }

    }

    public void handleCollision(Hitboxc originUnit, Hitboxc other, float x, float y) {
    }
}
