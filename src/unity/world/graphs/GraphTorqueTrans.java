package unity.world.graphs;

import arc.Core;
import arc.scene.ui.layout.Table;
import unity.world.modules.GraphTorqueTransModule;

public class GraphTorqueTrans extends GraphTorque {
    public final float[] ratio = new float[]{1.0F, 2.0F};

    public GraphTorqueTrans(float friction, float inertia) {
        super(friction, inertia);
        this.multi();
    }

    public GraphTorqueTrans setRatio(float ratio1, float ratio2) {
        this.ratio[0] = ratio1;
        this.ratio[1] = ratio2;
        return this;
    }

    public void setStatsExt(Table table) {
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.transratio") + ":[] ").left();
        String ratio = this.ratio[0] + ":" + this.ratio[1];
        table.add(ratio);
    }

    public GraphTorqueTransModule module() {
        return (new GraphTorqueTransModule()).graph(this);
    }
}
