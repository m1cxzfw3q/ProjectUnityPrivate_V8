package unity.entities;

import arc.func.Boolf2;
import arc.func.Cons;
import arc.func.Floatf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import java.util.Objects;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import unity.type.TentacleType;
import unity.util.Utils;

public class Tentacle {
    TentacleSegment[] segments;
    TentacleType type;
    Unit unit;
    Teamc target;
    Interval timer = new Interval(2);
    Vec2 targetPos = new Vec2();
    Vec2 endVelocity = new Vec2();
    float swayScl = 1.0F;
    float reloadTime = 0.0F;
    float chargingTime = 0.0F;
    float chargingTimeB = 0.0F;
    float chargingTimeC = 0.0F;
    float lastTipX;
    float lastTipY;
    Bullet bullet;
    boolean attacking = false;

    public void updateMovement() {
        if (!this.attacking && this.chargingTime <= 0.0F) {
            this.swayScl = Mathf.lerpDelta(this.swayScl, 1.0F, 0.04F);
        } else {
            this.swayScl = Mathf.lerpDelta(this.swayScl, 0.0F, 0.04F);
            if (this.type.bullet != null) {
                float speed = this.type.speed * this.type.accel;
                Tmp.v2.trns(this.last().angleTo(this.targetPos) + 180.0F, this.type.range / 3.0F).add(this.targetPos);
                float dst = this.last().dst(Tmp.v2) / 200.0F;
                Tmp.v1.set(this.last()).approachDelta(Tmp.v2, Math.min(speed, dst)).sub(this.last());
                this.endVelocity.add(Tmp.v1).limit(this.type.speed);
                this.last().rotation = Angles.moveToward(this.last().rotation, this.last().angleTo(this.targetPos) + 180.0F, this.type.rotationSpeed * Time.delta);
                this.last().rotation = Mathf.slerpDelta(this.last().rotation, this.last().angleTo(this.targetPos) + 180.0F, this.endVelocity.len2() / (this.type.speed * this.type.speed) * 0.25F);
                this.last().updatePosition();
            } else if (this.chargingTime < 80.0F) {
                if (!this.last().within(this.targetPos, 10.0F) && Angles.within((this.last().rotation + 180.0F) % 360.0F, this.last().angleTo(this.targetPos), 90.0F) && this.last().within(this.targetPos, this.type.range() + 10.0F)) {
                    this.last().rotation = Angles.moveToward(this.last().rotation, this.last().angleTo(this.targetPos) + 180.0F, this.type.rotationSpeed * Time.delta);
                    this.last().rotation = Mathf.slerpDelta(this.last().rotation, this.last().angleTo(this.targetPos) + 180.0F, this.endVelocity.len2() / (this.type.speed * this.type.speed) * 0.25F);
                    this.last().updatePosition();
                    this.chargingTimeC += Time.delta;
                } else {
                    this.chargingTime += Time.delta;
                }

                if (this.chargingTimeC >= 120.0F) {
                    this.chargingTime = 81.0F;
                }

                Tmp.v1.trns(this.last().rotation + 180.0F, this.type.speed * this.type.accel);
                this.endVelocity.add(Tmp.v1).limit(this.type.speed);
            } else {
                Position origin = this.parentPosition(-1);
                Tmp.v2.trns(this.last().angleTo(origin) + 180.0F, this.type.range / 3.0F).add(origin);
                float dst = this.last().dst(Tmp.v2) / 200.0F;
                Tmp.v1.set(this.last()).approachDelta(Tmp.v2, Math.min(this.type.speed * this.type.accel, dst)).sub(this.last());
                this.endVelocity.add(Tmp.v1).limit(this.type.speed);
                this.chargingTimeB += Time.delta;
                if (this.chargingTimeB >= 120.0F) {
                    this.chargingTimeC = 0.0F;
                    this.chargingTimeB = 0.0F;
                    this.chargingTime = 0.0F;
                }
            }
        }

        if (this.endVelocity.len2() > 1.0E-4F) {
            this.last().pos.add(this.endVelocity, Time.delta);

            for(int i = this.segments.length - 2; i >= 0; --i) {
                TentacleSegment seg = this.segments[i];
                TentacleSegment segNext = this.segments[i + 1];
                Position nextPos = Tmp.v1.set(segNext.oppositePosition());
                float newAngle = seg.oppositePosition().angleTo(nextPos) + 180.0F;
                newAngle = Utils.clampedAngle(newAngle, segNext.rotation, segNext.angleLimit());
                float angVel = Utils.angleDistSigned(newAngle, seg.rotation);
                seg.angularVelocity += angVel;
                seg.rotation = newAngle;
                seg.updatePosBack();
            }

            for(TentacleSegment segment : this.segments) {
                segment.updatePosition();
            }
        }

        this.endVelocity.scl(1.0F - Mathf.clamp(this.type.drag * Time.delta));
    }

