package unity.util;

import arc.Events;
import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Boolf3;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Cons3;
import arc.func.Cons4;
import arc.func.Floatc;
import arc.func.Floatc2;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Quat;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import java.util.Objects;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.EntityGroup;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import unity.graphics.UnityPal;

public final class Utils {
    public static final Interp.PowIn pow6In = new Interp.PowIn(6.0F);
    public static final Interp.PowOut pow25Out = new Interp.PowOut(25);
    public static final float sqrtHalf = Mathf.sqrt(0.5F);
    public static final Quat q1 = new Quat();
    public static final Quat q2 = new Quat();
    public static final Rand seedr = new Rand();
    public static final Rand seedr2 = new Rand();
    public static final Rand seedr3 = new Rand();
    private static final Vec2 tV = new Vec2();
    private static final Vec2 tV2 = new Vec2();
    private static final Seq<Healthc> tmpUnitSeq = new Seq();
    private static final IntSet collidedBlocks = new IntSet();
    private static final IntSet collidedEntities = new IntSet(204);
    private static final Rect rect = new Rect();
    private static final Rect rectAlt = new Rect();
    private static final Rect hitRect = new Rect();
    private static Posc result;
    private static float cdist;
    private static int idx;
    private static Tile furthest;
    private static Building tmpBuilding;
    private static Unit tmpUnit;
    private static boolean hit;
    private static boolean hitB;
    private static int randSeed = 1;
    private static final BoolGrid collideLineCollided = new BoolGrid();
    private static final IntSeq lineCast = new IntSeq();
    private static final IntSeq lineCastNext = new IntSeq();
    private static final Seq<Hit> hitEffects = new Seq();
    private static final Point2[][] d8d5;

    public static void init() {
        Events.on(EventType.WorldLoadEvent.class, (event) -> collideLineCollided.updateSize(Vars.world.width(), Vars.world.height()));
    }

    public static <T> T with(T inst, Cons<T> cons) {
        cons.get(inst);
        return inst;
    }

    public static <T extends Buildingc> Tile getBestTile(T build, int before, int after) {
        Tile tile = build.tile();
        int bound = before - after + 1;
        int offset = Mathf.floorPositive((float)bound / 2.0F);
        if (bound % 2 == 0 && after % 2 == 0) {
            --offset;
        }

        offset *= -1;
        int minScore = bound * bound * 2;
        Tile ctile = null;

        for(int i = offset; i < offset + bound; ++i) {
            for(int j = offset; j < offset + bound; ++j) {
                int max = Math.max(Math.abs(i), Math.abs(j));
                if (max < minScore && notSolid(tile, before, i, j)) {
                    minScore = max;
                    ctile = tile.nearby(i, j);
                }
            }
        }

        return ctile;
    }

