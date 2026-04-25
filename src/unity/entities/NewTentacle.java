package unity.entities;

import arc.func.Boolf2;
import arc.func.Boolf3;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
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

public class NewTentacle {
    static final Vec2 tv;
    static final Vec2 tv2;
    NewTentacleSegment root;
    NewTentacleSegment end;
    TentacleType type;
    Unit unit;
    Teamc target;
    Bullet bullet;
    boolean attacking = false;
    boolean stab;
    float targetX;
    float targetY;
    float swayScl;
    float attackTime;
    float stabTime;
    float alx;
    float aly;
    float retarget;
    float reload;

    public NewTentacle(TentacleType t, Unit unit) {
        this.type = t;
        this.unit = unit;
        NewTentacleSegment child = null;
        tv.trns(unit.rotation + this.type.rotationOffset + 180.0F, this.type.segmentLength);
        Position pos = this.unitPosition();

        for(int i = 0; i < this.type.segments; ++i) {
            NewTentacleSegment seg = new NewTentacleSegment();
            seg.main = this;
            if (child == null) {
                this.root = seg;
            } else {
                child.parent = seg;
                seg.child = child;
            }

            if (i >= this.type.segments - 1) {
                this.end = seg;
            }

            int s = i + 1;
            seg.x = pos.getX() + tv.x * (float)s;
            seg.y = pos.getY() + tv.y * (float)s;
            child = seg;
        }

    }

    float rootRange() {
        return (float)this.type.segments * this.type.segmentLength + (this.type.bullet != null ? this.type.bullet.range() : 0.0F);
    }

    void updateMovement() {
        this.end.updateLastPosition();
        if (this.type.bullet != null) {
            tv2.set(this.end.x, this.end.y).sub(this.targetX, this.targetY).setLength(this.type.range / 3.0F).add(this.targetX, this.targetY);
            if (!tv2.isNaN()) {
                tv.set(tv2).sub(this.end.x, this.end.y).scl(0.025F).limit(this.type.speed);
                float ang = Angles.angle(this.end.x, this.end.y, this.targetX, this.targetY);
                this.end.rotation = Angles.moveToward(this.end.rotation, ang, this.type.rotationSpeed);
                float scl = Mathf.clamp((90.0F - Angles.angleDist(this.end.rotation, ang)) / 90.0F, 0.7F, 1.0F);
                tv2.trns(this.end.rotation, this.type.segmentLength).add(this.end.prevPos());
                this.end.x = tv2.x;
                this.end.y = tv2.y;
                NewTentacleSegment var10000 = this.end;
                var10000.vx += tv.x * scl * this.type.accel;
                var10000 = this.end;
                var10000.vy += tv.y * scl * this.type.accel;
            }
        } else {
            tv.set(this.targetX, this.targetY).sub(this.unitPosition()).scl(2.0F).add(this.unitPosition());
            float tx = tv.x;
            float ty = tv.y;
            float ang = Angles.angle(this.end.x, this.end.y, tx, ty);
            float scl = Mathf.clamp(Math.abs(90.0F - Angles.angleDist(this.end.rotation, ang)) / 90.0F, 0.7F, 1.0F);
            if (this.stab) {
                tv.set(tx, ty).sub(this.end.x, this.end.y).limit(this.type.speed);
                this.end.rotation = Angles.moveToward(this.end.rotation, ang, this.type.rotationSpeed);
                tv2.trns(this.end.rotation, this.type.segmentLength).add(this.end.prevPos());
                this.end.x = tv2.x;
                this.end.y = tv2.y;
                NewTentacleSegment var8 = this.end;
                var8.vx += tv.x * scl * this.type.accel;
                var8 = this.end;
                var8.vy += tv.y * scl * this.type.accel;
                if ((this.attackTime += Time.delta) >= 80.0F) {
                    this.attackTime = 0.0F;
                    this.stab = false;
                }
            } else {
                this.alx = this.end.x;
                this.aly = this.end.y;
                tv2.set(this.targetX, this.targetY).sub(this.unitPosition()).setLength(this.type.range / 5.0F).add(this.unitPosition());
                if (!tv2.isNaN()) {
                    tv.set(tv2).sub(this.end.x, this.end.y).scl(0.04F).limit(this.type.speed);
                    this.end.rotation = Angles.moveToward(this.end.rotation, ang, this.type.rotationSpeed);
                    tv2.trns(this.end.rotation, this.type.segmentLength).add(this.end.prevPos());
                    this.end.x = tv2.x;
                    this.end.y = tv2.y;
                    NewTentacleSegment var10 = this.end;
                    var10.vx += tv.x * scl * this.type.accel;
                    var10 = this.end;
                    var10.vy += tv.y * scl * this.type.accel;
                }

                this.attackTime += Time.delta;
                if (this.attackTime >= 80.0F) {
                    this.target = null;
                    this.attackTime = 0.0F;
                    this.stab = true;
                }
            }
        }

    }

