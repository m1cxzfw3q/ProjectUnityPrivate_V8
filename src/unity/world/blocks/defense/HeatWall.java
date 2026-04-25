package unity.world.blocks.defense;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.gen.Building;
import mindustry.world.Block;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.Graphs;
import unity.world.modules.GraphModules;

public class HeatWall extends Block implements GraphBlockBase {
    protected float minStatusRadius = 4.0F;
    protected float statusRadiusMul = 20.0F;
    protected float minStatusDuration = 3.0F;
    protected float statusDurationMul = 40.0F;
    protected float statusTime = 60.0F;
    protected float maxDamage;
    final Graphs graphs = new Graphs();
    TextureRegion heatRegion;

    public HeatWall(String name) {
        super(name);
        this.update = this.solid = true;
    }

    public void setStats() {
        super.setStats();
        this.graphs.setStats(this.stats);
        this.setStatsExt(this.stats);
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        this.graphs.drawPlace(x, y, this.size, rotation, valid);
        super.drawPlace(x, y, rotation, valid);
    }

    public Graphs graphs() {
        return this.graphs;
    }

    public void load() {
        super.load();
        this.heatRegion = Core.atlas.find(this.name + "-heat");
    }

    public class HeatWallBuild extends Building implements GraphBlockBase.GraphBuildBase {
        GraphModules gms;

        public void created() {
            this.gms = new GraphModules(this);
            HeatWall.this.graphs.injectGraphConnector(this.gms);
            this.gms.created();
        }

        public float efficiency() {
            return super.efficiency() * this.gms.efficiency();
        }

        public void onRemoved() {
            this.gms.updateGraphRemovals();
            this.onDelete();
            super.onRemoved();
            this.onDeletePost();
        }

        public void updateTile() {
            if (HeatWall.this.graphs.useOriginalUpdate()) {
                super.updateTile();
            }

            this.updatePre();
            this.gms.updateTile();
            this.updatePost();
            this.gms.prevTileRotation(this.rotation);
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            this.gms.onProximityUpdate();
            this.proxUpdate();
        }

        public void display(Table table) {
            super.display(table);
            this.gms.display(table);
            this.displayExt(table);
        }

        public void displayBars(Table table) {
            super.displayBars(table);
            this.gms.displayBars(table);
            this.displayBarsExt(table);
        }

        public void drawSelect() {
            super.drawSelect();
            this.gms.drawSelect();
        }

        public void write(Writes write) {
            super.write(write);
            this.gms.write(write);
            this.writeExt(write);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.gms.read(read, revision);
            this.readExt(read, revision);
        }

        public GraphModules gms() {
            return this.gms;
        }

        public void updatePost() {
            if (this.timer(HeatWall.this.timerDump, HeatWall.this.statusTime)) {
                float intensity = Mathf.clamp(Mathf.map(this.heat().getTemp(), 400.0F, 1000.0F, 0.0F, 1.0F));
                Damage.status(this.team, this.x, this.y, intensity * HeatWall.this.statusRadiusMul + HeatWall.this.minStatusRadius, StatusEffects.burning, HeatWall.this.minStatusDuration + intensity * HeatWall.this.statusDurationMul, false, true);
                if (HeatWall.this.maxDamage > 0.0F) {
                    Damage.damage(this.team, this.x, this.y, intensity * 10.0F + 8.0F, intensity * HeatWall.this.maxDamage, false, true);
                }
            }

        }

        public void draw() {
            Draw.rect(HeatWall.this.region, this.x, this.y);
            UnityDrawf.drawHeat(HeatWall.this.heatRegion, this.x, this.y, 0.0F, this.heat().getTemp());
            this.drawTeamTop();
        }
    }
}