    public static boolean notSolid(Tile tile, int size, int x, int y) {
        Tile ttile = tile.nearby(x, y);
        int off = Mathf.floorPositive((float)(size - 1) / 2.0F) * -1;

        for(int i = off; i < size + off; ++i) {
            for(int j = off; j < size + off; ++j) {
                Tile check = ttile.nearby(i, j);
                if (check.solid() && (check.build == null || check.build.tile != tile)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean hasBuilding(float wx, float wy, float range, Boolf<Building> pred) {
        collidedBlocks.clear();
        int tx = World.toTile(wx);
        int ty = World.toTile(wy);
        int tileRange = (int)(range / 8.0F + 1.0F);
        boolean any = false;

        for(int x = -tileRange + tx; x <= tileRange + tx; ++x) {
            for(int y = -tileRange + ty; y <= tileRange + ty; ++y) {
                if (Mathf.within((float)(x * 8), (float)(y * 8), wx, wy, range)) {
                    Building other = Vars.world.build(x, y);
                    if (other != null && pred.get(other) && collidedBlocks.add(other.pos())) {
                        any = true;
                        return any;
                    }
                }
            }
        }

        return any;
    }

    public static <T extends Entityc> T bestEntity(EntityGroup<T> group, Boolf<T> pred, Floatf<T> comp) {
        T best = null;
        float last = -Float.MAX_VALUE;
        float s = 0.0F;

        for(T t : group) {
            if (pred.get(t) && (best == null || last > (s = comp.get(t)))) {
                best = t;
                last = s;
            }
        }

        return best;
    }

    public static Bullet nearestBullet(float x, float y, float range, Boolf<Bullet> boolf) {
        result = null;
        cdist = range;
        Tmp.r1.setCentered(x, y, range * 2.0F);
        Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, (b) -> {
            float dst = b.dst(x, y);
            if (boolf.get(b) && b.within(x, y, range + b.hitSize) && (result == null || dst < cdist)) {
                result = b;
                cdist = dst;
            }

        });
        return (Bullet)result;
    }

    public static float angleDistSigned(float a, float b) {
        a += 360.0F;
        a %= 360.0F;
        b += 360.0F;
        b %= 360.0F;
        float d = Math.abs(a - b) % 360.0F;
        int sign = (!(a - b >= 0.0F) || !(a - b <= 180.0F)) && (!(a - b <= -180.0F) || !(a - b >= -360.0F)) ? -1 : 1;
        return (d > 180.0F ? 360.0F - d : d) * (float)sign;
    }

    public static float angleDistSigned(float a, float b, float start) {
        float dst = angleDistSigned(a, b);
        if (Math.abs(dst) > start) {
            return dst > 0.0F ? dst - start : dst + start;
        } else {
            return 0.0F;
        }
    }

    public static float angleDist(float a, float b) {
        float d = Math.abs(a - b) % 360.0F;
        return d > 180.0F ? 360.0F - d : d;
    }

    public static float clampedAngle(float angle, float relative, float limit) {
        if (limit >= 180.0F) {
            return angle;
        } else if (limit <= 0.0F) {
            return relative;
        } else {
            float dst = angleDistSigned(angle, relative);
            if (Math.abs(dst) > limit) {
                float val = dst > 0.0F ? dst - limit : dst + limit;
                return (angle - val) % 360.0F;
            } else {
                return angle;
            }
        }
    }

    public static float randomTriangularSeed(long seed) {
        seedr.setSeed(seed * 9999L);
        return seedr.nextFloat() - seedr.nextFloat();
    }

    public static void shotgunRange(int points, float range, float angle, Floatc cons) {
        if (points <= 1) {
            cons.get(angle);
        } else {
            for(int i = 0; i < points; ++i) {
                float in = Mathf.lerp(-range, range, (float)i / ((float)points - 1.0F));
                cons.get(in + angle);
            }

        }
    }

    public static float[] castCircle(float wx, float wy, float range, int rays, Boolf<Building> filter, Cons<Building> cons, Boolf<Tile> insulator) {
        collidedBlocks.clear();
        float[] cast = new float[rays];

        for(int i = 0; i < cast.length; ++i) {
            cast[i] = range;
            float ang = (float)i * (360.0F / (float)cast.length);
            tV.trns(ang, range).add(wx, wy);
            Vars.world.raycastEachWorld(wx, wy, tV.x, tV.y, (cx, cy) -> {
                Tile t = Vars.world.tile(cx, cy);
                if (t != null && t.block() != null && insulator.get(t)) {
                    float dst = t.dst(wx, wy);
                    cast[i] = dst;
                    return true;
                } else {
                    return false;
                }
            });
        }

        Vars.indexer.allBuildings(wx, wy, range, (build) -> {
            if (filter.get(build)) {
                float ang = Angles.angle(wx, wy, build.x, build.y);
                float dst = build.dst2(wx, wy) - build.hitSize() * build.hitSize() / 2.0F;
                int idx = Mathf.mod(Mathf.round(ang % 360.0F / (360.0F / (float)cast.length)), cast.length);
                float d = cast[idx];
                if (dst <= d * d) {
                    cons.get(build);
                }

            }
        });
        return cast;
    }

    public static float[] castConeTile(float wx, float wy, float range, float angle, float cone, int rays, Cons2<Building, Tile> consBuilding, Boolf<Tile> insulator) {
        return castConeTile(wx, wy, range, angle, cone, consBuilding, insulator, new float[rays]);
    }

    public static float[] castConeTile(float wx, float wy, float range, float angle, float cone, Cons2<Building, Tile> consBuilding, Boolf<Tile> insulator, float[] ref) {
        collidedBlocks.clear();
        idx = 0;
        float expand = 3.0F;
        rect.setCentered(wx, wy, expand);
        shotgunRange(3, cone, angle, (con) -> {
            tV.trns(con, range).add(wx, wy);
            rectAlt.setCentered(tV.x, tV.y, expand);
            rect.merge(rectAlt);
        });
        if (insulator != null) {
            shotgunRange(ref.length, cone, angle, (con) -> {
                tV.trns(con, range).add(wx, wy);
                ref[idx] = range * range;
                Vars.world.raycastEachWorld(wx, wy, tV.x, tV.y, (x, y) -> {
                    Tile tile = Vars.world.tile(x, y);
                    if (tile != null && insulator.get(tile)) {
                        ref[idx] = Mathf.dst2(wx, wy, (float)(x * 8), (float)(y * 8));
                        return true;
                    } else {
                        return false;
                    }
                });
                ++idx;
            });
        }

        int tx = Mathf.round(rect.x / 8.0F);
        int ty = Mathf.round(rect.y / 8.0F);
        int tw = tx + Mathf.round(rect.width / 8.0F);
        int th = ty + Mathf.round(rect.height / 8.0F);

        for(int x = tx; x <= tw; ++x) {
            for(int y = ty; y <= th; ++y) {
                float ofX = (float)(x * 8) - wx;
                float ofY = (float)(y * 8) - wy;
                int angIdx = Mathf.clamp(Mathf.round((angleDistSigned(Angles.angle(ofX, ofY), angle) + cone) / (cone * 2.0F) * (float)(ref.length - 1)), 0, ref.length - 1);
                float dst = ref[angIdx];
                float dst2 = Mathf.dst2(ofX, ofY);
                if (dst2 < dst && dst2 < range * range && angleDist(Angles.angle(ofX, ofY), angle) < cone) {
                    Tile tile = Vars.world.tile(x, y);
                    Building building = null;
                    if (tile != null) {
                        Building b = Vars.world.build(x, y);
                        if (b != null && !collidedBlocks.contains(b.id)) {
                            building = b;
                            collidedBlocks.add(b.id);
                        }

                        consBuilding.get(building, tile);
                    }
                }
            }
        }

        collidedBlocks.clear();
        return ref;
    }

    public static void castCone(float wx, float wy, float range, float angle, float cone, Cons4<Tile, Building, Float, Float> consTile, Cons3<Unit, Float, Float> consUnit) {
        collidedBlocks.clear();
        float expand = 3.0F;
        float rangeSquare = range * range;
        if (consTile != null) {
            rect.setCentered(wx, wy, expand);

            for(int i = 0; i < 3; ++i) {
                float angleC = (float)(-1 + i) * cone + angle;
                tV.trns(angleC, range).add(wx, wy);
                rectAlt.setCentered(tV.x, tV.y, expand);
                rect.merge(rectAlt);
            }

            int tx = Mathf.round(rect.x / 8.0F);
            int ty = Mathf.round(rect.y / 8.0F);
            int tw = tx + Mathf.round(rect.width / 8.0F);
            int th = ty + Mathf.round(rect.height / 8.0F);

            for(int x = tx; x <= tw; ++x) {
                for(int y = ty; y <= th; ++y) {
                    float temp = Angles.angle(wx, wy, (float)(x * 8), (float)(y * 8));
                    float tempDst = Mathf.dst((float)(x * 8), (float)(y * 8), wx, wy);
                    if (!(tempDst >= rangeSquare) && Angles.within(temp, angle, cone)) {
                        Tile other = Vars.world.tile(x, y);
                        if (other != null && !collidedBlocks.contains(other.pos())) {
                            float dst = 1.0F - tempDst / range;
                            float anDst = 1.0F - Angles.angleDist(temp, angle) / cone;
                            consTile.get(other, other.build, dst, anDst);
                            collidedBlocks.add(other.pos());
                        }
                    }
                }
            }
        }

        if (consUnit != null) {
            Groups.unit.intersect(wx - range, wy - range, range * 2.0F, range * 2.0F, (e) -> {
                float temp = Angles.angle(wx, wy, e.x, e.y);
                float tempDst = Mathf.dst(e.x, e.y, wx, wy);
                if (!(tempDst >= rangeSquare) && Angles.within(temp, angle, cone)) {
                    float dst = 1.0F - tempDst / range;
                    float anDst = 1.0F - Angles.angleDist(temp, angle) / cone;
                    consUnit.get(e, dst, anDst);
                }
            });
        }

    }

    public static void castCone(float wx, float wy, float range, float angle, float cone, Cons4<Tile, Building, Float, Float> consTile) {
        castCone(wx, wy, range, angle, cone, consTile, (Cons3)null);
    }

    public static void castCone(float wx, float wy, float range, float angle, float cone, Cons3<Unit, Float, Float> consUnit) {
        castCone(wx, wy, range, angle, cone, (Cons4)null, consUnit);
    }

    public static float offsetSin(float offset, float scl) {
        return Mathf.absin(Time.time + offset * (180F / (float)Math.PI), scl, 0.5F) + 0.5F;
    }

    public static float offsetSinB(float offset, float scl) {
        return Mathf.absin(Time.time + offset * (180F / (float)Math.PI), scl, 0.25F);
    }

    public static void trueEachBlock(float wx, float wy, float range, Cons<Building> cons) {
        trueEachBlock(wx, wy, range, (b) -> true, cons);
    }

    public static void trueEachBlock(float wx, float wy, float range, Boolf<Building> boolf, Cons<Building> cons) {
        collidedBlocks.clear();
        int tx = World.toTile(wx);
        int ty = World.toTile(wy);
        int tileRange = Mathf.floorPositive(range / 8.0F + 1.0F);
        int x = -tileRange + tx;

        for(int lenX = tileRange + tx; x <= lenX; ++x) {
            int y = -tileRange + ty;

            for(int lenY = tileRange + ty; y <= lenY; ++y) {
                if (Mathf.within((float)(x * 8), (float)(y * 8), wx, wy, range)) {
                    Building other = Vars.world.build(x, y);
                    if (other != null && boolf.get(other) && !collidedBlocks.contains(other.pos())) {
                        cons.get(other);
                        collidedBlocks.add(other.pos());
                    }
                }
            }
        }

    }

    public static float getBulletDamage(BulletType type) {
        return type.damage + type.splashDamage + Math.max(type.lightningDamage / 2.0F, 0.0F) * (float)type.lightning * (float)type.lightningLength;
    }

    public static Posc targetUnique(Team team, float x, float y, float radius, Posc[] targetArray) {
        result = null;
        float radiusSquare = radius * radius;
        cdist = radiusSquare + 1.0F;
        Posc[] tmpArray = new Posc[targetArray.length];
        int size = 0;

        for(Posc posc : targetArray) {
            if (posc != null) {
                tmpArray[size++] = posc;
            }
        }

        Units.nearbyEnemies(team, x - radius, y - radius, radius * 2.0F, radius * 2.0F, (unit) -> {
            float dst = unit.dst2(x, y);
            if (!Structs.contains(targetArray, unit) && dst < cdist && dst < radiusSquare) {
                result = unit;
                cdist = dst;
            }

        });
        if (result == null && size > 0) {
            result = tmpArray[Mathf.random(0, size - 1)];
        }

        return result;
    }

    public static float findLaserLength(float wx, float wy, float wx2, float wy2, Boolf<Tile> pred) {
        furthest = null;
        boolean found = Vars.world.raycast(World.toTile(wx), World.toTile(wy), World.toTile(wx2), World.toTile(wy2), (x, y) -> (furthest = Vars.world.tile(x, y)) != null && pred.get(furthest));
        return found && furthest != null ? Math.max(6.0F, Mathf.dst(wx, wy, furthest.worldx(), furthest.worldy())) : Mathf.dst(wx, wy, wx2, wy2);
    }

    public static Seq<Healthc> nearbyEnemySorted(Team team, float x, float y, float radius, float variance) {
        tmpUnitSeq.clear();
        Seq var10004 = tmpUnitSeq;
        Objects.requireNonNull(var10004);
        Units.nearbyEnemies(team, x, y, radius, var10004::add);
        Vars.indexer.allBuildings(x, y, radius, (b) -> {
            if (b.team != team) {
                tmpUnitSeq.add(b);
            }

        });
        ++randSeed;
        return tmpUnitSeq.sort((h) -> {
            float r = Mathf.randomSeedRange((long)(randSeed + h.id()), variance);
            return h.dst2(x, y) + r * r;
        });
    }

    public static boolean inTriangleCircle(float x1, float y1, float x2, float y2, float x3, float y3, float cx, float cy, float radius) {
        if (Intersector.isInTriangle(cx, cy, x1, y1, x2, y2, x3, y3)) {
            return true;
        } else if (radius <= 0.0F) {
            return false;
        } else if (Intersector.distanceSegmentPoint(x1, y1, x2, y2, cx, cy) <= radius) {
            return true;
        } else if (Intersector.distanceSegmentPoint(x2, y2, x3, y3, cx, cy) <= radius) {
            return true;
        } else {
            return Intersector.distanceSegmentPoint(x3, y3, x1, y1, cx, cy) <= radius;
        }
    }

    public static boolean inTriangleRect(float x1, float y1, float x2, float y2, float x3, float y3, Rect rect) {
        float cx = rect.x + rect.width / 2.0F;
        float cy = rect.y + rect.height / 2.0F;
        if (Intersector.isInTriangle(cx, cy, x1, y1, x2, y2, x3, y3)) {
            return true;
        } else if (rect.width <= 0.0F && rect.height <= 0.0F) {
            return false;
        } else if (!rect.contains(x1, y1) && !rect.contains(x2, y2) && !rect.contains(x3, y3)) {
            if (Geometry.raycastRect(x1, y1, x2, y2, rect) != null) {
                return true;
            } else if (Geometry.raycastRect(x2, y2, x3, y3, rect) != null) {
                return true;
            } else {
                return Geometry.raycastRect(x3, y3, x1, y1, rect) != null;
            }
        } else {
            return true;
        }
    }

    public static <T extends Posc> void inTriangle(EntityGroup<T> group, float x1, float y1, float x2, float y2, float x3, float y3, Boolf<T> filter, Cons<T> cons) {
        Rect r = rect.setCentered(x1, y1, 0.0F);
        r.merge(x2, y2);
        r.merge(x3, y3);
        group.intersect(r.x, r.y, r.width, r.height, (g) -> {
            if (filter.get(g) && inTriangleCircle(x1, y1, x2, y2, x3, y3, g.x(), g.y(), g instanceof Hitboxc ? ((Hitboxc)g).hitSize() / 2.0F : 0.0F)) {
                cons.get(g);
            }

        });
    }

    public static void inTriangleBuilding(Team team, boolean enemy, float x1, float y1, float x2, float y2, float x3, float y3, Boolf<Building> filter, Cons<Building> cons) {
        if (team != null && !enemy) {
            if (team.data().buildings != null) {
                Rect r = rect.setCentered(x1, y1, 0.0F);
                r.merge(x2, y2);
                r.merge(x3, y3);
                team.data().buildings.intersect(r, (b) -> {
                    if (filter.get(b)) {
                        b.hitbox(rectAlt);
                        int sz = b.block.size;
                        boolean hit = sz > 3 ? inTriangleRect(x1, y1, x2, y2, x3, y3, rectAlt) : inTriangleCircle(x1, y1, x2, y2, x3, y3, b.x, b.y, (float)(sz * 8) / 2.0F);
                        if (hit) {
                            cons.get(b);
                        }
                    }

                });
            }
        } else {
            Rect r = rect.setCentered(x1, y1, 0.0F);
            r.merge(x2, y2);
            r.merge(x3, y3);

            for(Teams.TeamData data : Vars.state.teams.present) {
                if (data.team != team && data.buildings != null) {
                    data.buildings.intersect(r, (b) -> {
                        if (filter.get(b)) {
                            b.hitbox(rectAlt);
                            int sz = b.block.size;
                            boolean hit = sz > 3 ? inTriangleRect(x1, y1, x2, y2, x3, y3, rectAlt) : inTriangleCircle(x1, y1, x2, y2, x3, y3, b.x, b.y, (float)(sz * 8) / 2.0F);
                            if (hit) {
                                cons.get(b);
                            }
                        }

                    });
                }
            }
        }

    }

    public static void collideLineLarge(Team team, float x, float y, float x2, float y2, float width, int segments, boolean sort, Boolf2<Sized, Vec2> within, HitHandler handler) {
        collidedEntities.clear();
        hitEffects.clear();

        for(Teams.TeamData data : Vars.state.teams.present) {
            if (data.team != team && data.buildings != null) {
                for(int i = 0; i < segments; ++i) {
                    float ofs = 1.0F / (float)segments;
                    float f = (float)i / (float)segments;
                    float sx = Mathf.lerp(x, x2, f);
                    float sy = Mathf.lerp(y, y2, f);
                    float sx2 = Mathf.lerp(x, x2, f + ofs);
                    float sy2 = Mathf.lerp(y, y2, f + ofs);
                    rect.set(sx, sy, 0.0F, 0.0F).merge(sx2, sy2).grow(width * 2.0F);
                    rectAlt.set(sx2, sy2, 0.0F, 0.0F).merge(Mathf.lerp(x, x2, f + ofs * 2.0F), Mathf.lerp(y, y2, f + ofs * 2.0F)).grow(width * 2.0F);
                    data.buildings.intersect(rect, (b) -> {
                        Vec2 v = Intersector.nearestSegmentPoint(x, y, x2, y2, b.x, b.y, tV);
                        if (within.get(b, v) && !collidedEntities.contains(b.id)) {
                            if (sort) {
                                Hit h = (Hit)Pools.obtain(Hit.class, Hit::new);
                                h.ent = b;
                                h.x = v.x;
                                h.y = v.y;
                                hitEffects.add(h);
                            } else {
                                handler.get(v.x, v.y, b, true);
                            }

                            b.hitbox(hitRect);
                            if (rectAlt.overlaps(hitRect)) {
                                collidedEntities.add(b.id);
                            }
                        }

                    });
                }
            }
        }

        for(int i = 0; i < segments; ++i) {
            float ofs = 1.0F / (float)segments;
            float f = (float)i / (float)segments;
            float sx = Mathf.lerp(x, x2, f);
            float sy = Mathf.lerp(y, y2, f);
            float sx2 = Mathf.lerp(x, x2, f + ofs);
            float sy2 = Mathf.lerp(y, y2, f + ofs);
            rect.set(sx, sy, 0.0F, 0.0F).merge(sx2, sy2).grow(width * 2.0F);
            rectAlt.set(sx2, sy2, 0.0F, 0.0F).merge(Mathf.lerp(x, x2, f + ofs * 2.0F), Mathf.lerp(y, y2, f + ofs * 2.0F)).grow(width * 2.0F);
            Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, (u) -> {
                if (u.team != team) {
                    Vec2 v = Intersector.nearestSegmentPoint(x, y, x2, y2, u.x, u.y, tV);
                    if (within.get(u, v) && !collidedEntities.contains(u.id)) {
                        if (sort) {
                            Hit h = (Hit)Pools.obtain(Hit.class, Hit::new);
                            h.ent = u;
                            h.x = v.x;
                            h.y = v.y;
                            hitEffects.add(h);
                        } else {
                            handler.get(v.x, v.y, u, true);
                        }

                        u.hitbox(hitRect);
                        if (rectAlt.overlaps(hitRect)) {
                            collidedEntities.add(u.id);
                        }
                    }

                }
            });
        }

        if (sort) {
            hit = false;
            hitEffects.sort((h) -> h.ent.dst2(x, y));
            hitEffects.removeAll((h) -> {
                if (!hit) {
                    hit = handler.get(h.x, h.y, h.ent, true);
                }

                Pools.free(h);
                return true;
            });
        }

        collidedEntities.clear();
    }

    public static void collideLineRawEnemyRatio(Team team, float x, float y, float x2, float y2, float width, Boolf3<Building, Float, Boolean> buildingCons, Boolf2<Unit, Float> unitCons, Floatc2 effectHandler) {
        float minRatio = 0.05F;
        collideLineRawEnemy(team, x, y, x2, y2, width, (building, direct) -> {
            float size = (float)(building.block.size * 8) / 2.0F;
            float dst = Mathf.clamp(1.0F - (Intersector.distanceSegmentPoint(x, y, x2, y2, building.x, building.y) - width) / size, minRatio, 1.0F);
            return buildingCons.get(building, dst, direct);
        }, (unit) -> {
            float size = unit.hitSize / 2.0F;
            float dst = Mathf.clamp(1.0F - (Intersector.distanceSegmentPoint(x, y, x2, y2, unit.x, unit.y) - width) / size, minRatio, 1.0F);
            return unitCons.get(unit, dst);
        }, effectHandler, true);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, boolean hitTiles, boolean hitUnits, boolean stopSort, HitHandler handler) {
        collideLineRawNew(x, y, x2, y2, width, width, (b) -> b.team != team, (u) -> u.team != team, hitTiles, hitUnits, (h) -> h.dst2(x, y), handler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, 3.0F, (b) -> b.team != team, (u) -> u.team != team, buildingCons, unitCons, (healthc) -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf<Healthc> pred, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, width, width, (b) -> b.team != team && pred.get(b), (u) -> u.team != team && pred.get(u), buildingCons, unitCons, (healthc) -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, width, width, (b) -> b.team != team, (u) -> u.team != team, buildingCons, unitCons, (healthc) -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, width, width, (b) -> b.team != team, (u) -> u.team != team, buildingCons, unitCons, sort, effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, unitWidth, tileWidth, (b) -> b.team != team, (u) -> u.team != team, buildingCons, unitCons, (healthc) -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, 3.0F, (b) -> b.team != team, (u) -> u.team != team, buildingCons, unitCons, (healthc) -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler) {
        collideLineRaw(x, y, x2, y2, 3.0F, (b) -> b.team != team, (u) -> u.team != team, buildingCons, unitCons, sort, effectHandler);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler) {
        collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, false);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
        Boolf<Unit> ucons = (unit) -> {
            unitCons.get(unit);
            return false;
        };
        collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons == null ? null : ucons, sort, effectHandler, stopSort);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
        collideLineRaw(x, y, x2, y2, unitWidth, 0.0F, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, stopSort);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
        collideLineRawNew(x, y, x2, y2, unitWidth, tileWidth, buildingFilter, unitFilter, buildingCons != null, unitCons != null, sort, (ex, ey, ent, direct) -> {
            boolean hit = false;
            if (unitCons != null && direct && ent instanceof Unit) {
                hit = unitCons.get((Unit)ent);
            }

            if (buildingCons != null && ent instanceof Building) {
                hit = buildingCons.get((Building)ent, direct);
            }

            if (effectHandler != null && direct) {
                effectHandler.get(ex, ey);
            }

            return hit;
        }, stopSort);
    }

    public static void collideLineRawNew(float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, boolean hitTile, boolean hitUnit, Floatf<Healthc> sort, HitHandler hitHandler, boolean stopSort) {
        hitEffects.clear();
        lineCast.clear();
        lineCastNext.clear();
        collidedBlocks.clear();
        tV.set(x2, y2);
        if (hitTile) {
            collideLineCollided.clear();
            Runnable cast = () -> {
                hitB = false;
                lineCast.each((i) -> {
                    int tx = Point2.x(i);
                    int ty = Point2.y(i);
                    Building build = Vars.world.build(tx, ty);
                    boolean hit = false;
                    if (build != null && (buildingFilter == null || buildingFilter.get(build)) && collidedBlocks.add(build.pos())) {
                        if (sort == null) {
                            hit = hitHandler.get((float)(tx * 8), (float)(ty * 8), build, true);
                        } else {
                            hit = hitHandler.get((float)(tx * 8), (float)(ty * 8), build, false);
                            Hit he = (Hit)Pools.obtain(Hit.class, Hit::new);
                            he.ent = build;
                            he.x = (float)(tx * 8);
                            he.y = (float)(ty * 8);
                            hitEffects.add(he);
                        }

                        if (hit && !hitB) {
                            tV.trns(Angles.angle(x, y, x2, y2), Mathf.dst(x, y, build.x, build.y)).add(x, y);
                            hitB = true;
                        }
                    }

                    Vec2 segment = Intersector.nearestSegmentPoint(x, y, tV.x, tV.y, (float)(tx * 8), (float)(ty * 8), tV2);
                    if (!hit && tileWidth > 0.0F) {
                        for(Point2 p : Geometry.d8) {
                            int newX = p.x + tx;
                            int newY = p.y + ty;
                            boolean within = !hitB || Mathf.within(x / 8.0F, y / 8.0F, (float)newX, (float)newY, tV.dst(x, y) / 8.0F);
                            if (segment.within((float)(newX * 8), (float)(newY * 8), tileWidth) && collideLineCollided.within(newX, newY) && !collideLineCollided.get(newX, newY) && within) {
                                lineCastNext.add(Point2.pack(newX, newY));
                                collideLineCollided.set(newX, newY, true);
                            }
                        }
                    }

                });
                lineCast.clear();
                lineCast.addAll(lineCastNext);
                lineCastNext.clear();
            };
            Vars.world.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
                if (collideLineCollided.within(cx, cy) && !collideLineCollided.get(cx, cy)) {
                    lineCast.add(Point2.pack(cx, cy));
                    collideLineCollided.set(cx, cy, true);
                }

                cast.run();
                return hitB;
            });

            while(!lineCast.isEmpty()) {
                cast.run();
            }
        }

