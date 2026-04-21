package unity.util;

import arc.Events;
import arc.func.Boolf;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.PQueue;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import java.util.Objects;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.PayloadConveyor;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.storage.CoreBlock;
import unity.mod.Triggers;
import unity.world.blocks.ConnectedBlock;

public class BlockMovement {
    public static Point2[] dirs = new Point2[]{new Point2(1, 0), new Point2(0, 1), new Point2(-1, 0), new Point2(0, -1)};
    public static Point2[][] origins = new Point2[16][];
    static ObjectMap<Building, BlockMovementUpdater> currentlyPushing = new ObjectMap();
    private static final Seq<Building> toRemove = new Seq();

    public static void init() {
        for(int size = 1; size <= 16; ++size) {
            int originx = 0;
            int originy = 0;
            originx += Mathf.floor((float)size / 2.0F);
            originy += Mathf.floor((float)size / 2.0F);
            originy -= size - 1;

            for(int side = 0; side < 4; ++side) {
                int ogx = originx;
                int ogy = originy;
                if (side != 0 && size > 1) {
                    for(int i = 1; i <= side; ++i) {
                        ogx += dirs[i].x * (size - 1);
                        ogy += dirs[i].y * (size - 1);
                    }
                }

                if (origins[size - 1] == null) {
                    origins[size - 1] = new Point2[4];
                }

                origins[size - 1][side] = new Point2(ogx, ogy);
            }
        }

        Triggers.listen(Trigger.update, BlockMovement::onUpdate);
        Events.on(EventType.WorldLoadEvent.class, (e) -> onMapLoad());
    }

    public static Point2 getNearbyPosition(Block block, int direction, int index) {
        Point2 tangent = dirs[(direction + 1) % 4];
        Point2 o = origins[block.size - 1][direction];
        return new Point2(o.x + tangent.x * index + dirs[direction].x, o.y + tangent.y * index + dirs[direction].y);
    }

    static boolean pushable(Building build) {
        return !(build.block instanceof CoreBlock) && !build.dead && !isBlockMoving(build);
    }

    static boolean isPayloadBlock(Building build) {
        return build != null && (build.block instanceof PayloadConveyor || build.block instanceof PayloadBlock);
    }

    static boolean tileAvalibleTo(Tile tile, Block block) {
        if (tile == null) {
            return false;
        } else if (tile.build != null) {
            return pushable(tile.build);
        } else if (!tile.solid() && tile.floor().placeableOn && (!block.requiresWater || tile.floor().liquidDrop == Liquids.water) && (!tile.floor().isDeep() || block.floating || block.requiresWater || block.placeableLiquid)) {
            return !block.solid && !block.solidifes || !Units.anyEntities((float)(tile.x * 8) + block.offset - (float)(block.size * 8) / 2.0F, (float)(tile.y * 8) + block.offset - (float)(block.size * 8) / 2.0F, (float)(block.size * 8), (float)(block.size * 8));
        } else {
            return false;
        }
    }

    static boolean canPush(Building build, int direction) {
        if (!pushable(build)) {
            return false;
        } else {
            Point2 tangent = dirs[(direction + 1) % 4];
            Point2 o = origins[build.block.size - 1][direction];

            for(int i = 0; i < build.block.size; ++i) {
                Tile t = build.tile.nearby(o.x + tangent.x * i + dirs[direction].x, o.y + tangent.y * i + dirs[direction].y);
                if (!tileAvalibleTo(t, build.block)) {
                    return false;
                }
            }

            Tile next = build.tile.nearby(dirs[direction].x, dirs[direction].y);
            return build.block.canPlaceOn(next, build.team);
        }
    }

    public static boolean pushSingle(Building build, int direction) {
        direction %= 4;
        if (build.block instanceof CoreBlock) {
            return false;
        } else {
            short bx = build.tile.x;
            short by = build.tile.y;
            build.tile.remove();
            if (!Build.validPlace(build.block, build.team, bx + dirs[direction].x, by + dirs[direction].y, build.rotation, false)) {
                Vars.world.tile(bx, by).setBlock(build.block, build.team, build.rotation, () -> build);
                return false;
            } else {
                Vars.world.tile(bx + dirs[direction].x, by + dirs[direction].y).setBlock(build.block, build.team, build.rotation, () -> build);
                return true;
            }
        }
    }

    static int project(Building build, int direction) {
        return (origins[build.block.size - 1][direction].x + build.tile.x) * dirs[direction].x + (origins[build.block.size - 1][direction].y + build.tile.y) * dirs[direction].y;
    }

