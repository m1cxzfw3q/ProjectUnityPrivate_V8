package unity.world.modules;

import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.struct.IntFloatMap;
import arc.struct.IntMap;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.meta.StatUnit;
import unity.world.graph.TorqueGraph;
import unity.world.graphs.GraphTorque;
import unity.world.meta.GraphType;

public class GraphTorqueModule<T extends GraphTorque> extends GraphModule<T, GraphTorqueModule<T>, TorqueGraph<T>> {
    static final Color[] pals;
    public float force;
    public float inertia;
    final IntFloatMap rots = new IntFloatMap(4);
    float friction;

    void applySaveState(TorqueGraph<T> graph, int index) {
        graph.lastVelocity = Math.max(graph.lastVelocity, ((Float[])this.saveCache.get(index))[0]);
    }

    void updateExtension() {
    }

    void updateProps(TorqueGraph<T> graph, int index) {
        float rot = this.rots.get(index, 0.0F);
        rot += graph.lastVelocity;
        rot %= 8640.0F;
        this.rots.put(index, rot);
    }

    void proximityUpdateCustom() {
    }

    void display(Table table) {
        TorqueGraph<T> net = (TorqueGraph)this.networks.get(0);
        if (!this.multi && net != null) {
            String ps = " " + StatUnit.perSecond.localized();
            table.row();
            table.table((sub) -> {
                sub.clearChildren();
                sub.left();
                sub.label(() -> Strings.fixed(net.lastVelocity / 6.0F, 2) + "r" + ps).color(Color.lightGray);
            }).left();
        }
    }

    void initStats() {
        this.friction = ((GraphTorque)this.graph).baseFriction;
        this.setInertia(((GraphTorque)this.graph).baseInertia);
    }

    void displayBars(Table table) {
    }

    void drawSelect() {
        for(IntMap.Entry<TorqueGraph<T>> graph : this.networks) {
            ((TorqueGraph)graph.value).connected.each((module) -> Drawf.selected((Building)module.parent.build.self(), pals[graph.key]));
        }

    }

    TorqueGraph<T> newNetwork() {
        return new TorqueGraph();
    }

    void writeGlobal(Writes write) {
        write.f(this.force);
        write.f(this.inertia);
        write.f(this.friction);
    }

    void readGlobal(Reads read, byte revision) {
        this.force = read.f();
        this.inertia = read.f();
        this.friction = read.f();
    }

    void writeLocal(Writes write, TorqueGraph<T> graph) {
        write.f(graph.lastVelocity);
    }

    Float[] readLocal(Reads read, byte revision) {
        return new Float[]{read.f()};
    }

    public GraphTorqueModule<T> graph(GraphTorque graph) {
        this.graph = graph;
        if (graph.isMultiConnector) {
            this.multi = true;
        }

        return this;
    }

    public GraphType type() {
        return GraphType.torque;
    }

    public void setInertia(float iner) {
        float diff = iner - this.inertia;
        if (diff != 0.0F) {
            if (this.multi) {
                for(TorqueGraph<T> i : this.networks.values()) {
                    i.injectInertia(diff);
                }
            } else {
                ((TorqueGraph)this.networks.get(0)).injectInertia(diff);
            }
        }

        this.inertia = iner;
    }

    public float getRotation() {
        return this.rots.get(0, 0.0F);
    }

    public float getRotationOf(int index) {
        return this.rots.get(index, 0.0F);
    }

    public float friction() {
        return this.friction;
    }

    public void setMotorForceMult(float a) {
    }

    static {
        pals = new Color[]{Pal.accent, Pal.redSpark, Pal.plasticSmoke, Pal.lancerLaser};
    }
}
