package unity.world.modules;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import unity.world.graph.HeatGraph;
import unity.world.graphs.GraphHeat;
import unity.world.meta.GraphType;

public class GraphHeatModule extends GraphModule<GraphHeat, GraphHeatModule, HeatGraph> {
    public float heat;
    public float heatBuffer;

    void applySaveState(HeatGraph graph, int index) {
    }

    void updateExtension() {
    }

    void updateProps(HeatGraph graph, int index) {
        float temp = this.getTemp();
        float cond = ((GraphHeat)this.graph).baseHeatConductivity;
        this.heatBuffer = 0.0F;
        float clampedDelta = Mathf.clamp(Time.delta, 0.0F, 1.0F / cond);

        GraphHeatModule n;
        for(ObjectMap.Keys var6 = this.neighbours.keys().iterator(); var6.hasNext(); this.heatBuffer += (n.getTemp() - temp) * cond * clampedDelta) {
            n = (GraphHeatModule)var6.next();
        }

        this.heatBuffer += (293.15F - temp) * ((GraphHeat)this.graph).baseHeatRadiativity * clampedDelta;
    }

    void proximityUpdateCustom() {
    }

    void display(Table table) {
        if (this.networks.get(0) != null) {
            String ps = Core.bundle.get("stat.unity.tempunit");
            table.row();
            table.table((sub) -> {
                sub.clearChildren();
                sub.left();
                sub.label(() -> Strings.fixed(this.getTemp() - 273.15F, 2) + ps).color(Color.lightGray);
            }).left();
        }
    }

    void initStats() {
        this.setTemp(293.15F);
    }

    void displayBars(Table table) {
    }

    HeatGraph newNetwork() {
        return new HeatGraph();
    }

    void writeGlobal(Writes write) {
        write.f(this.heat);
    }

    void readGlobal(Reads reads, byte revision) {
        this.heat = reads.f();
        this.heatBuffer = 0.0F;
    }

    void writeLocal(Writes write, HeatGraph graph) {
    }

    Object[] readLocal(Reads read, byte revision) {
        return null;
    }

    public GraphType type() {
        return GraphType.heat;
    }

    public float getTemp() {
        return this.heat / ((GraphHeat)this.graph).baseHeatCapacity;
    }

    void setTemp(float t) {
        this.heat = t * ((GraphHeat)this.graph).baseHeatCapacity;
    }
}
