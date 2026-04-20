package unity.ai;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.entities.units.UnitCommand;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;
import unity.type.UnityUnitType;
import unity.util.Utils;

public class SmartGroundAI extends AIController {
    static float tmpAngle = 0.0F;
    static float tmpAngleB;
    Seq<WeaponMount> mainMounts = new Seq();
    float mainMountsRange = -1.0F;
    FloatSeq score = new FloatSeq();
    FloatSeq tmpf = new FloatSeq();
    Seq<Healthc> targets = new Seq();
    Seq<Healthc> tmp = new Seq();
    Healthc[] targetArray;
    QuadTree<QuadTree.QuadTreeObject> tree;
    IntSet occupied;
    float retarget;
    float aimX;
    float aimY;
    int targetIdx;
    boolean targeting;
    boolean set;

    public SmartGroundAI() {
        this.tree = new QuadTree(new Rect(-250.0F, -250.0F, (float)(Vars.world.width() * 8) + 500.0F, (float)(Vars.world.height() * 8) + 500.0F));
        this.occupied = new IntSet();
        this.targeting = false;
        this.set = false;
    }

    float angDst(float dst, float width) {
        return Angles.angle(dst, -width) % 180.0F;
    }

    void updateScoring() {
        boolean tmr = this.timer.get(1, 20.0F);
        if (tmr) {
            this.tree.clear();
        }

        UnitType t = this.unit.type;
        this.tmp.clear();
        this.score.clear();
        tmpAngle = -361.0F;
        tmpAngleB = -361.0F;
        this.targeting = false;
        this.set = false;
        if (!this.targets.isEmpty()) {
            this.targets.removeAll((h) -> {
                boolean invalid = !h.isAdded() || Units.invalidateTarget(h, this.unit.team, this.unit.x, this.unit.y, t.maxRange);
                if (!invalid) {
                    if (this.unit.within(h, this.mainMountsRange)) {
                        if (h instanceof Unit) {
                            Unit u = (Unit)h;
                            this.tmpf.add(u.x, u.y, u.health + u.type.dpsEstimate);
                            this.updateScore(this.unit.angleTo(u), this.unit.dst(u), false);
                        } else if (h instanceof Building) {
                            Building b = (Building)h;
                            float sc = b.health;
                            if (b instanceof Turret.TurretBuild) {
                                Turret.TurretBuild tr = (Turret.TurretBuild)b;
                                Turret tt = (Turret)b.block;
                                sc += tr.hasAmmo() ? tr.peekAmmo().estimateDPS() / tt.reloadTime * (float)tt.shots : 0.0F;
                            } else if (b instanceof Wall.WallBuild) {
                                sc *= -1.0F;
                            } else if (b instanceof CoreBlock.CoreBuild) {
                                sc *= 10.0F;
                            }

                            this.tmpf.add(b.x, b.y, sc);
                            this.updateScore(this.unit.angleTo(b), this.unit.dst(b), false);
                        }

                        float an = this.unit.angleTo(h);
                        if (tmpAngleB == -361.0F) {
                            tmpAngleB = an;
                        }

                        tmpAngle = an;
                    }

                    if (tmr) {
                        this.tree.insert((QuadTree.QuadTreeObject)h);
                    }
                }

                return invalid;
            });
            this.updateScore(-1.0F, -1.0F, true);
            if (!this.targets.isEmpty()) {
                if (this.retarget <= 0.0F || this.targetIdx + 2 >= this.score.size) {
                    float ls = -Float.MAX_VALUE;

                    for(int i = 0; i < this.score.size; i += 3) {
                        float s = this.score.items[i + 2];
                        if (s > ls) {
                            this.targetIdx = i;
                            ls = s;
                        }
                    }

                    this.retarget = 120.0F;
                }

                float tx = this.score.items[this.targetIdx];
                float ty = this.score.items[this.targetIdx + 1];

                for(WeaponMount m : this.mainMounts) {
                    this.targeting |= m.weapon.bullet.damage >= this.score.items[this.targetIdx + 2] * 2.0F;
                    m.aimX = tx + this.unit.x;
                    m.aimY = ty + this.unit.y;
                    m.rotate = m.shoot = true;
                }

                this.aimX = tx;
                this.aimY = ty;
                this.set = true;
            } else {
                for(WeaponMount m : this.mainMounts) {
                    m.rotate = m.shoot = false;
                }
            }
        }

        this.retarget = Math.max(this.retarget - Time.delta, 0.0F);
        if (tmr) {
            this.occupied.clear();

            for(int i = 0; i < this.unit.mounts.length; ++i) {
                WeaponMount m = this.unit.mounts[i];
                Weapon w = this.unit.mounts[i].weapon;
                if (w.rotate && !(w.rotateSpeed <= 1.0F) && m.target == null) {
                    this.tmp.clear();
                    float weaponRotation = this.unit.rotation + m.rotation;
                    float mountX = this.unit.x + Angles.trnsx(this.unit.rotation - 90.0F, w.x, w.y);
                    float mountY = this.unit.y + Angles.trnsy(this.unit.rotation - 90.0F, w.x, w.y);
                    float range = w.bullet.range();
                    Rect r = Tmp.r1.setCentered(mountX, mountY, range * 2.0F);
                    this.tree.intersect(r, (q) -> {
                        Healthc h = (Healthc)q;
                        if (h.within(mountX, mountY, range + ((Sized)q).hitSize() / 2.0F)) {
                            this.tmp.add(h);
                        }

                    });
                    if (!this.tmp.isEmpty()) {
                        this.tmp.sort((h) -> {
                            float angScore = Mathf.clamp(Utils.angleDist(weaponRotation, h.angleTo(mountX, mountY) + 180.0F) / 180.0F);
                            angScore += (1.0F - angScore) * 0.5F;
                            float score = 0.0F;
                            float dst = h.dst(mountX, mountY);
                            if (this.occupied.contains(h.id())) {
                                score += 1000000.0F;
                            }

                            if (h instanceof Unit && this.angDst(dst, this.unit.deltaLen() * 2.0F) <= w.rotateSpeed) {
                                score += 1000000.0F;
                            }

                            return dst * angScore + score;
                        });
                        Healthc target = (Healthc)this.tmp.first();
                        this.occupied.add(target.id());
                        m.target = (Teamc)target;
                    }
                }
            }
        }

        for(WeaponMount m : this.unit.mounts) {
            Weapon w = m.weapon;
            if (w.rotate && !(w.rotateSpeed <= 1.0F)) {
                m.rotate = m.shoot = false;
                float mountX = this.unit.x + Angles.trnsx(this.unit.rotation - 90.0F, w.x, w.y);
                float mountY = this.unit.y + Angles.trnsy(this.unit.rotation - 90.0F, w.x, w.y);
                if (Units.invalidateTarget(m.target, this.unit.team, mountX, mountY, m.weapon.bullet.range())) {
                    m.target = null;
                }

                if (m.target != null) {
                    if (w.predictTarget) {
                        Vec2 to = Predict.intercept(this.unit, m.target, w.bullet.speed);
                        m.aimX = to.x;
                        m.aimY = to.y;
                    } else {
                        m.aimX = m.target.x();
                        m.aimY = m.target.y();
                    }

                    m.rotate = m.shoot = true;
                }
            }
        }

    }

