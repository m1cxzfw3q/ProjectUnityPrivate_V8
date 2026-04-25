package unity.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.blocks.production.GenericCrafter;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.Graphs;
import unity.world.modules.GraphModules;

public class SporePyrolyser extends GenericCrafter implements GraphBlockBase {
    final Graphs graphs = new Graphs();
    TextureRegion heatRegion;

    public SporePyrolyser(String name) {
        super(name);
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

    public class SporPyrolyserBuild extends GenericCrafter.GenericCrafterBuild implements GraphBlockBase.GraphBuildBase {
        GraphModules gms;

        public SporPyrolyserBuild() {
            super(SporePyrolyser.this);
        }

        public void created() {
            this.gms = new GraphModules(this);
            SporePyrolyser.this.graphs.injectGraphConnector(this.gms);
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
            if (SporePyrolyser.this.graphs.useOriginalUpdate()) {
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

        public float getProgressIncrease(float baseTime) {
            return Mathf.sqrt(Mathf.clamp((this.heat().getTemp() - 370.0F) / 300.0F)) / baseTime * this.edelta();
        }

        public void draw() {
            Draw.rect(SporePyrolyser.this.region, this.x, this.y);
            UnityDrawf.drawHeat(SporePyrolyser.this.heatRegion, this.x, this.y, 0.0F, this.heat().getTemp() * 1.5F);
            this.drawTeamTop();
        }
    }
}
