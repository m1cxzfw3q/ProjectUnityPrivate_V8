package unity.map.planets;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Tmp;
import arc.util.Log.LogLevel;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;
import unity.Unity;
import unity.content.UnityBlocks;
import unity.graphics.UnityPal;
import unity.map.UnityWaves;
import unity.mod.Faction;
import unity.world.blocks.LoreMessageBlock;

public class MegalithPlanetGenerator extends PlanetGenerator {
    protected BaseGenerator basegen = new BaseGenerator();
    protected float scl = 4.5F;
    protected float waterOffset = 0.1F;
    protected Block[][] blocks;
    protected float waterHeight;
    protected Vec3 crater;
    protected float craterRadius;
    protected float craterDepth;

    public MegalithPlanetGenerator() {
        this.blocks = new Block[][]{{Blocks.deepwater, Blocks.water, Blocks.water, Blocks.water, Blocks.darksandWater, Blocks.darksandWater, Blocks.darksand, Blocks.basalt, UnityBlocks.sharpslate, Blocks.darksand}, {Blocks.deepwater, Blocks.water, Blocks.water, Blocks.darksandWater, Blocks.darksand, UnityBlocks.sharpslate, UnityBlocks.sharpslate, Blocks.basalt, UnityBlocks.sharpslate, Blocks.dacite}, {Blocks.deepwater, Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.basalt, UnityBlocks.sharpslate, UnityBlocks.sharpslate, Blocks.darksand, Blocks.snow}, {Blocks.deepwater, Blocks.water, Blocks.darksandWater, Blocks.darksand, UnityBlocks.sharpslate, Blocks.darksand, Blocks.darksand, UnityBlocks.sharpslate, UnityBlocks.sharpslate, UnityBlocks.sharpslate}, {Blocks.water, Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.basalt, Blocks.darksandWater, Blocks.snow, UnityBlocks.sharpslate, UnityBlocks.sharpslate, Blocks.dacite}, {Blocks.water, Blocks.darksandWater, Blocks.darksand, UnityBlocks.sharpslate, Blocks.darksandWater, Blocks.water, Blocks.darksandWater, Blocks.dacite, UnityBlocks.sharpslate, Blocks.dacite}, {Blocks.darksandWater, Blocks.darksand, Blocks.darksand, Blocks.darksandWater, Blocks.water, Blocks.deepwater, Blocks.water, Blocks.darksandWater, Blocks.dacite, UnityBlocks.sharpslate}, {UnityBlocks.sharpslate, UnityBlocks.sharpslate, UnityBlocks.sharpslate, UnityBlocks.sharpslate, Blocks.darksandWater, Blocks.water, Blocks.darksandWater, Blocks.dacite, Blocks.snow, Blocks.snow}, {UnityBlocks.sharpslate, Blocks.darksand, UnityBlocks.sharpslate, Blocks.dacite, Blocks.snow, Blocks.darksandWater, Blocks.snow, UnityBlocks.sharpslate, Blocks.snow, Blocks.iceSnow}, {UnityBlocks.sharpslate, Blocks.dacite, Blocks.dacite, Blocks.dacite, UnityBlocks.sharpslate, UnityBlocks.sharpslate, Blocks.snow, Blocks.snow, Blocks.iceSnow, Blocks.ice}, {Blocks.dacite, Blocks.dacite, UnityBlocks.sharpslate, Blocks.dacite, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.iceSnow, Blocks.ice}, {Blocks.dacite, Blocks.snow, Blocks.snow, Blocks.snow, UnityBlocks.sharpslate, Blocks.snow, Blocks.snow, Blocks.iceSnow, Blocks.ice, Blocks.ice}};
        this.waterHeight = 2.0F / (float)this.blocks[0].length;
        this.crater = new Vec3(-0.023117876F, 0.36916345F, -0.9290769F);
        this.craterRadius = 0.36F;
        this.craterDepth = 1.0F;
    }

    protected boolean withinCrater(Vec3 position) {
        return this.withinCrater(position, 0.0F);
    }

    protected boolean withinCrater(Vec3 position, float epsilon) {
        return position.within(this.crater, this.craterRadius + epsilon);
    }