    public void updateTargeting() {
        if (this.targetArray == null || this.targetArray.length != this.unit.mounts.length) {
            this.targetArray = new Healthc[this.unit.mounts.length];
        }

        UnitType t = this.unit.type;
        if (this.timer.get(0, 15.0F)) {
            this.targets.clear();
            Rect r = Tmp.r1.setCentered(this.unit.x, this.unit.y, t.maxRange * 2.0F);
            Groups.unit.intersect(r.x, r.y, r.x + r.width, r.y + r.height, (u) -> {
                if (u.team != this.unit.team && this.unit.within(u, t.maxRange + u.hitSize / 2.0F)) {
                    this.targets.add(u);
                }

            });
            Vars.indexer.allBuildings(this.unit.x, this.unit.y, t.maxRange, (b) -> {
                if (b.team != this.unit.team) {
                    this.targets.add(b);
                }

            });
            this.targets.sort((u) -> Utils.angleDistSigned(this.unit.rotation, this.unit.angleTo(u)));
        }

        this.updateScoring();
    }

    void updateScore(float rotation, float dst, boolean finalize) {
        float adst = this.angDst(dst, ((UnityUnitType)this.unit.type).bulletWidth / 2.0F);
        if (tmpAngleB != 361.0F && Utils.angleDist(tmpAngleB, rotation) > adst * 2.0F) {
            finalize = true;
        }

        if (finalize || tmpAngle != 361.0F && Utils.angleDist(tmpAngle, rotation) > adst) {
            float fx = 0.0F;
            float fy = 0.0F;
            float fs = 0.0F;
            int size = 0;

            for(int i = 0; i < this.tmpf.size; i += 3) {
                fx += this.tmpf.items[i] - this.unit.x;
                fy += this.tmpf.items[i + 1] - this.unit.y;
                fs += this.tmpf.items[i + 2];
                ++size;
            }

            this.score.add(fx / (float)size, fy / (float)size, fs);
            this.tmpf.clear();
            tmpAngleB = rotation;
        }

    }

    public void updateMovement() {
        if (!this.targeting) {
            Building core = this.unit.closestEnemyCore();
            if ((core == null || !this.unit.within(core, this.unit.type.range * 0.5F)) && this.command() == UnitCommand.attack) {
                boolean move = true;
                if (Vars.state.rules.waves && this.unit.team == Vars.state.rules.defaultTeam) {
                    Tile spawner = this.getClosestSpawner();
                    if (spawner != null && this.unit.within(spawner, Vars.state.rules.dropZoneRadius + 120.0F)) {
                        move = false;
                    }
                }

                if (move) {
                    this.pathfind(0);
                }
            }

            if (this.command() == UnitCommand.rally) {
                Teamc target = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);
                if (target != null && !this.unit.within(target, 70.0F)) {
                    this.pathfind(1);
                }
            }
        }

        if (this.unit.type.canBoost && this.unit.elevation > 0.001F && !this.unit.onSolid()) {
            this.unit.elevation = Mathf.approachDelta(this.unit.elevation, 0.0F, this.unit.type.riseSpeed);
        }

        this.faceTarget();
    }

    public void faceTarget() {
        if (!this.set) {
            super.faceTarget();
        } else {
            this.unit.lookAt(Angles.angle(this.aimX, this.aimY));
        }

    }

    public void init() {
        Core.app.post(() -> {
            this.mainMounts.clear();

            for(WeaponMount m : this.unit.mounts) {
                if (!m.weapon.rotate || m.weapon.rotateSpeed <= 1.0F) {
                    this.mainMounts.add(m);
                    if (this.mainMountsRange < 0.0F || m.weapon.bullet.range() < this.mainMountsRange) {
                        this.mainMountsRange = m.weapon.bullet.range();
                    }
                }
            }

        });
    }
}