    public static Seq<Building> getAllContacted(Building root, int direction, int max, Boolf<Building> bool) {
        PQueue<Building> queue = new PQueue(10, (a, bx) -> Math.round((float)(project(a, direction) - project(bx, direction))));
        queue.add(root);
        Seq<Building> contacts = null;

        while(!queue.empty() && (contacts == null || contacts.size <= max)) {
            Building next = (Building)queue.poll();
            if (contacts == null) {
                contacts = Seq.with(new Building[]{next});
            } else {
                contacts.add(next);
            }

            Point2 tangent = dirs[(direction + 1) % 4];
            Point2 o = origins[next.block.size - 1][direction];

            for(int i = 0; i < next.block.size; ++i) {
                Tile t = next.tile.nearby(o.x + tangent.x * i + dirs[direction].x, o.y + tangent.y * i + dirs[direction].y);
                Building b = t.build;
                if (b != null && Structs.indexOf(queue.queue, b) < 0 && !contacts.contains(b)) {
                    if (!pushable(b) || bool != null && !bool.get(b)) {
                        return null;
                    }

                    queue.add(b);
                    if (next instanceof ConnectedBlock) {
                        for(int dir = 0; dir < 4 && dir != direction; ++dir) {
                        }
                    }
                }
            }
        }

        return contacts != null && contacts.size <= max ? contacts : null;
    }

    public static boolean pushBlock(Building build, int direction, int maxBlocks, float speed, Boolf<Building> bool) {
        Seq<Building> pushing = getAllContacted(build, direction, maxBlocks, bool);
        if (pushing == null) {
            return false;
        } else {
            for(int i = pushing.size - 1; i >= 0; --i) {
                if (!canPush((Building)pushing.get(i), direction)) {
                    return false;
                }
            }

            for(int i = pushing.size - 1; i >= 0; --i) {
                pushSingle((Building)pushing.get(i), direction);
                if (speed > 0.0F) {
                    addPushedBlock((Building)pushing.get(i), direction, speed);
                }
            }

            return true;
        }
    }

    public static int pushOut(Building build, int x, int y, int direction, float speed, int max, Boolf<Building> bool, boolean waitPayload) {
        Tile tile = Vars.world.tile(x, y);
        if (tile.build == null) {
            if (!tileAvalibleTo(tile, build.block)) {
                return 0;
            } else {
                addPushedBlock(build, direction, speed);
                tile.setBlock(build.block, build.team, build.rotation, () -> build);
                return 2;
            }
        } else if (waitPayload && isPayloadBlock(tile.build)) {
            BuildPayload bp = new BuildPayload(build);
            bp.set((float)((x - dirs[direction].x) * 8), (float)((y - dirs[direction].y) * 8), 0.0F);
            if (tile.build.acceptPayload(build, bp)) {
                tile.build.handlePayload(build, bp);
                return 2;
            } else {
                return 1;
            }
        } else if (pushBlock(tile.build, direction, max, speed, bool)) {
            addPushedBlock(build, direction, speed);
            tile.setBlock(build.block, build.team, build.rotation, () -> build);
            return 2;
        } else {
            return 0;
        }
    }

    public static boolean isBlockMoving(Building build) {
        return currentlyPushing.containsKey(build);
    }

    static void addPushedBlock(Building build, int direction, float speed) {
        BlockMovementUpdater bmu = new BlockMovementUpdater(build, dirs[direction], 60.0F / speed, 0.0F, 0.0F, 0.0F);
        currentlyPushing.put(build, bmu);
        bmu.update();
    }

    public static void onUpdate() {
        currentlyPushing.each((b, animate) -> {
            animate.update();
            if (animate.isDead()) {
                toRemove.add(b);
            }

        });
        Seq var10000 = toRemove;
        ObjectMap var10001 = currentlyPushing;
        Objects.requireNonNull(var10001);
        var10000.each(var10001::remove);
        toRemove.clear();
    }

    public static void onMapLoad() {
        currentlyPushing.clear();
    }

    public static void onMapUnload() {
    }

    static class BlockMovementUpdater {
        Building build;
        Point2 dir;
        float delay;
        float timer;
        float ox;
        float oy;

        public BlockMovementUpdater(Building building, Point2 dir, float delay, float timer, float ox, float oy) {
            this.build = building;
            this.dir = dir;
            this.delay = delay;
            this.timer = timer;
            this.ox = ox;
            this.oy = oy;
        }

        public void update() {
            if (this.timer == 0.0F) {
                Building var10000 = this.build;
                var10000.x -= (float)(this.dir.x * 8);
                var10000 = this.build;
                var10000.y -= (float)(this.dir.y * 8);
                this.ox = this.build.x;
                this.oy = this.build.y;
            }

            this.timer += Time.delta;
            float progress = Math.min(1.0F, this.timer / this.delay);
            this.build.x = this.ox + (float)(this.dir.x * 8) * progress;
            this.build.y = this.oy + (float)(this.dir.y * 8) * progress;
        }

        public boolean isDead() {
            return this.timer > this.delay;
        }
    }
}