    void updateWeapon() {
        this.attacking = false;
        boolean player = this.unit.isPlayer();
        if (!this.type.automatic && player) {
            if (this.unit.isShooting) {
                this.targetX = this.unit.aimX;
                this.targetY = this.unit.aimY;
            }
        } else {
            if (this.target == null && (this.retarget += Time.delta) >= 20.0F) {
                this.target = Units.closestTarget(this.unit.team, this.end.x, this.end.y, this.type.range, (u) -> u.isValid() && this.unitPosition().within(u, this.rootRange()), (bx) -> this.unitPosition().within(bx, this.rootRange()));
                this.retarget = 0.0F;
            }

            if (this.target != null) {
                this.targetX = this.target.getX();
                this.targetY = this.target.getY();
            }
        }

        Position pos = this.unitPosition();
        if (Units.invalidateTarget(this.target, this.unit.team, pos.getX(), pos.getY(), this.rootRange()) || player && !this.type.automatic) {
            this.target = null;
        }

        if (this.bullet == null && this.type.bullet != null) {
            this.reload += Time.delta * this.unit.reloadMultiplier;
        }

        if (this.target != null || player && this.unit.isShooting) {
            this.attacking = true;
            if (this.type.bullet != null && this.reload >= this.type.reload && Angles.within(this.end.rotation, Angles.angle(this.end.x, this.end.y, this.targetX, this.targetY), this.type.shootCone)) {
                Bullet b = this.type.bullet.create(this.unit, this.unit.team, this.end.x, this.end.y, this.end.rotation);
                if (this.type.shootSound != null) {
                    this.type.shootSound.at(this.end.x, this.end.y, Mathf.random(0.9F, 1.1F));
                }

                if (this.type.continuous) {
                    if (this.type.bulletDuration > 0.0F) {
                        b.lifetime = this.type.bulletDuration;
                    }

                    this.bullet = b;
                }

                this.reload = 0.0F;
            }
        }

        if (this.type.continuous) {
            if (this.bullet != null && (this.bullet.type != this.type.bullet || !this.bullet.isAdded())) {
                this.bullet = null;
            }

            if (this.bullet != null) {
                this.bullet.set(this.end.x, this.end.y);
                this.bullet.rotation(this.end.rotation);
            }
        }

        if (this.bullet == null && this.attacking && this.end.len() > 0.2F) {
            if (this.stab) {
                if ((this.stabTime += Time.delta) >= 5.0F) {
                    Team var10000 = this.unit.team;
                    float var10001 = this.alx;
                    float var10002 = this.aly;
                    float var10003 = this.end.x;
                    float var10004 = this.end.y;
                    Boolf3 var10006 = (building, ratio, direct) -> {
                        if (direct) {
                            building.damage(this.type.tentacleDamage * ratio);
                        }

                        return false;
                    };
                    Boolf2 var10007 = (unit, ratio) -> {
                        unit.damage(this.type.tentacleDamage * ratio);
                        return false;
                    };
                    Effect var10008 = Fx.hitBulletSmall;
                    Objects.requireNonNull(var10008);
                    Utils.collideLineRawEnemyRatio(var10000, var10001, var10002, var10003, var10004, 3.0F, var10006, var10007, var10008::at);
                    this.alx = this.end.x;
                    this.aly = this.end.y;
                    this.stabTime = 0.0F;
                }
            } else {
                this.alx = this.end.x;
                this.aly = this.end.y;
            }
        }

    }