    void updateWeapon() {
        if (this.type.tentacleDamage > 0.0F && this.timer.get(1, 5.0F)) {
            if (this.endVelocity.len() - this.type.startVelocity > 1.0E-4F && this.type.speed > 0.0F) {
                float damage = this.type.tentacleDamage * Interp.pow2In.apply(Mathf.clamp((this.endVelocity.len() - this.type.startVelocity) * (1.0F + this.type.startVelocity / this.type.speed) / this.type.speed));
                if (damage > 0.0F) {
                    Team var10000 = this.unit.team;
                    float var10001 = this.last().getX();
                    float var10002 = this.last().getY();
                    float var10003 = this.lastTipX;
                    float var10004 = this.lastTipY;
                    Boolf2 var10005 = (building, aBoolean) -> {
                        if (aBoolean) {
                            building.damage(damage);
                        }

                        return false;
                    };
                    Cons var10006 = (unit1) -> unit1.damage(damage);
                    Effect var10008 = Fx.hitBulletSmall;
                    Objects.requireNonNull(var10008);
                    Utils.collideLineRawEnemy(var10000, var10001, var10002, var10003, var10004, var10005, var10006, (Floatf)null, var10008::at);
                }
            }

            this.lastTipX = this.last().getX();
            this.lastTipY = this.last().getY();
        }

        if (this.type.bullet != null) {
            if (this.reloadTime >= this.type.reload) {
                if (Angles.within(this.last().rotation + 180.0F, this.last().angleTo(this.targetPos), this.type.shootCone) && this.last().within(this.targetPos, this.type.range) && (this.target != null || this.unit.isPlayer() && this.unit.isShooting())) {
                    Bullet b = this.type.bullet.create(this.unit, this.unit.team, this.last().getX(), this.last().getY(), this.last().rotation + 180.0F);
                    if (this.type.continuous) {
                        this.bullet = b;
                    }

                    this.reloadTime = 0.0F;
                }
            } else if (this.bullet == null || !this.type.continuous) {
                this.reloadTime += Time.delta * this.unit.reloadMultiplier();
            }

            if (this.bullet != null) {
                this.bullet.set(this.last());
                this.bullet.rotation(this.last().rotation + 180.0F);
                if (this.bullet.time >= this.bullet.lifetime || !this.bullet.isAdded() || this.bullet.type != this.type.bullet) {
                    this.bullet = null;
                }
            }

        }
    }

    void updateTargeting() {
        Position origin = this.parentPosition(-1);
        TentacleSegment segment = this.segments[this.segments.length - 1];
        if (Units.invalidateTarget(this.target, this.unit.team, origin.getX(), origin.getY(), this.type.range())) {
            this.target = null;
        }

        if (this.timer.get(20.0F) && (!this.unit.isPlayer() || this.type.automatic)) {
            this.target = Units.closestTarget(this.unit.team, segment.getX(), segment.getY(), this.type.range, (unit) -> origin.within(unit, this.type.range()) && unit.isValid(), (building) -> origin.within(building, this.type.range()));
        }

        if (this.unit.isPlayer() && !this.type.automatic) {
            if (this.unit.isShooting()) {
                this.attacking = true;
                this.targetPos.set(this.unit.aimX, this.unit.aimY);
            } else {
                this.attacking = false;
            }
        } else if (this.target == null || this.type.bullet == null && !this.unit.isShooting()) {
            this.attacking = false;
        } else {
            this.attacking = true;
            this.targetPos.set(this.target);
        }

    }

    float indexRotation(int index) {
        return index < 0 ? this.unit.rotation + this.type.rotationOffset : this.segments[Math.min(index, this.segments.length - 1)].rotation;
    }

    Position parentPosition(int index) {
        if (index < 0) {
            return Tmp.v4.trns(this.unit.rotation - 90.0F, this.type.x, this.type.y).add(this.unit);
        } else {
            return index >= this.segments.length ? null : this.segments[index];
        }
    }

    TentacleSegment last() {
        return this.segments[this.segments.length - 1];
    }

