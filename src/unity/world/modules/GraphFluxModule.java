package unity.world.modules;

import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import unity.world.graph.FluxGraph;
import unity.world.graphs.GraphFlux;
import unity.world.meta.GraphType;

public class GraphFluxModule extends GraphModule<GraphFlux, GraphFluxModule, FluxGraph> {
    float flux;

    void applySaveState(FluxGraph graph, int index) {
    }

    void updateExtension() {
    }

    void updateProps(FluxGraph graph, int index) {
    }

    void proximityUpdateCustom() {
    }

    void display(Table table) {
        FluxGraph net = (FluxGraph)this.networks.get(0);
        if (net != null) {
            String ps = " Wb";
            table.row();
            table.table((sub) -> {
                sub.clearChildren();
                sub.left();
                sub.label(() -> Strings.fixed(net.flux(), 2) + ps).color(Color.lightGray);
            }).left();
        }
    }

    void initStats() {
        this.flux = ((GraphFlux)this.graph).baseFlux;
    }

    void displayBars(Table table) {
    }

    FluxGraph newNetwork() {
        return new FluxGraph();
    }

    void writeGlobal(Writes write) {
    }

    void readGlobal(Reads read, byte revision) {
    }

    void writeLocal(Writes write, FluxGraph graph) {
    }

    Object[] readLocal(Reads read, byte revision) {
        return null;
    }

    public GraphType type() {
        return GraphType.flux;
    }

    public float flux() {
        return this.flux;
    }

    public void mulFlux(float mul) {
        this.flux = mul * ((GraphFlux)this.graph).baseFlux;
    }
}