    public void update() {
        if (!this.attacking) {
            this.swayScl = Mathf.lerpDelta(this.swayScl, 1.0F, 0.04F);
        } else {
            this.swayScl = Mathf.lerpDelta(this.swayScl, 0.0F, 0.04F);
            this.updateMovement();
        }

        for(int i = 0; i < 2; ++i) {
            int s = 0;
            NewTentacleSegment cur = i == 0 ? this.end : this.root;

            while(cur != null) {
                if (i == 0) {
                    cur.updateLastPosition();
                    tv.set(cur.vx, cur.vy).limit(this.type.speed);
                    cur.vx = tv.x;
                    cur.vy = tv.y;
                    cur.x += cur.vx * Time.delta;
                    cur.y += cur.vy * Time.delta;
                    cur.vx *= 1.0F - this.type.drag * Time.delta;
                    cur.vy *= 1.0F - this.type.drag * Time.delta;
                    if (this.swayScl >= 1.0E-4F) {
                        float sin = this.swayScl * Mathf.sin(Time.time + this.type.swayOffset + (float)s * this.type.swaySegmentOffset, this.type.swayScl, this.type.swayMag) * (float)Mathf.sign(this.type.flipSprite);
                        cur.rotation += sin;
                    }

                    if (cur.child != null) {
                        NewTentacleSegment c = cur.child;
                        float cx = Angles.trnsx(c.rotation + 180.0F, this.type.segmentLength) + c.x;
                        float cy = Angles.trnsy(c.rotation + 180.0F, this.type.segmentLength) + c.y;
                        float sx = Angles.trnsx(cur.rotation + 180.0F, this.type.segmentLength) + cur.x;
                        float sy = Angles.trnsy(cur.rotation + 180.0F, this.type.segmentLength) + cur.y;
                        c.rotation = Angles.angle(cx, cy, sx, sy);
                        float ang = Utils.angleDistSigned(cur.rotation, c.rotation, this.type.angleLimit);
                        c.rotation += ang;
                        c.x = sx;
                        c.y = sy;
                    }

                    cur = cur.child;
                    ++s;
                } else {
                    if (cur.child == null) {
                        float parentAng = this.unit.rotation + this.type.rotationOffset + 180.0F;
                        float ang = cur.prevPos().angleTo(cur.x, cur.y);
                        cur.rotation = Utils.clampedAngle(ang, parentAng, this.type.firstSegmentAngleLimit);
                        tv.trns(cur.rotation, this.type.segmentLength).add(this.unitPosition());
                    } else {
                        float childAng = cur.child.rotation;
                        float ang = cur.prevPos().angleTo(cur.x, cur.y);
                        cur.rotation = Utils.clampedAngle(ang, childAng, this.type.angleLimit);
                        tv.trns(cur.rotation, this.type.segmentLength).add(cur.child.x, cur.child.y);
                    }

                    cur.x = tv.x;
                    cur.y = tv.y;
                    cur = cur.parent;
                }
            }
        }

        this.updateWeapon();
    }

    public void draw() {
        float z = Draw.z();

        for(NewTentacleSegment cur = this.root; cur != null; cur = cur.parent) {
            if (this.type.top && cur != this.root) {
                Draw.z(z + 0.01001F);
            }

            TextureRegion region = cur.parent == null ? this.type.tipRegion : this.type.region;
            Position prev = cur.prevPos();
            tv.set(cur.x, cur.y).sub(prev).setLength((float)region.width * Draw.scl).add(prev);
            this.unit.type.applyColor(this.unit);
            Lines.stroke((float)region.height * Draw.scl * (float)Mathf.sign(this.type.flipSprite));
            Lines.line(region, prev.getX(), prev.getY(), tv.x, tv.y, false);
        }

        Draw.reset();
        Draw.z(z);
    }

    Position unitPosition() {
        return Tmp.v1.trns(this.unit.rotation - 90.0F, this.type.x, this.type.y).add(this.unit);
    }

    static {
        tv = Tmp.v2;
        tv2 = Tmp.v3;
    }

    public static class NewTentacleSegment {
        NewTentacleSegment child;
        NewTentacleSegment parent;
        NewTentacle main;
        float lx;
        float ly;
        float x;
        float y;
        float rotation;
        float vx;
        float vy;

        void updateLastPosition() {
            this.lx = this.x;
            this.ly = this.y;
        }

        float len() {
            return Mathf.len(this.x - this.lx, this.y - this.ly);
        }

        Position prevPos() {
            return (Position)(this.child == null ? this.main.unitPosition() : Tmp.v1.set(this.child.x, this.child.y));
        }
    }
}
