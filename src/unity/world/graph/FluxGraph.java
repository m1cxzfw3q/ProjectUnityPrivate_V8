package unity.world.graph;

import arc.struct.OrderedSet;
import unity.world.graphs.GraphFlux;
import unity.world.modules.GraphFluxModule;

public class FluxGraph extends BaseGraph<GraphFluxModule, FluxGraph> {
    float flux;
    float fluxTotal;

    public FluxGraph create() {
        return new FluxGraph();
    }

    void copyGraphStatsFrom(FluxGraph graph) {
    }

    void updateOnGraphChanged() {
    }

    void updateGraph() {
        this.fluxTotal = 0.0F;
        int totalMags = 0;
        OrderedSet.OrderedSetIterator var2 = this.connected.iterator();

        while(var2.hasNext()) {
            GraphFluxModule module = (GraphFluxModule)var2.next();
            this.fluxTotal += module.flux();
            if (((GraphFlux)module.graph).fluxProducer) {
                ++totalMags;
            }
        }

        float weight = 1.0F;
        if (totalMags > 1) {
            weight = (float)((double)1.5F * (double)totalMags / (Math.log10((double)totalMags) + (double)1.0F) - (double)0.5F);
        }

        this.flux = this.fluxTotal / weight;
    }

    void updateDirect() {
    }

    void addMergeStats(GraphFluxModule module) {
    }

    void mergeStats(FluxGraph graph) {
    }

    public float flux() {
        return this.flux;
    }
}
