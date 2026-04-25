package unity.world.graphs;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.graphics.Pal;
import unity.world.meta.GraphType;
import unity.world.modules.GraphHeatModule;

public class GraphHeat extends Graph {
    public final float baseHeatCapacity;
    public final float baseHeatConductivity;
    public final float baseHeatRadiativity;

    public GraphHeat(float capacity, float conductivity, float radiativity) {
        this.baseHeatCapacity = capacity;
        this.baseHeatConductivity = conductivity;
        this.baseHeatRadiativity = radiativity;
    }

    public GraphHeat() {
        this(10.0F, 0.5F, 0.01F);
    }

    public void setStats(Table table) {
        table.row().left();
        table.add("Heat system").color(Pal.accent).fillX().row();
        table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.heatcapacity") + ":[] ").left();
        table.add(this.baseHeatCapacity + "K J/K").row();
        table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.heatconductivity") + ":[] ").left();
        table.add(this.baseHeatConductivity + "W/mK").row();
        table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.heatradiativity") + ":[] ").left();
        table.add(this.baseHeatRadiativity * 1000.0F + "W/K");
        this.setStatsExt(table);
    }

    public void setStatsExt(Table table) {
    }

    void drawPlace(int x, int y, int size, int rotation, boolean valid) {
    }

    public GraphType type() {
        return GraphType.heat;
    }

    public GraphHeatModule module() {
        return (GraphHeatModule)(new GraphHeatModule()).graph(this);
    }

    boolean canBeMulti() {
        return false;
    }
}
