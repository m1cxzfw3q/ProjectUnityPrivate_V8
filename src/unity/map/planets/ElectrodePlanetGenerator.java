package unity.map.planets;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.ai.BaseRegistry;
import mindustry.content.Blocks;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.game.Waves;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;
import unity.content.UnityBlocks;

public class ElectrodePlanetGenerator extends PlanetGenerator {
    BaseGenerator basegen = new BaseGenerator();
    float scl = 6.0F;
    float waterOffset = 0.07F;
    Block[][] arr;
    float waterf;

    public ElectrodePlanetGenerator() {
        this.arr = new Block[][]{{Blocks.metalFloor2, UnityBlocks.electroTile, Blocks.darkMetal, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, Blocks.ice, Blocks.ice, Blocks.ice}, {Blocks.metalFloor2, Blocks.ice, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, Blocks.darkMetal, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, Blocks.darkMetal, UnityBlocks.electroTile, Blocks.ice, Blocks.ice}, {Blocks.water, Blocks.metalFloor2, Blocks.darksand, Blocks.darkMetal, Blocks.deepwater, Blocks.deepwater, Blocks.darkMetal, Blocks.deepwater, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, UnityBlocks.electroTile, Blocks.ice}, {Blocks.water, Blocks.metalFloor2, Blocks.metalFloor2, Blocks.darkMetal, Blocks.darksand, Blocks.darksand, Blocks.darkMetal, Blocks.deepwater, Blocks.darkMetal, Blocks.deepwater, Blocks.darkMetal, Blocks.deepwater, UnityBlocks.electroTile}, {Blocks.deepwater, Blocks.water, Blocks.metalFloor2, Blocks.darksand, Blocks.darksand, Blocks.darkMetal, Blocks.darkMetal, Blocks.darkMetal, Blocks.darkMetal, Blocks.basalt, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile}, {Blocks.deepwater, Blocks.water, Blocks.metalFloor2, Blocks.darkMetal, Blocks.darksand, Blocks.darkMetal, Blocks.darkMetal, Blocks.darkMetal, Blocks.basalt, Blocks.basalt, Blocks.darkMetal, Blocks.deepwater, Blocks.deepwater}, {Blocks.deepwater, Blocks.water, Blocks.metalFloor2, Blocks.darksand, Blocks.darkMetal, Blocks.darkMetal, Blocks.basalt, Blocks.darkMetal, Blocks.darkMetal, Blocks.basalt, Blocks.darkMetal, Blocks.basalt, Blocks.deepwater}, {Blocks.deepwater, Blocks.water, Blocks.metalFloor2, Blocks.darksand, Blocks.darksand, Blocks.darkMetal, Blocks.basalt, Blocks.darkMetal, Blocks.basalt, Blocks.basalt, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile}, {Blocks.water, Blocks.metalFloor2, Blocks.metalFloor2, Blocks.darksand, Blocks.darksand, Blocks.darkMetal, Blocks.darkMetal, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile}, {Blocks.water, Blocks.metalFloor2, Blocks.darksand, Blocks.darkMetal, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, Blocks.darkMetal, Blocks.ice}, {Blocks.metalFloor2, Blocks.ice, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, Blocks.darkMetal, UnityBlocks.electroTile, Blocks.deepwater, Blocks.darkMetal, UnityBlocks.electroTile, Blocks.ice, Blocks.ice}, {Blocks.metalFloor2, UnityBlocks.electroTile, Blocks.deepwater, Blocks.deepwater, Blocks.darkMetal, Blocks.deepwater, UnityBlocks.electroTile, Blocks.deepwater, Blocks.deepwater, UnityBlocks.electroTile, Blocks.ice, Blocks.ice, Blocks.ice}};
        this.waterf = 2.0F / (float)this.arr[0].length;
    }

