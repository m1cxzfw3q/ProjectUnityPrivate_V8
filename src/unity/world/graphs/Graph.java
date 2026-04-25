package unity.world.graphs;

import arc.scene.ui.layout.Table;
import unity.world.meta.GraphType;
import unity.world.modules.GraphModule;

public abstract class Graph {
    public boolean isMultiConnector;
    public int[] accept;

    public Graph setAccept(int... newAccept) {
        this.accept = newAccept;
        return this;
    }

    public Graph multi() {
        this.isMultiConnector = this.canBeMulti();
        return this;
    }

    public abstract void setStats(Table var1);

    public abstract void setStatsExt(Table var1);

    abstract void drawPlace(int var1, int var2, int var3, int var4, boolean var5);

    public abstract GraphType type();

    public abstract GraphModule module();

    abstract boolean canBeMulti();
}
