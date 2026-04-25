package unity.world.modules;

import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.GraphTorque;
import unity.world.meta.GraphData;
import unity.world.meta.GraphType;

public class GraphModules {
    public final GraphBlockBase.GraphBuildBase build;
    private GraphHeatModule heat;
    private GraphTorqueModule<? extends GraphTorque> torque;
    private GraphCrucibleModule crucible;
    private GraphFluxModule flux;
    private boolean hasHeat;
    private boolean hasTorque;
    private boolean hasCrucible;
    private boolean hasFlux;
    int prevTileRotation = -1;

    public GraphModules(GraphBlockBase.GraphBuildBase build) {
        this.build = build;
    }

    public GraphModule getGraphConnector(GraphType type) {
        if (type == GraphType.heat) {
            return this.heat;
        } else if (type == GraphType.torque) {
            return this.torque;
        } else if (type == GraphType.crucible) {
            return this.crucible;
        } else {
            return type == GraphType.flux ? this.flux : null;
        }
    }

    public <T extends GraphModule> void setGraphConnector(T graph) {
        graph.parent = this;
        if (graph instanceof GraphHeatModule) {
            GraphHeatModule heat = (GraphHeatModule)graph;
            this.heat = heat;
            this.hasHeat = heat != null;
        }

        if (graph instanceof GraphTorqueModule) {
            GraphTorqueModule torque = (GraphTorqueModule)graph;
            this.torque = torque;
            this.hasTorque = torque != null;
        }

        if (graph instanceof GraphCrucibleModule) {
            GraphCrucibleModule crucible = (GraphCrucibleModule)graph;
            this.crucible = crucible;
            this.hasCrucible = crucible != null;
        }

        if (graph instanceof GraphFluxModule) {
            GraphFluxModule flux = (GraphFluxModule)graph;
            this.flux = flux;
            this.hasFlux = flux != null;
        }

    }

    public GraphHeatModule heat() {
        return this.heat;
    }

    public GraphTorqueModule<? extends GraphTorque> torque() {
        return this.torque;
    }

    public GraphCrucibleModule crucible() {
        return this.crucible;
    }

    public GraphFluxModule flux() {
        return this.flux;
    }

    public GraphData getConnectSidePos(int index) {
        return GraphData.getConnectSidePos(index, this.build.block().size, this.build.rotation());
    }

    public void created() {
        if (this.hasHeat) {
            this.heat.onCreate(this.build);
        }

        if (this.hasTorque) {
            this.torque.onCreate(this.build);
        }

        if (this.hasCrucible) {
            this.crucible.onCreate(this.build);
        }

        if (this.hasFlux) {
            this.flux.onCreate(this.build);
        }

        this.prevTileRotation = -1;
    }

    public float efficiency() {
        float e = 1.0F;
        if (this.hasHeat) {
            e *= this.heat.efficiency();
        }

        if (this.hasTorque) {
            e *= this.torque.efficiency();
        }

        if (this.hasCrucible) {
            e *= this.crucible.efficiency();
        }

        if (this.hasFlux) {
            e *= this.flux.efficiency();
        }

        return Math.max(0.0F, e);
    }

    public void updateGraphRemovals() {
        if (this.hasHeat) {
            this.heat.onRemoved();
        }

        if (this.hasTorque) {
            this.torque.onRemoved();
        }

        if (this.hasCrucible) {
            this.crucible.onRemoved();
        }

        if (this.hasFlux) {
            this.flux.onRemoved();
        }

    }

    public void updateTile() {
        if (!this.build.block().rotate) {
            this.build.rotation(0);
        }

        if (this.prevTileRotation != this.build.rotation()) {
            if (this.hasHeat) {
                this.heat.onRotationChanged(this.prevTileRotation, this.build.rotation());
            }

            if (this.hasTorque) {
                this.torque.onRotationChanged(this.prevTileRotation, this.build.rotation());
            }

            if (this.hasCrucible) {
                this.crucible.onRotationChanged(this.prevTileRotation, this.build.rotation());
            }

            if (this.hasFlux) {
                this.flux.onRotationChanged(this.prevTileRotation, this.build.rotation());
            }

            this.build.onRotationChanged();
        }

        if (this.hasHeat) {
            this.heat.onUpdate();
        }

        if (this.hasTorque) {
            this.torque.onUpdate();
        }

        if (this.hasCrucible) {
            this.crucible.onUpdate();
        }

        if (this.hasFlux) {
            this.flux.onUpdate();
        }

    }

    public void onProximityUpdate() {
        if (this.hasHeat) {
            this.heat.proximityUpdateCustom();
        }

        if (this.hasTorque) {
            this.torque.proximityUpdateCustom();
        }

        if (this.hasCrucible) {
            this.crucible.proximityUpdateCustom();
        }

        if (this.hasFlux) {
            this.flux.proximityUpdateCustom();
        }

    }

    public void display(Table table) {
        if (this.hasHeat) {
            this.heat.display(table);
        }

        if (this.hasTorque) {
            this.torque.display(table);
        }

        if (this.hasCrucible) {
            this.crucible.display(table);
        }

        if (this.hasFlux) {
            this.flux.display(table);
        }

    }

    public void displayBars(Table table) {
        if (this.hasHeat) {
            this.heat.displayBars(table);
        }

        if (this.hasTorque) {
            this.torque.displayBars(table);
        }

        if (this.hasCrucible) {
            this.crucible.displayBars(table);
        }

        if (this.hasFlux) {
            this.flux.displayBars(table);
        }

    }

    public void write(Writes write) {
        if (this.hasHeat) {
            this.heat.write(write);
        }

        if (this.hasTorque) {
            this.torque.write(write);
        }

        if (this.hasCrucible) {
            this.crucible.write(write);
        }

        if (this.hasFlux) {
            this.flux.write(write);
        }

    }

    public void read(Reads read, byte revision) {
        if (this.hasHeat) {
            this.heat.read(read, revision);
        }

        if (this.hasTorque) {
            this.torque.read(read, revision);
        }

        if (this.hasCrucible) {
            this.crucible.read(read, revision);
        }

        if (this.hasFlux) {
            this.flux.read(read, revision);
        }

    }

    public void prevTileRotation(int r) {
        this.prevTileRotation = r;
    }

    public void drawSelect() {
        if (this.hasHeat) {
            this.heat.drawSelect();
        }

        if (this.hasTorque) {
            this.torque.drawSelect();
        }

        if (this.hasCrucible) {
            this.crucible.drawSelect();
        }

        if (this.hasFlux) {
            this.flux.drawSelect();
        }

    }
}
