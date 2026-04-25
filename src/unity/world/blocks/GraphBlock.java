package unity.world.blocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.Block;
import unity.graphics.UnityDrawf;
import unity.world.graphs.Graphs;
import unity.world.meta.GraphType;
import unity.world.modules.GraphModules;

public class GraphBlock extends Block implements GraphBlockBase {
    protected final Graphs graphs = new Graphs();
    protected boolean preserveDraw;
    protected TextureRegion heatRegion;
    protected TextureRegion liquidRegion;

    public GraphBlock(String name) {
        super(name);
        this.update = true;
    }

    public void load() {
        super.load();
        if (this.graphs.hasGraph(GraphType.crucible)) {
            this.liquidRegion = Core.atlas.find(this.name + "-liquid");
        }

        if (this.graphs.hasGraph(GraphType.heat)) {
            this.heatRegion = Core.atlas.find(this.name + "-heat");
        }

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

    public class GraphBuild extends Building implements GraphBlockBase.GraphBuildBase {
        protected GraphModules gms;

        public void created() {
            this.gms = new GraphModules(this);
            GraphBlock.this.graphs.injectGraphConnector(this.gms);
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
            if (GraphBlock.this.graphs.useOriginalUpdate()) {
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

        public void drawSelect() {
            super.drawSelect();
            this.gms.drawSelect();
        }

        public void draw() {
            if (GraphBlock.this.preserveDraw) {
                super.draw();
            } else if (GraphBlock.this.graphs.hasGraph(GraphType.heat)) {
                Draw.rect(GraphBlock.this.region, this.x, this.y);
                UnityDrawf.drawHeat(GraphBlock.this.heatRegion, this.x, this.y, 0.0F, this.heat().getTemp());
                this.drawTeamTop();
            }

        }
    }
}
