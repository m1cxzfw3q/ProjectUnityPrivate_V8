package unity.world.graph;

import arc.struct.OrderedSet;
import arc.util.Time;
import unity.world.graphs.GraphTorque;
import unity.world.modules.GraphTorqueModule;

public class TorqueGraph<T extends GraphTorque> extends BaseGraph<GraphTorqueModule<T>, TorqueGraph<T>> {
    public float lastInertia;
    public float lastGrossForceApplied;
    public float lastNetForceApplied;
    public float lastVelocity;
    public float lastFrictionCoefficient;

    public TorqueGraph<T> create() {
        return new TorqueGraph<T>();
    }

    void copyGraphStatsFrom(TorqueGraph<T> graph) {
        this.lastVelocity = graph.lastVelocity;
    }

    boolean canConnect(GraphTorqueModule<T> b1, GraphTorqueModule<T> b2) {
        return b1.parent.build.team() == b2.parent.build.team();
    }

    void updateOnGraphChanged() {
    }

    void updateGraph() {
        float netForce = this.lastGrossForceApplied - this.lastFrictionCoefficient;
        this.lastNetForceApplied = netForce;
        float acceleration = this.lastInertia == 0.0F ? 0.0F : netForce / this.lastInertia;
        this.lastVelocity += acceleration * Time.delta;
        this.lastVelocity = Math.max(0.0F, this.lastVelocity);
    }

    void updateDirect() {
        float forceApply = 0.0F;
        float fricCoeff = 0.0F;
        float iner = 0.0F;

        GraphTorqueModule<T> module;
        for(OrderedSet.OrderedSetIterator var4 = this.connected.iterator(); var4.hasNext(); iner += module.inertia) {
            module = (GraphTorqueModule)var4.next();
            forceApply += module.force;
            fricCoeff += module.friction();
        }

        this.lastFrictionCoefficient = fricCoeff;
        this.lastGrossForceApplied = forceApply;
        this.lastInertia = iner;
    }

    void addMergeStats(GraphTorqueModule<T> module) {
    }

    void mergeStats(TorqueGraph<T> graph) {
        float momentumA = this.lastVelocity * this.lastInertia;
        float mementumB = graph.lastVelocity * graph.lastInertia;
        this.lastVelocity = (momentumA + mementumB) / (this.lastInertia + graph.lastInertia);
    }

    public void injectInertia(float iner) {
        float inerSum = this.lastInertia + iner;
        this.lastVelocity *= inerSum == 0.0F ? 0.0F : this.lastInertia / inerSum;
    }
}