        if (hitUnit) {
            rect.setPosition(x, y).setSize(tV.x - x, tV.y - y);
            if (rect.width < 0.0F) {
                Rect var10000 = rect;
                var10000.x += rect.width;
                var10000 = rect;
                var10000.width *= -1.0F;
            }

            if (rect.height < 0.0F) {
                Rect var15 = rect;
                var15.y += rect.height;
                var15 = rect;
                var15.height *= -1.0F;
            }

            rect.grow(unitWidth * 2.0F);
            Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, (unit) -> {
                if (unitFilter == null || unitFilter.get(unit)) {
                    unit.hitbox(hitRect);
                    hitRect.grow(unitWidth * 2.0F);
                    Vec2 vec = Geometry.raycastRect(x, y, tV.x, tV.y, hitRect);
                    if (vec != null) {
                        float scl = (unit.hitSize - unitWidth) / unit.hitSize;
                        vec.sub(unit).scl(scl).add(unit);
                        if (sort == null) {
                            hitHandler.get(vec.x, vec.y, unit, true);
                        } else {
                            Hit he = (Hit)Pools.obtain(Hit.class, Hit::new);
                            he.ent = unit;
                            he.x = vec.x;
                            he.y = vec.y;
                            hitEffects.add(he);
                        }
                    }
                }

            });
        }

        if (sort != null) {
            hit = false;
            hitEffects.sort((he) -> sort.get(he.ent)).each((he) -> {
                if (!stopSort || !hit) {
                    hit = hitHandler.get(he.x, he.y, he.ent, true);
                }

                Pools.free(he);
            });
        }

        hitEffects.clear();
    }

    /** @deprecated */
    @Deprecated
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf<Building> buildC, Cons<Unit> unitC, Effect effect) {
        collideLineRaw(x, y, x2, y2, (b) -> b.team != team, (u) -> u.team != team, buildC, unitC, (unit) -> unit.dst2(x, y), effect);
    }

    /** @deprecated */
    @Deprecated
    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC) {
        collideLineRaw(x, y, x2, y2, buildB, unitB, buildC, unitC, (Floatf)null, (Effect)null);
    }

    /** @deprecated */
    @Deprecated
    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC, Floatf<Healthc> sort, Effect effect) {
        collideLineRaw(x, y, x2, y2, buildB, unitB, buildC, unitC, sort, (e) -> false, effect);
    }

    /** @deprecated */
    @Deprecated
    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC, Floatf<Healthc> sort, Boolf<Building> buildAlt, Effect effect) {
        collidedBlocks.clear();
        tmpUnitSeq.clear();
        tV.set(x2, y2);
        if (buildC != null) {
            Vars.world.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
                Building tile = Vars.world.build(cx, cy);
                if (tile != null && (buildB == null || buildB.get(tile)) && !collidedBlocks.contains(tile.pos())) {
                    boolean s;
                    if (sort == null) {
                        s = buildC.get(tile);
                    } else {
                        tmpUnitSeq.add(tile);
                        s = buildAlt.get(tile);
                    }

                    collidedBlocks.add(tile.pos());
                    if (effect != null) {
                        effect.at((float)(cx * 8), (float)(cy * 8));
                    }

                    if (s) {
                        tV.trns(Angles.angle(x, y, x2, y2), Mathf.dst(x, y, tile.x, tile.y));
                        tV.add(x, y);
                        return true;
                    }
                }

                return false;
            });
        }

        if (unitB != null && unitC != null) {
            rect.setPosition(x, y).setSize(tV.x - x, tV.y - y);
            if (rect.width < 0.0F) {
                Rect var10000 = rect;
                var10000.x += rect.width;
                var10000 = rect;
                var10000.width *= -1.0F;
            }

            if (rect.height < 0.0F) {
                Rect var13 = rect;
                var13.y += rect.height;
                var13 = rect;
                var13.height *= -1.0F;
            }

            float expand = 2.0F;
            rect.grow(expand * 2.0F);
            if (sort == null) {
                Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, (unit) -> {
                    if (unitB.get(unit)) {
                        unit.hitbox(hitRect);
                        hitRect.grow(expand * 2.0F);
                        Vec2 vec = Geometry.raycastRect(x, y, tV.x, tV.y, hitRect);
                        if (vec != null) {
                            if (effect != null) {
                                effect.at(vec.x, vec.y);
                            }

                            unitC.get(unit);
                        }
                    }

                });
            } else {
                Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, (unit) -> {
                    if (unitB.get(unit)) {
                        unit.hitbox(hitRect);
                        hitRect.grow(expand * 2.0F);
                        Vec2 vec = Geometry.raycastRect(x, y, tV.x, tV.y, hitRect);
                        if (vec != null) {
                            if (effect != null) {
                                effect.at(vec.x, vec.y);
                            }

                            tmpUnitSeq.add(unit);
                        }
                    }

                });
                hit = false;
                tmpUnitSeq.sort(sort).each((e) -> {
                    if (e instanceof Building && buildC != null && !hit) {
                        hit = buildC.get((Building)e);
                    }

                    if (e instanceof Unit) {
                        unitC.get((Unit)e);
                    }

                });
                tmpUnitSeq.clear();
            }
        }

    }

    public static void collideLineDamageOnly(Team team, float damage, float x, float y, float angle, float length, Bullet hitter) {
        collidedBlocks.clear();
        tV.trns(angle, length);
        if (hitter.type.collidesGround) {
            Vars.world.raycastEachWorld(x, y, x + tV.x, y + tV.y, (cx, cy) -> {
                Building tile = Vars.world.build(cx, cy);
                if (tile != null && !collidedBlocks.contains(tile.pos()) && tile.team != team) {
                    tile.damage(damage);
                    collidedBlocks.add(tile.pos());
                }

                return false;
            });
        }

        rect.setPosition(x, y).setSize(tV.x, tV.y);
        float x2 = tV.x + x;
        float y2 = tV.y + y;
        if (rect.width < 0.0F) {
            Rect var10000 = rect;
            var10000.x += rect.width;
            var10000 = rect;
            var10000.width *= -1.0F;
        }

        if (rect.height < 0.0F) {
            Rect var11 = rect;
            var11.y += rect.height;
            var11 = rect;
            var11.height *= -1.0F;
        }

        float expand = 3.0F;
        Rect var13 = rect;
        var13.y -= expand;
        var13 = rect;
        var13.x -= expand;
        var13 = rect;
        var13.width += expand * 2.0F;
        var13 = rect;
        var13.height += expand * 2.0F;
        Units.nearbyEnemies(team, rect, (unit) -> {
            if (unit.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) {
                unit.hitbox(hitRect);
                Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitRect.grow(expand * 2.0F));
                if (vec != null) {
                    unit.damage(damage);
                }

            }
        });
    }

    public static void chanceMultiple(float chance, Runnable run) {
        int intC = Mathf.ceil(chance);
        float tmp = chance;

        for(int i = 0; i < intC; ++i) {
            if (tmp >= 1.0F) {
                run.run();
                --tmp;
            } else if (tmp > 0.0F && Mathf.chance((double)tmp)) {
                run.run();
            }
        }

    }

    public static float linear(float current, float target, float maxTorque, float coefficient) {
        current = Math.min(target, current);
        return Math.min(coefficient * (target - current) * maxTorque / target, 99999.0F);
    }

    public static Color tempColor(float temp) {
        if (temp > 273.15F) {
            float a = Math.max(0.0F, (temp - 498.0F) * 0.001F);
            if (a < 0.01F) {
                return Color.clear.cpy();
            } else {
                Color fcol = Pal.turretHeat.cpy().a(a);
                if (a > 1.0F) {
                    fcol.b += 0.01F * a;
                    fcol.mul(a);
                }

                return fcol;
            }
        } else {
            float a = 1.0F - Mathf.clamp(temp / 273.15F);
            return a < 0.01F ? Color.clear.cpy() : UnityPal.coldColor.cpy().a(a);
        }
    }

    public static IntSeq unpackInts(IntSeq intpack) {
        IntSeq out = new IntSeq();
        int i = 0;

        for(int len = intpack.size * 2; i < len; ++i) {
            int cint = intpack.get(i / 2);
            int value = cint >>> (i % 2 == 0 ? 0 : 16) & '\uffff';
            int am = value >> 8 & 255;

            for(int k = 0; k < am; ++k) {
                out.add(value & 255);
            }
        }

        return out;
    }

    public static IntSeq unpackIntsFromString(String sintpack) {
        IntSeq out = new IntSeq();
        int i = 0;

        for(int len = sintpack.length(); i < len; i += 2) {
            int val = sintpack.codePointAt(i + 1);
            int am = sintpack.codePointAt(i);

            for(int k = 0; k < am; ++k) {
                out.add(val);
            }
        }

        return out;
    }

    public static Healthc linecast(Bullet hitter, float x, float y, float angle, float length) {
        tV.trns(angle, length);
        tmpBuilding = null;
        if (hitter.type.collidesGround) {
            Vars.world.raycastEachWorld(x, y, x + tV.x, y + tV.y, (cx, cy) -> {
                Building tile = Vars.world.build(cx, cy);
                if (tile != null && tile.team != hitter.team) {
                    tmpBuilding = tile;
                    return true;
                } else {
                    return false;
                }
            });
        }

        rect.setPosition(x, y).setSize(tV.x, tV.y);
        float x2 = tV.x + x;
        float y2 = tV.y + y;
        if (rect.width < 0.0F) {
            Rect var10000 = rect;
            var10000.x += rect.width;
            var10000 = rect;
            var10000.width *= -1.0F;
        }

        if (rect.height < 0.0F) {
            Rect var9 = rect;
            var9.y += rect.height;
            var9 = rect;
            var9.height *= -1.0F;
        }

        float expand = 3.0F;
        Rect var11 = rect;
        var11.y -= expand;
        var11 = rect;
        var11.x -= expand;
        var11 = rect;
        var11.width += expand * 2.0F;
        var11 = rect;
        var11.height += expand * 2.0F;
        tmpUnit = null;
        Units.nearbyEnemies(hitter.team, rect, (e) -> {
            if ((tmpUnit == null || !(e.dst2(x, y) > tmpUnit.dst2(x, y))) && e.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) {
                e.hitbox(hitRect);
                Rect other = hitRect;
                other.y -= expand;
                other.x -= expand;
                other.width += expand * 2.0F;
                other.height += expand * 2.0F;
                Vec2 vec = Geometry.raycastRect(x, y, x2, y2, other);
                if (vec != null) {
                    tmpUnit = e;
                }

            }
        });
        if (tmpBuilding != null && tmpUnit != null) {
            if (Mathf.dst2(x, y, tmpBuilding.getX(), tmpBuilding.getY()) <= Mathf.dst2(x, y, tmpUnit.getX(), tmpUnit.getY())) {
                return tmpBuilding;
            }
        } else if (tmpBuilding != null) {
            return tmpBuilding;
        }

        return tmpUnit;
    }

    static {
        d8d5 = new Point2[][]{{Geometry.d4[0], Geometry.d8edge[0], Geometry.d8edge[3], Geometry.d4[1], Geometry.d4[3]}, {Geometry.d8edge[3], Geometry.d4[0], Geometry.d4[3], Geometry.d8edge[0], Geometry.d8edge[2]}, {Geometry.d4[3], Geometry.d8edge[3], Geometry.d8edge[2], Geometry.d4[0], Geometry.d4[2]}, {Geometry.d8edge[2], Geometry.d4[3], Geometry.d4[2], Geometry.d8edge[3], Geometry.d8edge[1]}, {Geometry.d4[2], Geometry.d8edge[2], Geometry.d8edge[1], Geometry.d4[3], Geometry.d4[1]}, {Geometry.d8edge[1], Geometry.d4[2], Geometry.d4[1], Geometry.d8edge[2], Geometry.d8edge[0]}, {Geometry.d4[1], Geometry.d8edge[1], Geometry.d8edge[0], Geometry.d4[2], Geometry.d4[0]}, {Geometry.d8edge[0], Geometry.d4[1], Geometry.d4[0], Geometry.d8edge[1], Geometry.d8edge[3]}};
    }

    static class Hit implements Pool.Poolable {
        Healthc ent;
        float x;
        float y;

        public void reset() {
            this.ent = null;
            this.x = this.y = 0.0F;
        }
    }

    public interface HitHandler {
        boolean get(float var1, float var2, Healthc var3, boolean var4);
    }
}
