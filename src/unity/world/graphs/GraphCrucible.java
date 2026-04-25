package unity.world.graphs;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.graphics.Pal;
import unity.world.meta.GraphType;
import unity.world.modules.GraphCrucibleModule;

public class GraphCrucible extends Graph {
    public final float baseLiquidCapcity;
    public final float meltSpeed;
    public final boolean doesCrafting;

    public GraphCrucible(float capcity, float speed, boolean crafting) {
        this.baseLiquidCapcity = capcity;
        this.meltSpeed = speed;
        this.doesCrafting = crafting;
    }

    public GraphCrucible(float capacity, boolean crafting) {
        this(capacity, 0.8F, crafting);
    }

    public GraphCrucible() {
        this(6.0F, 0.8F, true);
    }

    public void setStats(Table table) {
        table.row().left();
        table.add("Crucible system").color(Pal.accent).fillX();
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.liquidcapacity") + ":[] ").left();
        table.add(this.baseLiquidCapcity + " Units");
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.meltspeed") + ":[] ").left();
        this.setStatsExt(table);
    }

    public void setStatsExt(Table table) {
    }

    void drawPlace(int x, int y, int size, int rotation, boolean valid) {
    }

    public GraphType type() {
        return GraphType.crucible;
    }

    public GraphCrucibleModule module() {
        return (GraphCrucibleModule)(new GraphCrucibleModule()).graph(this);
    }

    boolean canBeMulti() {
        return true;
    }
}
