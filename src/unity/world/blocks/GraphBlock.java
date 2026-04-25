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
    protected TextureRegion heatRegion, liquidRegion;

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
            gms = new GraphModules(this);
            graphs.injectGraphConnector(gms);
            gms.created();
        }

        public float efficiency() {
            return efficiency * gms.efficiency();
        }

        public void onRemoved() {
            gms.updateGraphRemovals();
            onDelete();
            super.onRemoved();
            onDeletePost();
        }

        public void updateTile() {
            if (graphs.useOriginalUpdate()) {
                updateTile();
            }

            updatePre();
            gms.updateTile();
            updatePost();
            gms.prevTileRotation(rotation);
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            gms.onProximityUpdate();
            proxUpdate();
        }

        public void display(Table table) {
            super.display(table);
            gms.display(table);
            displayExt(table);
        }

        public void displayBars(Table table) {
            super.displayBars(table);
            gms.displayBars(table);
            displayBarsExt(table);
        }

        public void write(Writes write) {
            super.write(write);
            gms.write(write);
            writeExt(write);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            gms.read(read, revision);
            readExt(read, revision);
        }

        public GraphModules gms() {
            return gms;
        }

        public void drawSelect() {
            super.drawSelect();
            gms.drawSelect();
        }

        public void draw() {
            if (preserveDraw) {
                super.draw();
            } else if (graphs.hasGraph(GraphType.heat)) {
                Draw.rect(region, x, y);
                UnityDrawf.drawHeat(heatRegion, x, y, 0f, heat().getTemp());
                drawTeamTop();
            }
        }
    }
}
