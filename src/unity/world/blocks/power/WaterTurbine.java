package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.ArmoredConduit;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.Graphs;
import unity.world.meta.GraphData;
import unity.world.modules.GraphModules;

public class WaterTurbine extends ArmoredConduit implements GraphBlockBase {
    protected final Graphs graphs = new Graphs();
    public final TextureRegion[] topRegions = new TextureRegion[4];
    public final TextureRegion[] bottomRegions = new TextureRegion[2];
    public final TextureRegion[] liquidRegions = new TextureRegion[2];
    public TextureRegion rotorRegion;

    public WaterTurbine(String name) {
        super(name);
        this.solid = true;
        this.noUpdateDisabled = false;
    }

    public void load() {
        super.load();
        this.rotorRegion = Core.atlas.find(this.name + "-rotor");

        for(int i = 0; i < 4; ++i) {
            this.topRegions[i] = Core.atlas.find(this.name + "-top" + (i + 1));
        }

        for(int i = 0; i < 2; ++i) {
            this.bottomRegions[i] = Core.atlas.find(this.name + "-bottom" + (i + 1));
            this.liquidRegions[i] = Core.atlas.find(this.name + "-liquid" + (i + 1));
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

    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list) {
        Draw.alpha(0.5F);
        Draw.rect(this.region, req.drawx(), req.drawy(), (float)req.rotation * 90.0F);
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{this.region};
    }

    public class WaterTurbineBuild extends ArmoredConduit.ArmoredConduitBuild implements GraphBlockBase.GraphBuildBase {
        protected GraphModules gms;
        float flowRate;

        public WaterTurbineBuild() {
            super(WaterTurbine.this);
        }

        public void created() {
            this.gms = new GraphModules(this);
            WaterTurbine.this.graphs.injectGraphConnector(this.gms);
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
            if (WaterTurbine.this.graphs.useOriginalUpdate()) {
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

        public void updatePre() {
            float flow = this.flowRate * 40.0F;
            this.smoothLiquid = Mathf.lerpDelta(this.smoothLiquid, this.liquids.currentAmount() / WaterTurbine.this.liquidCapacity, 0.05F);
            if (this.liquids.total() > 0.001F && this.timer(WaterTurbine.this.timerFlow, 1.0F)) {
                this.flowRate = this.moveLiquidForward(WaterTurbine.this.leaks, this.liquids.current());
            }

            float mul = flow / 100.0F;
            if (mul < 0.4F) {
                mul = 0.0F;
            }

            if (mul > 1.0F) {
                mul = 0.5F * Mathf.log2(mul) + 1.0F;
            }

            this.torque().setMotorForceMult(mul);
        }

        public void draw() {
            float rot = this.torque().getRotation();
            Draw.rect(WaterTurbine.this.bottomRegions[this.rotation % 2], this.x, this.y);
            if (this.liquids.total() > 0.001F) {
                Drawf.liquid(WaterTurbine.this.liquidRegions[this.rotation % 2], this.x, this.y, this.liquids.total() / WaterTurbine.this.liquidCapacity, this.liquids.current().color);
            }

            Drawf.shadow(WaterTurbine.this.rotorRegion, this.x - (float)WaterTurbine.this.size / 2.0F, this.y - (float)WaterTurbine.this.size / 2.0F, rot);
            Draw.rect(WaterTurbine.this.rotorRegion, this.x, this.y, rot);
            Draw.rect(WaterTurbine.this.topRegions[this.rotation], this.x, this.y);
            this.drawTeamTop();
        }

        public float moveLiquidForward(boolean leaks, Liquid liquid) {
            Point2 rPos = GraphData.getConnectSidePos(1, 3, this.rotation).toPos;
            Tile next = this.tile.nearby(rPos);
            if (next == null) {
                return 0.0F;
            } else {
                return next.build != null ? this.moveLiquid(next.build, liquid) : 0.0F;
            }
        }
    }
}
