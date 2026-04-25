package unity.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import unity.graphics.UnityDrawf;
import unity.util.GraphicUtils;

public class SporeFarm extends Block {
    static final int frames = 5;
    int gTimer;
    public final TextureRegion[] sporeRegions = new TextureRegion[5];
    public final TextureRegion[] groundRegions = new TextureRegion[5];
    public TextureRegion[] fenceRegions;
    public TextureRegion cageFloor;

    public SporeFarm(String name) {
        super(name);
        this.update = true;
        this.gTimer = this.timers++;
    }

    public void load() {
        super.load();

        for(int i = 0; i < 5; ++i) {
            this.sporeRegions[i] = Core.atlas.find(this.name + "-spore" + (i + 1));
            this.groundRegions[i] = Core.atlas.find(this.name + "-ground" + (i + 1));
        }

        this.fenceRegions = GraphicUtils.getRegions(Core.atlas.find(this.name + "-fence"), 12, 4);
        this.cageFloor = Core.atlas.find(this.name + "-floor");
    }

    public class SporeFarmBuild extends Building {
        float growth;
        float delay = -1.0F;
        int tileIndex = -1;
        boolean needsTileUpdate;

        boolean randomChk() {
            Tile cTile = Vars.world.tile(this.tileX() + Mathf.range(3), this.tileY() + Mathf.range(3));
            return cTile != null && cTile.floor().liquidDrop == Liquids.water;
        }

        void updateTilings() {
            this.tileIndex = 0;

            for(int i = 0; i < 8; ++i) {
                Tile other = this.tile.nearby(Geometry.d8(i));
                if (other != null && other.build instanceof SporeFarmBuild) {
                    this.tileIndex += 1 << i;
                }
            }

        }

        void updateNeighbours() {
            for(int i = 0; i < 8; ++i) {
                Tile other = this.tile.nearby(Geometry.d8(i));
                if (other != null) {
                    Building var4 = other.build;
                    if (var4 instanceof SporeFarmBuild) {
                        SporeFarmBuild b = (SporeFarmBuild)var4;
                        b.needsTileUpdate = true;
                    }
                }
            }

        }

        public void onProximityRemoved() {
            super.onProximityRemoved();
            this.updateNeighbours();
        }

        public void updateTile() {
            if (this.tileIndex == -1) {
                this.updateTilings();
                this.updateNeighbours();
            }

            if (this.needsTileUpdate) {
                this.updateTilings();
                this.needsTileUpdate = false;
            }

            if (this.timer(SporeFarm.this.gTimer, (60.0F + this.delay) * 5.0F)) {
                if (this.delay == -1.0F) {
                    this.delay = ((float)this.tileX() * 89.0F + (float)this.tileY() * 13.0F) % 21.0F;
                } else {
                    boolean chk = this.randomChk();
                    if (this.growth == 0.0F && !chk) {
                        return;
                    }

                    this.growth += chk ? (this.growth > 3.0F ? 0.1F : 0.45F) : -0.1F;
                    if (this.growth >= 5.0F) {
                        this.growth = 4.0F;
                        if (this.items.total() < 1) {
                            this.offload(Items.sporePod);
                        }
                    }

                    if (this.growth < 0.0F) {
                        this.growth = 0.0F;
                    }
                }
            }

            if (this.timer(SporeFarm.this.timerDump, 15.0F)) {
                this.dump(Items.sporePod);
            }

        }

        public void draw() {
            float rrot = ((float)this.tileX() * 89.0F + (float)this.tileY() * 13.0F) % 4.0F;
            float rrot2 = ((float)this.tileX() * 69.0F + (float)this.tileY() * 42.0F) % 4.0F;
            if (this.growth < 4.5F) {
                Tile t = Vars.world.tileWorld(this.x, this.y);
                if (t != null && t.floor() != Blocks.air) {
                    Floor f = t.floor();
                    Mathf.rand.setSeed((long)t.pos());
                    Draw.rect(f.variantRegions()[Mathf.randomSeed((long)t.pos(), 0, Math.max(0, SporeFarm.this.variantRegions().length - 1))], this.x, this.y);
                }

                Draw.rect(SporeFarm.this.cageFloor, this.x, this.y);
            }

            if (this.growth != 0.0F) {
                Draw.rect(SporeFarm.this.groundRegions[Mathf.floor(this.growth)], this.x, this.y, rrot * 90.0F);
                Draw.rect(SporeFarm.this.sporeRegions[Mathf.floor(this.growth)], this.x, this.y, rrot2 * 90.0F);
            }

            Draw.rect(SporeFarm.this.fenceRegions[UnityDrawf.tileMap[this.tileIndex]], this.x, this.y, 8.0F, 8.0F);
            this.drawTeamTop();
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.growth);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.growth = read.f();
        }
    }
}