    public void update() {
        this.updateMovement();

        for(TentacleSegment segment : this.segments) {
            segment.pos.add(segment.vel.x * Time.delta, segment.vel.y * Time.delta);
            segment.vel.scl(1.0F - Mathf.clamp(this.type.drag * Time.delta));
            float offset = this.swayScl > 0.0F ? Mathf.sin(Time.time + (float)segment.index * this.type.swaySegmentOffset + this.type.swayOffset, this.type.swayScl, this.type.swayMag * this.swayScl) * (float)Mathf.sign(this.type.flipSprite) : 0.0F;
            segment.angularVelocity += offset;
            segment.angularVelocity = Mathf.clamp(segment.angularVelocity, -this.type.speed, this.type.speed);
        }

        for(TentacleSegment segment : this.segments) {
            if (segment.index == 0) {
                Tmp.v1.trns(this.unit.rotation - 90.0F, this.type.x, this.type.y).add(this.unit);
                float angle = segment.angleTo(Tmp.v1) + segment.angularVelocity * Time.delta;
                segment.rotation = Utils.clampedAngle(angle, this.unit.rotation + this.type.rotationOffset, this.type.firstSegmentAngleLimit);
                Tmp.v2.trns(segment.rotation, this.type.segmentLength).add(segment).sub(Tmp.v1);
                segment.pos.sub(Tmp.v2);
                Tmp.v3.trns(segment.rotation, Tmp.v2.len() / Time.delta);
            } else {
                TentacleSegment last = this.segments[segment.index - 1];
                float angle = segment.angleTo(last) + segment.angularVelocity * Time.delta;
                segment.rotation = Utils.clampedAngle(angle, last.rotation, this.type.angleLimit);
                Tmp.v2.trns(segment.rotation, this.type.segmentLength).add(segment).sub(last);
                segment.pos.sub(Tmp.v2);
                Tmp.v3.trns(segment.rotation, Math.max(last.vel.len(), segment.vel.len()));
            }

            segment.vel.add(Tmp.v3);
            segment.vel.limit(this.type.speed);
            segment.angularVelocity *= 1.0F - Mathf.clamp(this.type.drag * Time.delta);
        }

        this.updateTargeting();
        if (this.target != null || this.unit.isPlayer()) {
            this.updateWeapon();
        }

    }

    public void draw() {
        for(int i = 0; i < this.segments.length; ++i) {
            TextureRegion region = i == this.segments.length - 1 ? this.type.tipRegion : this.type.region;
            TentacleSegment a = this.segments[i];
            Position b = this.parentPosition(i - 1);
            Tmp.v1.set(a).sub(b).setLength((float)region.width / 4.0F).add(b);
            this.unit.type.applyColor(this.unit);
            Lines.stroke((float)region.height * Draw.scl * (float)Mathf.sign(this.type.flipSprite));
            Lines.line(region, b.getX(), b.getY(), Tmp.v1.x, Tmp.v1.y, false);
        }

        Draw.color();
    }

    public Tentacle add(TentacleType t, Unit unit) {
        this.type = t;
        this.segments = new TentacleSegment[t.segments];
        this.unit = unit;

        for(int i = 0; i < this.segments.length; ++i) {
            TentacleSegment s = new TentacleSegment();
            s.index = i;
            s.main = this;
            s.pos.trns(unit.rotation + this.type.rotationOffset + 180.0F, (float)(i + 1) * this.type.segmentLength).add(Tmp.v1.trns(unit.rotation - 90.0F, this.type.x, this.type.y).add(unit));
            s.rotation = unit.rotation + 180.0F + this.type.rotationOffset;
            this.segments[i] = s;
        }

        this.lastTipX = this.last().getX();
        this.lastTipY = this.last().getY();
        return this;
    }

    public static class TentacleSegment implements Position {
        Vec2 pos = new Vec2();
        Vec2 vel = new Vec2();
        Tentacle main;
        int index;
        float angularVelocity;
        float rotation;

        float angleLimit() {
            return this.index == 0 ? this.main.type.firstSegmentAngleLimit : this.main.type.angleLimit;
        }

        void updatePosition() {
            float angle = this.main.indexRotation(this.index - 1);
            this.rotation = Utils.clampedAngle(this.rotation, angle, this.index == 0 ? this.main.type.firstSegmentAngleLimit : this.main.type.angleLimit);
            Tmp.v2.trns(this.rotation, this.main.type.segmentLength).add(this).sub(this.main.parentPosition(this.index - 1));
            this.pos.sub(Tmp.v2);
            this.vel.limit(this.main.type.speed);
        }

        void updatePosBack() {
            if (this.index < this.main.segments.length - 1) {
                TentacleSegment next = this.main.segments[this.index + 1];
                Tmp.v2.trns(next.rotation, this.main.type.segmentLength).add(next).sub(this);
                this.vel.add(Tmp.v2);
                this.vel.limit(this.main.type.speed);
                this.pos.add(Tmp.v2);
            }
        }

        Position oppositePosition() {
            return Tmp.v4.trns(this.rotation, this.main.type.segmentLength).add(this);
        }

        public float getX() {
            return this.pos.x;
        }

        public float getY() {
            return this.pos.y;
        }
    }
}
