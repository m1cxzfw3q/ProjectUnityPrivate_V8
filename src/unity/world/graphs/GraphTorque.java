package unity.world.graphs;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import mindustry.graphics.Pal;
import unity.world.meta.GraphData;
import unity.world.meta.GraphType;
import unity.world.modules.GraphTorqueModule;

public class GraphTorque extends Graph {
    public final float baseFriction;
    public final float baseInertia;

    public GraphTorque(float friction, float inertia) {
        this.baseFriction = friction;
        this.baseInertia = inertia;
    }

    public GraphTorque() {
        this(0.1F, 10.0F);
    }

    public void setStats(Table table) {
        table.row().left();
        table.add("Torque system").color(Pal.accent).fillX();
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.friction") + ":[] ").left();
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.inertia") + ":[] ").left();
        table.add(this.baseInertia + "t m^2");
        this.setStatsExt(table);
    }

    public void setStatsExt(Table table) {
    }

    void drawPlace(int x, int y, int size, int rotation, boolean valid) {
        int i = 0;

        for(int len = this.accept.length; i < len; ++i) {
            if (this.accept[i] != 0) {
                Lines.stroke(3.5F, Color.white);
                GraphData outPos = GraphData.getConnectSidePos(i, size, rotation);
                int dx = (outPos.toPos.x + x) * 8;
                int dy = (outPos.toPos.y + y) * 8;
                Point2 dir = Geometry.d4(outPos.dir);
                Lines.line((float)(dx - dir.x), (float)(dy - dir.y), (float)(dx - dir.x * 2), (float)(dy - dir.y * 2));
            }
        }

    }

    public GraphType type() {
        return GraphType.torque;
    }

    public GraphTorqueModule module() {
        return (new GraphTorqueModule()).graph(this);
    }

    boolean canBeMulti() {
        return true;
    }
}