    protected float rawHeight(Vec3 position) {
        Tmp.v33.set(position).scl(this.scl);
        float res = (Mathf.pow(Simplex.noise3d(0, (double)6.0F, (double)0.5F, 0.3333333333333333, (double)Tmp.v33.x, (double)Tmp.v33.y, (double)Tmp.v33.z), 2.3F) + this.waterOffset) / (1.0F + this.waterOffset);
        if (this.withinCrater(position, 0.03F)) {
            float n = Simplex.noise3d(0, 8.4, 0.4, 0.27, (double)Tmp.v33.x, (double)Tmp.v33.y, (double)Tmp.v33.z) * (this.craterRadius / 4.0F);
            float depth = Interp.pow2Out.apply(1.0F - position.dst(this.crater) / this.craterRadius);
            return res - (this.craterDepth * depth + (1.0F - depth) * n);
        } else {
            return res;
        }
    }

    public float getHeight(Vec3 position) {
        float height = this.rawHeight(position);
        return this.withinCrater(position) ? height : Math.max(height, this.waterHeight);
    }

    public Color getColor(Vec3 position) {
        Block block = this.getBlock(position);
        Color base = Tmp.c1.set(block.mapColor);
        if (block == UnityBlocks.sharpslate) {
            float res = Simplex.noise3d(0, (double)6.0F, (double)0.5F, (double)0.5F, (double)position.x, (double)position.y, (double)position.z) * 0.2F;
            base.lerp(UnityPal.monolithLight, res);
        }

        return base.a(1.0F - block.albedo);
    }

    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = this.getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if (Ridged.noise3d(1, (double)position.x, (double)position.y, (double)position.z, 3, 22.0F) > 0.32F) {
            tile.block = Blocks.air;
        }

    }

    Block[] getBlockset(Vec3 position) {
        position = Tmp.v33.set(position).scl(this.scl);
        float rad = this.scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2.0F) / rad);
        float tnoise = Simplex.noise3d(0, (double)7.0F, 0.56, (double)0.33333334F, (double)position.x, (double)(position.y + 999.0F), (double)position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5F);
        return this.blocks[Mathf.clamp((int)(temp * (float)this.blocks.length), 0, this.blocks.length - 1)];
    }

    protected Block getBlock(Vec3 position) {
        Block[] set = this.getBlockset(position);
        int i = Mathf.clamp((int)(Mathf.clamp(this.rawHeight(position) * 1.2F) * (float)set.length), 0, set.length - 1);
        if (this.withinCrater(position, 0.1F)) {
            while(i < set.length && set[i].asFloor().isLiquid) {
                ++i;
            }
        }

        return set[i];
    }

    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag) {
        Vec3 v = this.sector.rect.project(x, y).scl(this.scl);
        return Simplex.noise3d(0, octaves, falloff, (double)1.0F / scl, (double)v.x, (double)v.y, (double)v.z) * (float)mag;
    }

    protected void clamp(Vec2 vec) {
        float margin = (float)this.width / 16.0F;
        vec.x = Math.max(Math.min(vec.x, (float)this.width - margin), margin);
        vec.y = Math.max(Math.min(vec.y, (float)this.width - margin), margin);
    }

    public void generateSector(Sector sector) {
        if (sector.id % 10 == 0) {
            sector.generateEnemyBase = false;
        } else {
            super.generateSector(sector);
        }
    }

    protected void generate() {
        this.cells(4);
        this.distort(10.0F, 12.0F);
        float roomPos = (float)this.width / 2.0F / Mathf.sqrt3;
        int roomMin = 4;
        int roomMax = 8;
        int roomCount = this.rand.random(roomMin, roomMax);

        class Room {
            final String name;
            final int x;
            final int y;
            final int radius;
            final ObjectSet<Room> connected = new ObjectSet();

            Room(String name, int x, int y, int radius) {
                this.name = name;
                if (x >= 0 && x < MegalithPlanetGenerator.this.width && y >= 0 && y < MegalithPlanetGenerator.this.height) {
                    this.x = x;
                    this.y = y;
                    this.radius = radius;
                    this.connected.add(this);
                } else {
                    throw new IllegalArgumentException(Strings.format("'@' out of bounds: (@, @) must be between (@, @) and exclusive (@, @)", new Object[]{name, x, y, 0, 0, MegalithPlanetGenerator.this.width, MegalithPlanetGenerator.this.height}));
                }
            }

            void connect(Room to) {
                if (!this.connected.contains(to)) {
                    this.connected.add(to);
                    to.connected.add(this);
                    float nscl = MegalithPlanetGenerator.this.rand.random(12.0F, 48.0F);
                    int stroke = MegalithPlanetGenerator.this.rand.random(4, 12);
                    MegalithPlanetGenerator.this.brush(MegalithPlanetGenerator.this.pathfind(this.x, this.y, to.x, to.y, (tile) -> (tile.solid() ? 5.0F : 0.0F) + MegalithPlanetGenerator.this.noise((float)tile.x, (float)tile.y, (double)1.0F, (double)1.0F, (double)(1.0F / nscl)) * 32.0F, Astar.manhattan), stroke);
                }
            }
        }

        Seq<Room> rooms = new Seq();
        float minRadius = 0.6F;
        float scl = minRadius + (1.0F - (float)(roomCount - roomMin) / (float)(roomMax - roomMin)) * (1.0F - minRadius);

        for(int i = 0; i < roomCount; ++i) {
            Tmp.v1.trns(this.rand.random(360.0F), this.rand.random(roomPos / 1.3F));
            this.clamp(Tmp.v2.set(Tmp.v1).add((float)this.width / 2.0F, (float)this.height / 2.0F));
            float maxrad = scl * (roomPos - Tmp.v1.len());
            float rad = Math.min(this.rand.random(9.0F, maxrad / 2.0F), 30.0F);
            rooms.add(new Room("Room-" + i, (int)Tmp.v2.x, (int)Tmp.v2.y, (int)rad));
        }

        Room[] spawn = new Room[]{null};
        Seq<Room> enemies = new Seq();
        int enemySpawns = this.rand.random(2, Math.max((int)(this.sector.threat * 4.0F), 2));
        int angleStep = 5;
        int offset = (int)((float)this.width / 2.0F / Mathf.sqrt3 - this.rand.random(26.0F, 49.0F));
        int offsetAngle = this.rand.nextInt(360);
        int waterCheck = 5;

        for(int i = 0; i < 360; i += angleStep) {
            int angle = offsetAngle + i;
            Tmp.v1.set((float)((int)((float)this.width / 2.0F + Angles.trnsx((float)angle, (float)offset))), (float)((int)((float)this.height / 2.0F + Angles.trnsy((float)angle, (float)offset))));
            int waterTiles = 0;

            for(int rx = -waterCheck; rx <= waterCheck; ++rx) {
                for(int ry = -waterCheck; ry <= waterCheck; ++ry) {
                    Tile tile = this.tiles.get((int)Tmp.v1.x + rx, (int)Tmp.v1.y + ry);
                    if (tile == null || tile.floor().liquidDrop != null) {
                        ++waterTiles;
                    }
                }
            }

            if (waterTiles <= 4 || i + angleStep >= 360) {
                rooms.add(spawn[0] = new Room("Room-Spawn", (int)Tmp.v1.x, (int)Tmp.v1.y, this.rand.random(16, 24)));

                for(int j = 0; j < enemySpawns; ++j) {
                    int enemyOffset = this.rand.range(60);
                    this.clamp(Tmp.v2.set(Tmp.v1.x - (float)this.width / 2.0F, Tmp.v1.y - (float)this.height / 2.0F).rotate(180.0F + (float)enemyOffset).add((float)this.width / 2.0F, (float)this.height / 2.0F));
                    Room espawn = new Room("Room-Espawn" + j, (int)Tmp.v2.x, (int)Tmp.v2.y, this.rand.random(12, 16));
                    rooms.add(espawn);
                    enemies.add(espawn);
                }
                break;
            }
        }

        Unity.print(LogLevel.debug, Strings.format("Generated @ rooms", new Object[]{rooms.size}), new Object[0]);

        for(Room room : rooms) {
            Unity.print(LogLevel.debug, Strings.format("Generated room @", new Object[]{room.name}), new Object[0]);
            this.erase(room.x, room.y, room.radius);
        }

        int connections = this.rand.random(Math.max(roomCount - 1, 1), roomCount + 3);

        for(int i = 0; i < connections; ++i) {
            ((Room)rooms.random(this.rand)).connect((Room)rooms.random(this.rand));
        }

        for(Room r : rooms) {
            spawn[0].connect(r);
        }

        this.cells(1);
        this.distort(10.0F, 6.0F);
        this.median(2);
        float difficulty = this.sector.threat;
        this.pass((x, y) -> {
            if (this.floor == Blocks.sand) {
                this.floor = Blocks.darksand;
            }

            if (this.block == Blocks.sandWall) {
                this.block = Blocks.duneWall;
            }

        });
        this.pass((x, y) -> {
            if (this.floor == UnityBlocks.sharpslate) {
                float sel = this.noise((float)x, (float)y, (double)4.0F, (double)17.0F, (double)460.0F, 0.84);
                if (sel < 0.5F || !this.rand.chance((double)sel)) {
                    return;
                }

                float noise = this.noise((float)x, (float)y, (double)6.0F, (double)30.0F, (double)360.0F, 0.63);
                if (noise > 0.4F) {
                    this.floor = UnityBlocks.archSharpslate;
                    if (this.block == UnityBlocks.sharpslate.asFloor().wall) {
                        this.block = UnityBlocks.archSharpslate.asFloor().wall;
                    }
                } else if (noise > 0.3F) {
                    this.floor = UnityBlocks.infusedSharpslate;
                    if (this.block == UnityBlocks.sharpslate.asFloor().wall) {
                        this.block = UnityBlocks.infusedSharpslate.asFloor().wall;
                    }
                }
            }

        });
        Block target = UnityBlocks.archSharpslate;
        Block over = UnityBlocks.archEnergy;
        this.pass((x, y) -> {
            float start = 0.03F;
            float inc = 0.01F;
            float chance = start + inc * difficulty;
            if (this.rand.chance((double)chance)) {
                boolean found = false;
                if (this.floor == target) {
                    this.ore = over;
                    found = true;
                }

                if (found) {
                    for(Point2 p : Geometry.d4) {
                        Tile tile = this.tiles.get(x + p.x, y + p.y);
                        if (tile != null && tile.floor() == target && this.rand.chance((double)(1.0F / (float)Geometry.d4.length))) {
                            tile.setOverlay(over);
                        }
                    }
                }
            }

        });
        Seq<Block> ores = Seq.with(new Block[]{Blocks.oreCopper, Blocks.oreLead, UnityBlocks.oreMonolite});
        float poles = Math.abs(this.sector.tile.v.y);
        float nmag = 0.5F;
        float addscl = 1.3F;
        if ((double)(Simplex.noise3d(0, (double)2.0F, (double)0.5F, (double)1.0F, (double)this.sector.tile.v.x, (double)this.sector.tile.v.y, (double)this.sector.tile.v.z) * nmag + poles) > (double)0.25F * (double)addscl) {
            ores.add(Blocks.oreCoal);
        }

        if ((double)(Simplex.noise3d(0, (double)2.0F, (double)0.5F, (double)1.0F, (double)(this.sector.tile.v.x + 1.0F), (double)this.sector.tile.v.y, (double)this.sector.tile.v.z) * nmag + poles) > (double)0.5F * (double)addscl) {
            ores.add(Blocks.oreTitanium);
        }

        if ((double)(Simplex.noise3d(0, (double)2.0F, (double)0.5F, (double)1.0F, (double)(this.sector.tile.v.x + 2.0F), (double)this.sector.tile.v.y, (double)this.sector.tile.v.z) * nmag + poles) > 0.7 * (double)addscl) {
            ores.add(Blocks.oreThorium);
        }

        if (this.rand.chance((double)0.3F)) {
            ores.add(Blocks.oreScrap);
        }

        FloatSeq frequencies = new FloatSeq();

        for(int i = 0; i < ores.size; ++i) {
            frequencies.add(this.rand.random(-0.09F, 0.01F) - (float)i * 0.01F);
        }

        this.pass((x, y) -> {
            if (!this.floor.asFloor().isLiquid) {
                float offsetX = (float)x - 4.0F;
                float offsetY = (float)y + 23.0F;

                for(int i = ores.size - 1; i >= 0; --i) {
                    Block entry = (Block)ores.get(i);
                    float freq = frequencies.get(i);
                    if ((double)Math.abs(0.5F - this.noise(offsetX, offsetY + (float)i * 999.0F, (double)2.0F, 0.7, (double)40.0F + (double)i * (double)2.0F)) > 0.22 + (double)i * 0.01 && (double)Math.abs(0.5F - this.noise(offsetX, offsetY - (float)i * 999.0F, (double)1.0F, (double)1.0F, (double)30.0F + (double)i * (double)4.0F)) > 0.37 + (double)freq) {
                        this.ore = entry;
                        break;
                    }
                }

            }
        });
        this.trimDark();
        this.inverseFloodFill(this.tiles.getn(spawn[0].x, spawn[0].y));
        if (!this.sector.hasEnemyBase()) {
            Seq<Room> msgRoom = rooms.select((rxx) -> rxx != spawn[0] && !enemies.contains((e) -> e.name.equals(rxx.name) || Mathf.within((float)e.x, (float)e.y, (float)rxx.x, (float)rxx.y, Vars.state.rules.dropZoneRadius * 1.2F / 8.0F)));
            boolean hasMessage = false;

            for(int r = 0; r < msgRoom.size && !hasMessage; ++r) {
                Room room = (Room)msgRoom.get(r);
                if (this.rand.chance((double)0.3F) || r == msgRoom.size - 1) {
                    int angleStep2 = 10;
                    int off = this.rand.random(360);

                    for(int i = 0; i < 360; i += angleStep2) {
                        int angle = off + i;
                        Tmp.v1.trns((float)angle, (float)room.radius - this.rand.random((float)room.radius / 2.0F)).add((float)room.x, (float)room.y);
                        Tile tile = this.tiles.getn((int)Tmp.v1.x, (int)Tmp.v1.y);
                        if (this.rand.chance((double)0.1F) || i + angleStep2 >= 360) {
                            if (tile == null) {
                                tile = new Tile((int)Tmp.v1.x, (int)Tmp.v1.y);
                                this.tiles.set((int)Tmp.v1.x, (int)Tmp.v1.y, tile);
                            }

                            Block[] floors = new Block[]{Blocks.metalFloor, Blocks.metalFloor2, Blocks.metalFloor3, Blocks.metalFloor5};

                            for(int tx = -4; tx < 4; ++tx) {
                                for(int ty = -4; ty < 4; ++ty) {
                                    if (Mathf.within((float)tile.x, (float)tile.y, (float)(tile.x + tx), (float)(tile.y + ty), 4.0F) && this.rand.chance((double)((4.0F - Mathf.dst((float)tile.x, (float)tile.y, (float)(tile.x + tx), (float)(tile.y + ty))) / 4.0F))) {
                                        Tile other = this.tiles.getn(tile.x + tx, tile.y + ty);
                                        if (other == null) {
                                            other = new Tile(tile.x + tx, tile.y + ty);
                                            this.tiles.set(other.x, other.y, other);
                                        }

                                        Block block = floors[this.rand.random(floors.length - 1)];
                                        other.setFloor(block.asFloor());
                                        if (other.solid() && !other.synthetic()) {
                                            other.setBlock(block);
                                        }
                                    }
                                }
                            }

                            tile.setBlock(UnityBlocks.loreMonolith, Team.sharded, 0, () -> {
                                LoreMessageBlock.LoreMessageBuild build = (LoreMessageBlock.LoreMessageBuild)UnityBlocks.loreMonolith.newBuilding().as();
                                build.setMessage("lore.unity.megalith-" + this.sector.id);
                                return build;
                            });
                            Unity.print(LogLevel.debug, Strings.format("Generated a message block at (@, @).", new Object[]{tile.x, tile.y}), new Object[0]);
                            hasMessage = true;
                            break;
                        }
                    }
                }
            }
        }

        Schematics.placeLaunchLoadout(spawn[0].x, spawn[0].y);
        enemies.each((espawnx) -> this.tiles.getn(espawnx.x, espawnx.y).setOverlay(Blocks.spawn));
        if (this.sector.hasEnemyBase()) {
            this.basegen.generate(this.tiles, enemies.map((rxx) -> this.tiles.getn(rxx.x, rxx.y)), this.tiles.getn(spawn[0].x, spawn[0].y), Vars.state.rules.waveTeam, this.sector, difficulty);
            Vars.state.rules.attackMode = this.sector.info.attack = true;
        } else {
            Vars.state.rules.winWave = this.sector.info.winWave = 15 * (int)Math.max(difficulty * 5.0F, 1.0F);
        }

        float waveTimeDec = 0.3F;
        Vars.state.rules.waveSpacing = Mathf.lerp(6000.0F, 2400.0F, Math.max(difficulty - waveTimeDec, 0.0F) / 0.8F);
        Vars.state.rules.waves = this.sector.info.waves = true;
        Vars.state.rules.enemyCoreBuildRadius = 600.0F;
        Vars.state.rules.lighting = true;
        Vars.state.rules.ambientLight = UnityPal.monolithAtmosphere;
        Vars.state.rules.spawns = UnityWaves.generate(Faction.monolith, difficulty, new Rand(), Vars.state.rules.attackMode);
    }

    public void postGenerate(Tiles tiles) {
        if (this.sector.hasEnemyBase()) {
            this.basegen.postGenerate();
        }

    }
}
