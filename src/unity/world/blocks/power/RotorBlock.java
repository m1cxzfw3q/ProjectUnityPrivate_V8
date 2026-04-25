package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.PowerGenerator;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlockBase;
import unity.world.graph.FluxGraph;
import unity.world.graph.TorqueGraph;
import unity.world.graphs.Graphs;
import unity.world.modules.GraphModules;
import unity.world.modules.GraphTorqueModule;

public class RotorBlock extends PowerGenerator implements GraphBlockBase {
    protected final Graphs graphs = new Graphs();
    protected float baseTopSpeed = 20.0F;
    protected float baseTorque = 5.0F;
    protected float torqueEfficiency = 1.0F;
    protected float fluxEfficiency = 1.0F;
    protected float rotPowerEfficiency = 1.0F;
    protected boolean big;
    public final TextureRegion[] topRegions = new TextureRegion[4];
    public TextureRegion overlayRegion;
    public TextureRegion rotorRegion;
    public TextureRegion bottomRegion;
    public TextureRegion topRegion;
    public TextureRegion overRegion;
    public TextureRegion spinRegion;

    public RotorBlock(String name) {
        super(name);
        this.rotate = this.consumesPower = this.outputsPower = true;
    }

    public void load() {
        super.load();
        if (this.big) {
            for(int i = 0; i < 4; ++i) {
                this.topRegions[i] = Core.atlas.find(this.name + "-top" + (i + 1));
            }

            this.overlayRegion = Core.atlas.find(this.name + "-overlay");
            this.rotorRegion = Core.atlas.find(this.name + "-rotor");
            this.bottomRegion = Core.atlas.find(this.name + "-bottom");
        } else {
            this.topRegion = Core.atlas.find(this.name + "-top");
            this.overRegion = Core.atlas.find(this.name + "-over");
            this.spinRegion = Core.atlas.find(this.name + "-spin");
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

    public class RotorBuild extends PowerGenerator.GeneratorBuild implements GraphBlockBase.GraphBuildBase {
        protected GraphModules gms;
        float topSpeed;

        public RotorBuild() {
            super(RotorBlock.this);
        }

        public void created() {
            this.gms = new GraphModules(this);
            RotorBlock.this.graphs.injectGraphConnector(this.gms);
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
            if (RotorBlock.this.graphs.useOriginalUpdate()) {
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

        public void displayBarsExt(Table table) {
            GraphTorqueModule<?> tGraph = this.torque();
            float mTorque = ((FluxGraph)this.flux().getNetwork()).flux() * RotorBlock.this.torqueEfficiency * RotorBlock.this.baseTorque;
            table.add(new Bar(() -> Core.bundle.format("bar.poweroutput", new Object[]{Strings.fixed((this.getPowerProduction() - RotorBlock.this.consumes.getPower().usage) * 60.0F * this.timeScale, 1)}), () -> Pal.powerBar, () -> this.productionEfficiency)).growX().row();
            table.add(new Bar(() -> Core.bundle.get("stat.unity.torque") + ": " + Strings.fixed(tGraph.force, 1) + "/" + Strings.fixed(mTorque, 1), () -> Pal.darkishGray, () -> tGraph.force / mTorque)).growX().row();
            table.add(new Bar(() -> Core.bundle.format("stat.unity.maxspeed", new Object[0]) + ":" + Strings.fixed(this.topSpeed / 6.0F, 1) + "r/s", () -> Pal.darkishGray, () -> this.topSpeed / RotorBlock.this.baseTopSpeed)).growX().row();
        }

        public void updatePre() {
            float flux = ((FluxGraph)this.flux().getNetwork()).flux();
            this.topSpeed = RotorBlock.this.baseTopSpeed / (1.0F + flux / RotorBlock.this.fluxEfficiency);
            float breakEven = RotorBlock.this.consumes.getPower().usage / RotorBlock.this.powerProduction;
            GraphTorqueModule<?> tGraph = this.torque();
            float rotNeg = Mathf.clamp(((TorqueGraph)tGraph.getNetwork()).lastVelocity / this.topSpeed, 0.0F, 2.0F / breakEven);
            this.productionEfficiency = Mathf.clamp(rotNeg * breakEven, 0.0F, 2.0F);
            this.productionEfficiency *= RotorBlock.this.rotPowerEfficiency;
            tGraph.force = flux * RotorBlock.this.baseTorque * (this.efficiency() - rotNeg) * this.delta();
        }

        public void draw() {
            float fixedRot = (this.rotdeg() + 90.0F) % 180.0F - 90.0F;
            float shaftRot = (this.rotation + 1) % 4 >= 2 ? 360.0F - this.torque().getRotation() : this.torque().getRotation();
            if (RotorBlock.this.big) {
                Draw.rect(RotorBlock.this.bottomRegion, this.x, this.y, fixedRot);
                UnityDrawf.drawRotRect(RotorBlock.this.rotorRegion, this.x, this.y, 24.0F, 15.0F, 24.0F, this.rotdeg(), shaftRot, shaftRot + 90.0F);
                UnityDrawf.drawRotRect(RotorBlock.this.rotorRegion, this.x, this.y, 24.0F, 15.0F, 24.0F, this.rotdeg(), shaftRot + 120.0F, shaftRot + 210.0F);
                UnityDrawf.drawRotRect(RotorBlock.this.rotorRegion, this.x, this.y, 24.0F, 15.0F, 24.0F, this.rotdeg(), shaftRot + 240.0F, shaftRot + 330.0F);
                Draw.rect(RotorBlock.this.overlayRegion, this.x, this.y, fixedRot);
                Draw.rect(RotorBlock.this.topRegions[this.rotation], this.x, this.y);
            } else {
                UnityDrawf.drawRotRect(RotorBlock.this.spinRegion, this.x, this.y, 8.0F, 3.5F, 8.0F, this.rotdeg(), shaftRot, shaftRot + 180.0F);
                UnityDrawf.drawRotRect(RotorBlock.this.spinRegion, this.x, this.y, 8.0F, 3.5F, 8.0F, this.rotdeg(), shaftRot + 180.0F, shaftRot + 360.0F);
                Draw.rect(RotorBlock.this.overRegion, this.x, this.y, fixedRot);
                Draw.rect(RotorBlock.this.topRegion, this.x, this.y, fixedRot);
            }

            this.drawTeamTop();
        }
    }
}
