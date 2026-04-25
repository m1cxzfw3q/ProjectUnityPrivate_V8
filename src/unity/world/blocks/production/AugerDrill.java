package unity.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.blocks.production.Drill;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.Graphs;
import unity.world.modules.GraphModules;

public class AugerDrill extends Drill implements GraphBlockBase {
    protected final Graphs graphs = new Graphs();
    public final TextureRegion[] bottomRegions = new TextureRegion[2];
    public final TextureRegion[] topRegions = new TextureRegion[2];
    public final TextureRegion[] oreRegions = new TextureRegion[2];
    public TextureRegion rotorRegion;
    public TextureRegion rotorRotateRegion;
    public TextureRegion mbaseRegion;
    public TextureRegion wormDrive;
    public TextureRegion gearRegion;
    public TextureRegion rotateRegion;
    public TextureRegion overlayRegion;

    public AugerDrill(String name) {
        super(name);
        this.rotate = true;
        this.hasLiquids = false;
        this.liquidBoostIntensity = 1.0F;
    }

    public void load() {
        super.load();
        this.rotorRegion = Core.atlas.find(this.name + "-rotor");
        this.rotorRotateRegion = Core.atlas.find(this.name + "-rotor-rotate");
        this.mbaseRegion = Core.atlas.find(this.name + "-mbase");
        this.gearRegion = Core.atlas.find(this.name + "-gear");
        this.overlayRegion = Core.atlas.find(this.name + "-overlay");
        this.rotateRegion = Core.atlas.find(this.name + "-moving");
        this.wormDrive = Core.atlas.find(this.name + "-rotate");

        for(int i = 0; i < 2; ++i) {
            this.bottomRegions[i] = Core.atlas.find(this.name + "-bottom" + (i + 1));
            this.topRegions[i] = Core.atlas.find(this.name + "-top" + (i + 1));
            this.oreRegions[i] = Core.atlas.find(this.name + "-ore" + (i + 1));
        }

    }

    public void setStats() {
        super.setStats();
        this.graphs.setStats(this.stats);
        this.setStatsExt(this.stats);
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(this.name)};
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        this.graphs.drawPlace(x, y, this.size, rotation, valid);
        super.drawPlace(x, y, rotation, valid);
    }

    public Graphs graphs() {
        return this.graphs;
    }

    public class ArguerDrillBuild extends Drill.DrillBuild implements GraphBlockBase.GraphBuildBase {
        protected GraphModules gms;

        public ArguerDrillBuild() {
            super(AugerDrill.this);
        }

        public void created() {
            this.gms = new GraphModules(this);
            AugerDrill.this.graphs.injectGraphConnector(this.gms);
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
            if (AugerDrill.this.graphs.useOriginalUpdate()) {
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
            this.warmup = Math.min(1.0F, this.warmup);
        }

        public void draw() {
            float rot = this.torque().getRotation();
            float fixedRot = (this.rotdeg() + 90.0F) % 180.0F - 90.0F;
            int variant = this.rotation % 2;
            float deg = this.rotation != 0 && this.rotation != 3 ? -rot : rot;
            float shaftRot = rot * 2.0F;
            Point2 offset = Geometry.d4(this.rotation + 1);
            Draw.rect(AugerDrill.this.bottomRegions[variant], this.x, this.y);
            if (this.dominantItem != null && AugerDrill.this.drawMineItem) {
                Draw.color(this.dominantItem.color);
                Draw.rect(AugerDrill.this.oreRegions[variant], this.x, this.y);
                Draw.color();
            }

            Draw.rect(AugerDrill.this.rotorRegion, this.x, this.y, 360.0F - rot * 0.25F);
            UnityDrawf.drawRotRect(AugerDrill.this.rotorRotateRegion, this.x, this.y, 24.0F, 3.5F, 3.5F, 450.0F - rot * 0.25F, shaftRot, shaftRot + 180.0F);
            UnityDrawf.drawRotRect(AugerDrill.this.rotorRotateRegion, this.x, this.y, 24.0F, 3.5F, 3.5F, 450.0F - rot * 0.25F, shaftRot + 180.0F, shaftRot + 360.0F);
            Draw.rect(AugerDrill.this.mbaseRegion, this.x, this.y, fixedRot);
            UnityDrawf.drawRotRect(AugerDrill.this.wormDrive, this.x, this.y, 24.0F, 3.5F, 3.5F, fixedRot, rot, rot + 180.0F);
            UnityDrawf.drawRotRect(AugerDrill.this.wormDrive, this.x, this.y, 24.0F, 3.5F, 3.5F, fixedRot, rot + 180.0F, rot + 360.0F);
            UnityDrawf.drawRotRect(AugerDrill.this.rotateRegion, this.x, this.y, 24.0F, 3.5F, 3.5F, fixedRot, rot, rot + 180.0F);
            Draw.rect(AugerDrill.this.overlayRegion, this.x, this.y, fixedRot);
            Draw.rect(AugerDrill.this.gearRegion, this.x + (float)offset.x * 4.0F, this.y + (float)offset.y * 4.0F, -deg / 2.0F);
            Draw.rect(AugerDrill.this.gearRegion, this.x - (float)offset.x * 4.0F, this.y - (float)offset.y * 4.0F, deg / 2.0F);
            Draw.rect(AugerDrill.this.topRegions[variant], this.x, this.y);
            this.drawTeamTop();
        }
    }
}