    float rawHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(this.scl);
        return (Mathf.pow(Simplex.noise3d(0, (double)5.0F, (double)0.5F, (double)0.33333334F, (double)position.x, (double)position.y, (double)position.z), 3.0F) + Math.abs(Mathf.sin(position.x) + Mathf.cos(position.y)) / 5.0F + this.waterOffset) / (1.0F + this.waterOffset);
    }

    public float getHeight(Vec3 position) {
        float height = this.rawHeight(position);
        return Math.max(height, this.waterf);
    }

    public Color getColor(Vec3 position) {
        Block block = this.getBlock(position);
        return block == null ? Color.white.cpy() : Tmp.c1.set(block.mapColor).a(1.0F - block.albedo);
    }

    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = this.getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if ((double)Ridged.noise3d(1, (double)position.x, (double)position.y, (double)position.z, 2, 22.0F) > 0.32) {
            tile.block = Blocks.air;
        }

    }

    Block getBlock(Vec3 position) {
        float height = this.rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(this.scl);
        float rad = this.scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2.0F) / rad);
        float tnoise = Simplex.noise3d(0, (double)7.0F, 0.56, (double)0.33333334F, (double)position.x, (double)(position.y + 999.0F), (double)position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5F);
        height *= 1.2F;
        height = Mathf.clamp(height);
        return this.arr[Mathf.clamp((int)(temp * (float)this.arr.length), 0, this.arr[0].length - 1)][Mathf.clamp((int)(height * (float)this.arr[0].length), 0, this.arr[0].length - 1)];
    }

    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag) {
        Vec3 v = this.sector.rect.project(x, y).scl(5.0F);
        return Simplex.noise3d(0, octaves, falloff, (double)1.0F / scl, (double)v.x, (double)v.y, (double)v.z) * (float)mag;
    }

    protected void generate() {
        this.cells(4);
        this.distort(10.0F, 12.0F);
        float constraint = 1.3F;
        float radius = (float)this.width / 2.0F / Mathf.sqrt3;
        int rooms = this.rand.random(2, 5);

        class Room {
            int x;
            int y;
            int radius;
            ObjectSet<Room> connected = new ObjectSet();

            Room(int x, int y, int radius) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.connected.add(this);
            }

            void connect(Room to) {
                if (!this.connected.contains(to)) {
                    this.connected.add(to);
                    float nscl = ElectrodePlanetGenerator.this.rand.random(20.0F, 60.0F);
                    int stroke = ElectrodePlanetGenerator.this.rand.random(4, 12);
                    ElectrodePlanetGenerator.this.brush(ElectrodePlanetGenerator.this.pathfind(this.x, this.y, to.x, to.y, (tile) -> (tile.solid() ? 5.0F : 0.0F) + ElectrodePlanetGenerator.this.noise((float)tile.x, (float)tile.y, (double)1.0F, (double)1.0F, (double)(1.0F / nscl)) * 60.0F, Astar.manhattan), stroke);
                }
            }
        }

        Seq<Room> roomseq = new Seq();

        for(int i = 0; i < rooms; ++i) {
            Tmp.v1.trns(this.rand.random(360.0F), this.rand.random(radius / constraint));
            float rx = (float)this.width / 2.0F + Tmp.v1.x;
            float ry = (float)this.height / 2.0F + Tmp.v1.y;
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(this.rand.random(9.0F, maxrad / 2.0F), 30.0F);
            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        Room spawn = null;
        Seq<Room> enemies = new Seq();
        int enemySpawns = this.rand.chance(0.3) ? 2 : 1;
        int offset = this.rand.nextInt(360);
        float length = (float)this.width / 2.55F - (float)this.rand.random(13, 23);
        int angleStep = 5;
        int waterCheckRad = 5;

        for(int i = 0; i < 360; i += angleStep) {
            int angle = offset + i;
            int cx = (int)((float)(this.width / 2) + Angles.trnsx((float)angle, length));
            int cy = (int)((float)(this.height / 2) + Angles.trnsy((float)angle, length));
            int waterTiles = 0;

            for(int rx = -waterCheckRad; rx <= waterCheckRad; ++rx) {
                for(int ry = -waterCheckRad; ry <= waterCheckRad; ++ry) {
                    Tile tile = this.tiles.get(cx + rx, cy + ry);
                    if (tile == null || tile.floor().liquidDrop != null) {
                        ++waterTiles;
                    }
                }
            }

            if (waterTiles <= 4 || i + angleStep >= 360) {
                spawn = new Room(cx, cy, this.rand.random(8, 15));
                roomseq.add(spawn);

                for(int j = 0; j < enemySpawns; ++j) {
                    float enemyOffset = this.rand.range(60.0F);
                    Tmp.v1.set((float)cx - (float)this.width / 2.0F, (float)cy - (float)this.height / 2.0F).rotate(180.0F + enemyOffset).add((float)this.width / 2.0F, (float)this.height / 2.0F);
                    Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, this.rand.random(8, 15));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }
                break;
            }
        }

        for(Room room : roomseq) {
            this.erase(room.x, room.y, room.radius);
        }

        int connections = this.rand.random(Math.max(rooms - 1, 1), rooms + 3);

        for(int i = 0; i < connections; ++i) {
            ((Room)roomseq.random(this.rand)).connect((Room)roomseq.random(this.rand));
        }

        for(Room room : roomseq) {
            spawn.connect(room);
        }

        this.cells(1);
        this.distort(10.0F, 6.0F);
        this.inverseFloodFill(this.tiles.getn(spawn.x, spawn.y));
        Seq<Block> ores = Seq.with(new Block[]{Blocks.oreCopper, Blocks.oreLead});
        float poles = Math.abs(this.sector.tile.v.y);
        float nmag = 0.5F;
        float scl = 1.0F;
        float addscl = 1.3F;
        if (Simplex.noise3d(0, (double)2.0F, (double)0.5F, (double)scl, (double)this.sector.tile.v.x, (double)this.sector.tile.v.y, (double)this.sector.tile.v.z) * nmag + poles > 0.25F * addscl) {
            ores.add(Blocks.oreCoal);
        }

        if (Simplex.noise3d(0, (double)2.0F, (double)0.5F, (double)scl, (double)(this.sector.tile.v.x + 1.0F), (double)this.sector.tile.v.y, (double)this.sector.tile.v.z) * nmag + poles > 0.5F * addscl) {
            ores.add(Blocks.oreTitanium);
        }

        if (Simplex.noise3d(0, (double)2.0F, (double)0.5F, (double)scl, (double)(this.sector.tile.v.x + 2.0F), (double)this.sector.tile.v.y, (double)this.sector.tile.v.z) * nmag + poles > 0.7F * addscl) {
            ores.add(Blocks.oreThorium);
        }

        FloatSeq frequencies = new FloatSeq();

        for(int i = 0; i < ores.size; ++i) {
            frequencies.add(this.rand.random(-0.09F, 0.01F) - (float)i * 0.01F);
        }

        this.pass((xx, yx) -> {
            if (this.floor.asFloor().hasSurface()) {
                int offsetX = xx - 4;
                int offsetY = yx + 23;

                for(int i = ores.size - 1; i >= 0; --i) {
                    Block entry = (Block)ores.get(i);
                    float freq = frequencies.get(i);
                    if ((double)Math.abs(0.5F - this.noise((float)offsetX, (float)(offsetY + i * 999), (double)2.0F, 0.7, (double)(40 + i * 2))) > (double)0.22F + (double)i * 0.01 && Math.abs(0.5F - this.noise((float)offsetX, (float)(offsetY - i * 999), (double)1.0F, (double)1.0F, (double)(30 + i * 4))) > 0.37F + freq) {
                        this.ore = entry;
                        break;
                    }
                }

            }
        });
        this.trimDark();
        this.median(2);
        this.tech();
        float difficulty = this.sector.threat;
        this.ints.clear();
        this.ints.ensureCapacity(this.width * this.height / 4);
        int ruinCount = this.rand.random(-2, 4);
        if (ruinCount > 0) {
            int padding = 25;

            for(int x = padding; x < this.width - padding; ++x) {
                for(int y = padding; y < this.height - padding; ++y) {
                    Tile tile = this.tiles.getn(x, y);
                    if (!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)) {
                        this.ints.add(tile.pos());
                    }
                }
            }

            this.ints.shuffle(this.rand);
            int placed = 0;
            float diffRange = 0.4F;

            for(int i = 0; i < this.ints.size && placed < ruinCount; ++i) {
                int val = this.ints.items[i];
                int x = Point2.x(val);
                int y = Point2.y(val);
                if (!Mathf.within((float)x, (float)y, (float)spawn.x, (float)spawn.y, 18.0F)) {
                    float range = difficulty + this.rand.random(diffRange);
                    Tile tile = this.tiles.getn(x, y);
                    BaseRegistry.BasePart part = null;
                    if (tile.overlay().itemDrop != null) {
                        part = (BaseRegistry.BasePart)Vars.bases.forResource(tile.drop()).getFrac(range);
                    } else if (tile.floor().liquidDrop != null && this.rand.chance(0.05)) {
                        part = (BaseRegistry.BasePart)Vars.bases.forResource(tile.floor().liquidDrop).getFrac(range);
                    } else if (this.rand.chance(0.05)) {
                        part = (BaseRegistry.BasePart)Vars.bases.parts.getFrac(range);
                    }

                    if (part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, (cxx, cyx) -> {
                        Tile other = this.tiles.getn(cxx, cyx);
                        other.setOverlay(Blocks.oreScrap);

                        for(int j = 1; j <= 2; ++j) {
                            for(Point2 p : Geometry.d8) {
                                Tile t = this.tiles.get(cxx + p.x * j, cyx + p.y * j);
                                if (t != null && t.floor().hasSurface() && this.rand.chance(j == 1 ? 0.4 : 0.2)) {
                                    t.setOverlay(Blocks.oreScrap);
                                }
                            }
                        }

                    })) {
                        ++placed;
                        int debrisRadius = Math.max(part.schematic.width, part.schematic.height) / 2 + 3;
                        Geometry.circle(x, y, this.tiles.width, this.tiles.height, debrisRadius, (cxx, cyx) -> {
                            float dst = Mathf.dst((float)cxx, (float)cyx, (float)x, (float)y);
                            float removeChance = Mathf.lerp(0.05F, 0.5F, dst / (float)debrisRadius);
                            Tile other = this.tiles.getn(cxx, cyx);
                            if (other.build != null && other.isCenter()) {
                                if (other.team() == Team.derelict && this.rand.chance((double)removeChance)) {
                                    other.remove();
                                } else if (this.rand.chance((double)0.5F)) {
                                    other.build.health -= this.rand.random(other.build.health * 0.9F);
                                }
                            }

                        });
                    }
                }
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        for(Room espawn : enemies) {
            this.tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if (this.sector.hasEnemyBase()) {
            this.basegen.generate(this.tiles, enemies.map((r) -> this.tiles.getn(r.x, r.y)), this.tiles.get(spawn.x, spawn.y), Vars.state.rules.waveTeam, this.sector, difficulty);
            Vars.state.rules.attackMode = true;
        } else {
            Vars.state.rules.winWave = 15 * (int)Math.max(difficulty * 10.0F, 1.0F);
        }

        Vars.state.rules.waves = true;
        Vars.state.rules.spawns = Waves.generate(difficulty);
    }

    public void postGenerate(Tiles tiles) {
        if (this.sector.hasEnemyBase()) {
            this.basegen.postGenerate();
        }

    }
}
