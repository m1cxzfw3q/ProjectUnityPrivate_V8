package unity.world.graphs;

import arc.Core;
import arc.scene.ui.layout.Table;
import unity.world.meta.GraphType;
import unity.world.modules.GraphFluxModule;
import unity.world.modules.GraphModule;

public class GraphFlux extends Graph {
    public final float baseFlux;
    public final boolean fluxProducer;

    public GraphFlux(float flux, boolean producer) {
        this.baseFlux = flux;
        this.fluxProducer = producer;
    }

    public GraphFlux(float flux) {
        this(flux, true);
    }

    public GraphFlux(boolean producer) {
        this(0.0F, producer);
    }

    public GraphFlux() {
        this(0.0F, true);
    }

    public void setStats(Table table) {
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.flux") + ":[] ").left();
        table.add(this.baseFlux + "Wb");
    }

    public void setStatsExt(Table table) {
    }

    void drawPlace(int x, int y, int size, int rotation, boolean valid) {
    }

    public GraphType type() {
        return GraphType.flux;
    }

    public GraphModule module() {
        return (new GraphFluxModule()).graph(this);
    }

    boolean canBeMulti() {
        return false;
    }
}
