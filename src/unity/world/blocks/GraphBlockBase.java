package unity.world.blocks;

import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Buildingc;
import mindustry.world.meta.Stats;
import unity.world.graphs.Graph;
import unity.world.graphs.GraphTorque;
import unity.world.graphs.Graphs;
import unity.world.meta.GraphType;
import unity.world.modules.GraphCrucibleModule;
import unity.world.modules.GraphFluxModule;
import unity.world.modules.GraphHeatModule;
import unity.world.modules.GraphModule;
import unity.world.modules.GraphModules;
import unity.world.modules.GraphTorqueModule;

public interface GraphBlockBase {
    Graphs graphs();

    default void disableOgUpdate() {
        this.graphs().disableOgUpdate();
    }

    default void addGraph(Graph graph) {
        this.graphs().setGraphConnectorTypes(graph);
    }

    default void setStatsExt(Stats stats) {
    }

    public interface GraphBuildBase extends Buildingc {
        GraphModules gms();

        default GraphModule getGraphConnector(GraphType type) {
            return this.gms().getGraphConnector(type);
        }

        default GraphHeatModule heat() {
            return this.gms().heat();
        }

        default GraphTorqueModule<? extends GraphTorque> torque() {
            return this.gms().torque();
        }

        default GraphCrucibleModule crucible() {
            return this.gms().crucible();
        }

        default GraphFluxModule flux() {
            return this.gms().flux();
        }

        default void onGraphUpdate() {
        }

        default void onNeighboursChanged() {
        }

        default void onDelete() {
        }

        default void onDeletePost() {
        }

        default void updatePre() {
        }

        default void onRotationChanged() {
        }

        default void updatePost() {
        }

        default void proxUpdate() {
        }

        default void displayExt(Table table) {
        }

        default void displayBarsExt(Table table) {
        }

        default void writeExt(Writes write) {
        }

        default void readExt(Reads read, byte revision) {
        }
    }
}
