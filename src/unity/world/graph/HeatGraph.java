package unity.world.graph;

import unity.world.modules.GraphHeatModule;

public class HeatGraph extends BaseGraph<GraphHeatModule, HeatGraph> {
    float lastHeatFlow;

    public HeatGraph create() {
        return new HeatGraph();
    }

    void copyGraphStatsFrom(HeatGraph graph) {
    }

    void updateOnGraphChanged() {
    }

    void updateGraph() {
        this.lastHeatFlow = 0.0F;
        this.connected.each((module) -> {
            module.heat += module.heatBuffer;
            this.lastHeatFlow += module.heatBuffer;
        });
    }

    void updateDirect() {
    }

    void addMergeStats(GraphHeatModule module) {
    }

    void mergeStats(HeatGraph graph) {
        this.lastHeatFlow += graph.lastHeatFlow;
    }
}
