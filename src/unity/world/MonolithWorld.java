package unity.world;

import arc.Events;
import arc.func.Cons;
import arc.func.Floatf;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.IntSet;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.world.Tile;
import unity.gen.FactionMeta;
import unity.mod.Faction;

public class MonolithWorld {
    public static final int chunkSize = 10;
    private Chunk[] chunks;
    private int width;
    private static float lastPriority;
    private static Chunk lastChunk;

    public MonolithWorld() {
        Events.on(EventType.WorldLoadEvent.class, (e) -> this.reload());
        Events.on(EventType.TileChangeEvent.class, (e) -> this.changed(e.tile));
    }

    public void reload() {
        this.width = Mathf.ceilPositive((float)Vars.world.width() / 10.0F) * 10;
        int h = Mathf.ceilPositive((float)Vars.world.height() / 10.0F) * 10;
        this.chunks = new Chunk[this.width * h];

        for(int y = 0; y < h; ++y) {
            for(int x = 0; x < this.width; ++x) {
                this.chunks[x + y * this.width] = new Chunk(x * 10, y * 10, Math.min(Vars.world.width() - x * 10, 10), Math.min(Vars.world.height() - y * 10, 10));
            }
        }

        for(Chunk chunk : this.chunks) {
            chunk.updateAll();
        }

    }

    public void changed(Tile tile) {
        Chunk chunk = this.getChunk(tile.x, tile.y);
        if (chunk != null) {
            chunk.update(tile);
        }

    }

    public Chunk getChunk(int x, int y) {
        return !Vars.world.tiles.in(x, y) ? null : this.chunks[x / 10 + y / 10 * this.width];
    }

    public Chunk getChunk(float x, float y) {
        return this.getChunk(World.toTile(x), World.toTile(y));
    }

    public void intersect(int x, int y, int width, int height, Cons<Chunk> cons) {
        width = Math.min(width, this.width - x);
        height = Math.min(height, this.chunks.length / this.width - y);
        int tx = Math.max(x / 10, 0);
        int ty = Math.max(y / 10, 0);
        int tw = Math.min(Mathf.ceilPositive((float)(x + width) / 10.0F) * 10, this.width);
        int th = Math.min(Mathf.ceilPositive((float)(y + height) / 10.0F) * 10, this.chunks.length / this.width);

        for(int cy = ty; cy < th; ++cy) {
            int pos = cy * this.width;

            for(int cx = tx; cx < tw; ++cx) {
                cons.get(this.chunks[cx + pos]);
            }
        }

    }

    public Chunk nearest(float x, float y, float range, Floatf<Chunk> priority) {
        lastChunk = null;
        lastPriority = 0.0F;
        int r = World.toTile(range) * 2;
        this.intersect(World.toTile(x), World.toTile(y), r, r, (c) -> {
            float p = priority.get(c);
            if (lastChunk == null || lastPriority < priority.get(c)) {
                lastPriority = p;
                lastChunk = c;
            }

        });
        return lastChunk;
    }

    public static class Chunk {
        public int x;
        public int y;
        public int width;
        public int height;
        public float centerX;
        public float centerY;
        public Seq<Tile> monolithTiles = new Seq();
        public IntSet monolithTilePos = new IntSet();

        public Chunk(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean within(Position pos) {
            return this.within(pos.getX(), pos.getY());
        }

        public boolean within(float x, float y) {
            return x >= World.unconv((float)this.x) && x <= World.unconv((float)this.width) && y >= World.unconv((float)this.y) && y <= World.unconv((float)this.height);
        }

        public void addMonolithTile(Tile tile) {
            if (this.monolithTilePos.add(tile.pos())) {
                this.monolithTiles.add(tile);
            }

        }

        public void removeMonolithTile(Tile tile) {
            if (this.monolithTilePos.remove(tile.pos())) {
                this.monolithTiles.remove(tile);
            }

        }

        public void update(Tile tile) {
            if (tile != null) {
                if (FactionMeta.map(tile.solid() && !tile.synthetic() ? tile.block() : tile.floor()) == Faction.monolith) {
                    this.addMonolithTile(tile);
                } else {
                    this.removeMonolithTile(tile);
                }

            }
        }

        public void updateAll() {
            this.centerX = World.unconv((float)this.x) + World.unconv((float)this.width) / 2.0F;
            this.centerY = World.unconv((float)this.y) + World.unconv((float)this.height) / 2.0F;
            this.monolithTiles.clear();
            this.monolithTilePos.clear();

            for(int y = this.y; y < this.height; ++y) {
                for(int x = this.x; x < this.width; ++x) {
                    this.update(Vars.world.tile(x, y));
                }
            }

        }
    }
}
